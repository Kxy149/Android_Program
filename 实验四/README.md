# 实验四：TensorFlow Lite 花朵识别应用

本项目是一个基于 TensorFlow Lite、Android Studio ML Model Binding 和 CameraX 的实时图像分类应用。应用会调用手机摄像头采集画面，并使用 `FlowerModel.tflite` 对画面中的花朵进行分类识别。

## 项目功能

- 使用 CameraX 打开摄像头并实时获取图像帧。
- 使用 TensorFlow Lite 模型进行端侧推理。
- 通过 Android Studio ML Model Binding 自动生成模型调用代码。
- 在界面中展示置信度最高的识别结果。
- 支持真机运行和调试。

## 项目结构

```text
TFLClassify/
├── start/                  # 实验实现模块，主要运行这个模块
│   └── src/main/
│       ├── java/           # CameraX、模型推理和结果展示代码
│       ├── ml/             # FlowerModel.tflite 模型文件
│       └── res/            # 布局、主题和字符串资源
├── finish/                 # 参考完成模块
├── build.gradle            # 根项目 Gradle 配置
├── settings.gradle         # 模块配置
└── README.md               # 项目说明
```

## 核心文件

- `start/src/main/ml/FlowerModel.tflite`：花朵分类 TensorFlow Lite 模型。
- `start/src/main/java/org/tensorflow/lite/examples/classification/MainActivity.kt`：主界面、摄像头预览、图像分析和模型推理入口。
- `start/src/main/java/org/tensorflow/lite/examples/classification/ui/RecognitionAdapter.kt`：识别结果列表适配器。
- `start/src/main/java/org/tensorflow/lite/examples/classification/viewmodel/RecognitionListViewModel.kt`：保存并刷新识别结果。
- `start/src/main/res/layout/activity_main.xml`：摄像头预览与结果列表界面。
- `start/build.gradle`：应用模块依赖配置，已启用 `mlModelBinding`。

## 环境要求

- Android Studio
- JDK 8 或更高版本
- Android SDK，项目当前 `compileSdk` 为 34
- 一台支持摄像头的 Android 真机或模拟器

## 运行方式

在 Android Studio 中打开 `TFLClassify` 项目，然后选择 `start` 模块运行。

也可以在命令行中进入项目目录后执行：

```powershell
.\gradlew.bat :start:assembleDebug
```

构建成功后，APK 位于：

```text
start/build/outputs/apk/debug/start-debug.apk
```

## 使用说明

1. 将应用安装到 Android 设备。
2. 首次启动时允许摄像头权限。
3. 将摄像头对准花朵图片或实物。
4. 应用会在底部列表中实时显示识别类别和置信度。

## 实验要点

本实验重点在于掌握 TensorFlow Lite 模型在 Android 端的集成流程，包括模型导入、ML Model Binding 自动生成接口、CameraX 图像流处理，以及将推理结果展示到 RecyclerView 中。

## 参考资料

- [TensorFlow Lite Image Classification](https://www.tensorflow.org/lite/examples/image_classification/overview)
- [CameraX 官方文档](https://developer.android.com/media/camera/camerax)
- [Android Studio ML Model Binding](https://developer.android.com/studio/preview/features#tensor-flow-lite-models)
