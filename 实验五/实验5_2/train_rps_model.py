import argparse
from pathlib import Path

import tensorflow as tf
from tensorflow.keras.preprocessing.image import ImageDataGenerator


IMAGE_SIZE = (224, 224)
LABELS = ("paper", "rock", "scissors")


def build_model() -> tf.keras.Model:
    model = tf.keras.Sequential(
        [
            tf.keras.layers.Input(shape=(224, 224, 3)),
            tf.keras.layers.Conv2D(64, (3, 3), activation="relu"),
            tf.keras.layers.MaxPooling2D(2, 2),
            tf.keras.layers.Conv2D(64, (3, 3), activation="relu"),
            tf.keras.layers.MaxPooling2D(2, 2),
            tf.keras.layers.Conv2D(128, (3, 3), activation="relu"),
            tf.keras.layers.MaxPooling2D(2, 2),
            tf.keras.layers.Conv2D(128, (3, 3), activation="relu"),
            tf.keras.layers.MaxPooling2D(2, 2),
            tf.keras.layers.Flatten(),
            tf.keras.layers.Dropout(0.5),
            tf.keras.layers.Dense(512, activation="relu"),
            tf.keras.layers.Dense(len(LABELS), activation="softmax"),
        ]
    )
    model.compile(
        loss="categorical_crossentropy",
        optimizer=tf.keras.optimizers.RMSprop(learning_rate=0.001),
        metrics=["accuracy"],
    )
    return model


def make_generators(train_dir: Path, validation_dir: Path, batch_size: int):
    train_datagen = ImageDataGenerator(
        rescale=1.0 / 255,
        rotation_range=40,
        width_shift_range=0.2,
        height_shift_range=0.2,
        shear_range=0.2,
        zoom_range=0.2,
        horizontal_flip=True,
        fill_mode="nearest",
    )
    validation_datagen = ImageDataGenerator(rescale=1.0 / 255)

    train_generator = train_datagen.flow_from_directory(
        train_dir,
        target_size=IMAGE_SIZE,
        batch_size=batch_size,
        class_mode="categorical",
        classes=list(LABELS),
    )
    validation_generator = validation_datagen.flow_from_directory(
        validation_dir,
        target_size=IMAGE_SIZE,
        batch_size=batch_size,
        class_mode="categorical",
        classes=list(LABELS),
    )
    return train_generator, validation_generator


def export_tflite(model: tf.keras.Model, output_file: Path) -> None:
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    output_file.parent.mkdir(parents=True, exist_ok=True)
    output_file.write_bytes(tflite_model)


def main() -> None:
    parser = argparse.ArgumentParser(description="Train the rock-paper-scissors classifier.")
    parser.add_argument("--train-dir", type=Path, default=Path("rps/rps"))
    parser.add_argument("--validation-dir", type=Path, default=Path("rps-test-set/rps-test-set"))
    parser.add_argument("--epochs", type=int, default=20)
    parser.add_argument("--batch-size", type=int, default=126)
    parser.add_argument("--keras-output", type=Path, default=Path("rps_model.keras"))
    parser.add_argument(
        "--tflite-output",
        type=Path,
        default=Path("TFLClassify/start/src/main/assets/RpsModel.tflite"),
    )
    args = parser.parse_args()

    train_generator, validation_generator = make_generators(
        args.train_dir, args.validation_dir, args.batch_size
    )

    model = build_model()
    model.summary()
    model.fit(
        train_generator,
        epochs=args.epochs,
        validation_data=validation_generator,
        verbose=1,
    )

    model.save(args.keras_output)
    export_tflite(model, args.tflite_output)
    print(f"Saved Keras model to {args.keras_output}")
    print(f"Saved TFLite model to {args.tflite_output}")


if __name__ == "__main__":
    main()
