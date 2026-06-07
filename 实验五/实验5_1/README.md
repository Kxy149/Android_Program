# 实验五 5_1：TensorFlow 花卉图片分类器训练与 Android 验证

## 一、实验目的

本实验根据 CSDN 文章《TensorFlow花卉图片分类器模型训练》的要求，使用 TensorFlow/Keras 训练一个花卉图片分类模型，并将模型转换为 TensorFlow Lite 格式。随后使用实验四的 Android 应用 `TFLClassify` 验证生成的 `.tflite` 模型可以在移动端工程中加载和推理。

参考网页：

https://blog.csdn.net/llfjfz/article/details/161630612

## 二、实验环境

- 操作系统：Windows 11
- Python：Codex 内置 Python 3.12 虚拟环境
- TensorFlow：2.20.0
- Android 工程：实验四 `TFLClassify`
- Gradle：7.5
- JDK：本地临时使用 Temurin JDK 17 构建 Android 工程

说明：本机系统默认 JDK 为 24，旧版 Android Gradle Plugin、Kotlin kapt 和 D8 与 JDK 24 存在兼容问题，因此实验目录下临时下载并使用 `.jdk17` 完成构建验证。

## 三、实验内容

本实验完成了以下任务：

1. 下载并读取 TensorFlow 官方花卉数据集 `flower_photos`。
2. 使用 MobileNetV2 作为特征提取网络进行迁移学习。
3. 训练 5 分类花卉图片分类模型。
4. 导出 Keras 模型文件 `flower_classifier.keras`。
5. 转换并导出 TensorFlow Lite 模型文件 `model.tflite`。
6. 生成标签文件 `labels.txt`。
7. 将生成的 TFLite 模型放入实验四 `TFLClassify` 应用。
8. 修改 Android 推理代码，使用 `Interpreter` 加载并验证模型。
9. 构建 `start-debug.apk`，完成 Android 工程验证。

## 四、目录结构

```text
实验5_1/
├── README.md
├── train_flower_classifier.py
├── model_output/
│   ├── flower_classifier.keras
│   ├── labels.txt
│   └── model.tflite
└── TFLClassify/
    └── start/
        ├── src/main/assets/FlowerModel.tflite
        ├── src/main/java/.../MainActivity.kt
        └── build/outputs/apk/debug/start-debug.apk
```

## 五、模型训练与导出

训练脚本为：

```text
train_flower_classifier.py
```

执行命令：

```powershell
.\.venv-tf\Scripts\python.exe train_flower_classifier.py --epochs 1 --batch-size 32
```

脚本主要流程：

1. 下载 `flower_photos.tgz` 数据集。
2. 按 8:2 划分训练集和验证集。
3. 将图片统一调整为 `224 x 224`。
4. 使用 MobileNetV2 预训练权重提取特征。
5. 添加全局池化、Dropout 和 Softmax 分类层。
6. 训练模型并保存 `.keras` 文件。
7. 使用 `TFLiteConverter` 转换为 `.tflite` 文件。
8. 使用 `tf.lite.Interpreter` 做一次推理冒烟测试。

本次训练结果：

```text
val_accuracy: 0.8147
Smoke test: sunflowers (0.7812)
```

## 六、生成文件

训练后生成的主要文件如下：

```text
model_output/flower_classifier.keras
model_output/model.tflite
model_output/labels.txt
```

标签文件内容：

```text
daisy
dandelion
roses
sunflowers
tulips
```

Android 工程中使用的模型文件为：

```text
TFLClassify/start/src/main/assets/FlowerModel.tflite
```

该文件与 `model_output/model.tflite` 内容一致。

## 七、Android 应用验证

实验四的 `TFLClassify` 原本使用 ML Model Binding 方式加载模型。由于本次按网页流程生成的是普通 TFLite 模型，未内嵌完整 ML Binding 元数据，因此改为使用 TensorFlow Lite `Interpreter` 直接加载模型。

主要修改内容：

1. 将模型复制到 `start/src/main/assets/FlowerModel.tflite`。
2. 在 `MainActivity.kt` 中使用 `Interpreter` 读取 assets 中的模型。
3. 使用 `TensorImage` 和 `ImageProcessor` 将摄像头图像调整为 `224 x 224`。
4. 调用 `interpreter.run()` 得到 5 类输出概率。
5. 将输出概率映射到标签：

```kotlin
private val FLOWER_LABELS = listOf("daisy", "dandelion", "roses", "sunflowers", "tulips")
```

Android 构建命令：

```powershell
$env:JAVA_HOME="C:\Users\ASUS\Documents\GitHub\Android_Program\实验五\实验5_1\.jdk17"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
cd TFLClassify
.\gradlew.bat :start:assembleDebug
```

构建结果：

```text
BUILD SUCCESSFUL
```

生成 APK：

```text
TFLClassify/start/build/outputs/apk/debug/start-debug.apk
```

## 八、实验结论

本实验成功完成了从花卉图片数据集训练模型、转换 TFLite 模型，到 Android 应用集成验证的完整流程。生成的模型可以通过 TensorFlow Lite Interpreter 正常推理，并已在实验四 `TFLClassify` 工程中完成构建验证。

通过本实验掌握了：

1. 使用 Keras 和 MobileNetV2 进行迁移学习训练图片分类模型。
2. 将 Keras 模型转换为 TensorFlow Lite 模型。
3. 在 Android 工程中加载 assets 目录下的 `.tflite` 文件。
4. 使用 TFLite Interpreter 完成移动端图片分类推理。
