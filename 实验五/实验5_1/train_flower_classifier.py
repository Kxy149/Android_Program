import argparse
import pathlib
import shutil
import tarfile
import urllib.request

import numpy as np
import tensorflow as tf


DATA_URL = "https://storage.googleapis.com/download.tensorflow.org/example_images/flower_photos.tgz"
LABELS = ["daisy", "dandelion", "roses", "sunflowers", "tulips"]


def download_and_extract(data_dir: pathlib.Path) -> pathlib.Path:
    dataset_dir = data_dir / "flower_photos"
    if dataset_dir.exists():
        return dataset_dir

    data_dir.mkdir(parents=True, exist_ok=True)
    archive_path = data_dir / "flower_photos.tgz"
    if not archive_path.exists():
        print(f"Downloading {DATA_URL}")
        urllib.request.urlretrieve(DATA_URL, archive_path)

    print(f"Extracting {archive_path}")
    with tarfile.open(archive_path, "r:gz") as tar:
        tar.extractall(data_dir)
    return dataset_dir


def build_model(num_classes: int) -> tf.keras.Model:
    inputs = tf.keras.Input(shape=(224, 224, 3), name="image")
    x = tf.keras.layers.Rescaling(1.0 / 127.5, offset=-1, name="mobilenetv2_preprocess")(inputs)
    base_model = tf.keras.applications.MobileNetV2(
        input_shape=(224, 224, 3),
        include_top=False,
        weights="imagenet",
    )
    base_model.trainable = False
    x = base_model(x, training=False)
    x = tf.keras.layers.GlobalAveragePooling2D()(x)
    x = tf.keras.layers.Dropout(0.2)(x)
    outputs = tf.keras.layers.Dense(num_classes, activation="softmax", name="probability")(x)
    return tf.keras.Model(inputs, outputs)


def representative_dataset(dataset):
    for images, _ in dataset.take(100):
        for image in images[:1]:
            yield [tf.expand_dims(tf.cast(image, tf.float32), axis=0)]


def export_tflite(model: tf.keras.Model, output_path: pathlib.Path, rep_dataset) -> None:
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.representative_dataset = lambda: representative_dataset(rep_dataset)
    converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS]
    converter.inference_input_type = tf.float32
    converter.inference_output_type = tf.float32
    output_path.write_bytes(converter.convert())


def smoke_test(tflite_path: pathlib.Path, image_batch: np.ndarray, labels: list[str]) -> None:
    interpreter = tf.lite.Interpreter(model_path=str(tflite_path))
    interpreter.allocate_tensors()
    input_details = interpreter.get_input_details()[0]
    output_details = interpreter.get_output_details()[0]
    image = image_batch[:1].astype(input_details["dtype"])
    interpreter.set_tensor(input_details["index"], image)
    interpreter.invoke()
    scores = interpreter.get_tensor(output_details["index"])[0]
    top = int(np.argmax(scores))
    print(f"Smoke test: {labels[top]} ({scores[top]:.4f})")


def main() -> None:
    parser = argparse.ArgumentParser(description="Train and export a flower classifier for TFLClassify.")
    parser.add_argument("--epochs", type=int, default=5)
    parser.add_argument("--batch-size", type=int, default=32)
    parser.add_argument("--data-dir", type=pathlib.Path, default=pathlib.Path("data"))
    parser.add_argument("--out-dir", type=pathlib.Path, default=pathlib.Path("model_output"))
    parser.add_argument(
        "--android-model",
        type=pathlib.Path,
        default=pathlib.Path("TFLClassify/start/src/main/ml/FlowerModel.tflite"),
    )
    args = parser.parse_args()

    tf.keras.utils.set_random_seed(42)
    dataset_dir = download_and_extract(args.data_dir)
    train_ds = tf.keras.utils.image_dataset_from_directory(
        dataset_dir,
        validation_split=0.2,
        subset="training",
        seed=42,
        image_size=(224, 224),
        batch_size=args.batch_size,
        label_mode="categorical",
    )
    val_ds = tf.keras.utils.image_dataset_from_directory(
        dataset_dir,
        validation_split=0.2,
        subset="validation",
        seed=42,
        image_size=(224, 224),
        batch_size=args.batch_size,
        label_mode="categorical",
    )
    class_names = train_ds.class_names
    train_ds = train_ds.prefetch(tf.data.AUTOTUNE)
    val_ds = val_ds.prefetch(tf.data.AUTOTUNE)

    model = build_model(len(class_names))
    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
        loss="categorical_crossentropy",
        metrics=["accuracy"],
    )
    model.fit(train_ds, validation_data=val_ds, epochs=args.epochs)

    args.out_dir.mkdir(parents=True, exist_ok=True)
    keras_path = args.out_dir / "flower_classifier.keras"
    tflite_path = args.out_dir / "model.tflite"
    labels_path = args.out_dir / "labels.txt"

    model.save(keras_path)
    labels_path.write_text("\n".join(class_names) + "\n", encoding="utf-8")
    export_tflite(model, tflite_path, val_ds)

    if args.android_model:
        args.android_model.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(tflite_path, args.android_model)

    sample_batch, _ = next(iter(val_ds.unbatch().batch(1)))
    smoke_test(tflite_path, sample_batch.numpy(), class_names)
    print(f"Saved Keras model: {keras_path}")
    print(f"Saved TFLite model: {tflite_path}")
    print(f"Saved labels: {labels_path}")
    print(f"Copied Android model: {args.android_model}")


if __name__ == "__main__":
    main()
