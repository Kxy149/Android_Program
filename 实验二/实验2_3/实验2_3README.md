# CameraX 相机应用

基于 Android CameraX Jetpack 库构建的功能完整、兼容性良好的相机应用程序。

## 功能模块

| 模块 | 说明 |
|------|------|
| **相机预览 (Preview)** | 通过 `PreviewView` 实时显示相机画面，绑定到 Activity 生命周期，自动管理相机资源 |
| **静态拍照 (ImageCapture)** | 捕获高分辨率 JPEG 图像，自动处理对焦与曝光，以时间戳命名保存至系统媒体库 |
| **视频录制 (VideoCapture)** | 支持开始/停止录制，含音频采集，MP4 格式输出并保存至系统媒体库 |
| **图像分析 (ImageAnalysis)** | 实时访问相机帧缓冲区，计算并显示 Y 平面平均亮度（Luminance），为二维码扫描、人脸检测等扩展提供基础框架 |
| **权限管理** | 动态申请相机、麦克风及存储（Android 9 及以下）权限，按 API 级别自适应 |

## 技术栈

- **语言**：Java
- **最低 SDK**：Android  5.0 (API 21)
- **目标 SDK**：Android  14 (API 36)
- **核心依赖**：
  - `androidx.camera:camera-core:1.3.4`
  - `androidx.camera:camera-camera2:1.3.4`
  - `androidx.camera:camera-lifecycle:1.3.4`
  - `androidx.camera:camera-view:1.3.4`
  - `androidx.camera:camera-video:1.3.4`

## 项目结构

```
CameraXApp/
├── app/
│   ├── build.gradle.kts          # 模块构建配置
│   └── src/main/
│       ├── AndroidManifest.xml    # 清单文件（权限、硬件特性声明）
│       ├── java/com/example/myapplication3/
│       │   └── MainActivity.java  # 主 Activity（核心逻辑）
│       └── res/
│           ├── drawable/          # 按钮背景 Drawable
│           ├── layout/
│           │   └── activity_main.xml  # 布局（PreviewView + 控件）
│           └── values/
│               └── strings.xml
├── build.gradle.kts              # 项目级构建配置
├── gradle/
│   └── libs.versions.toml        # 版本目录
└── settings.gradle.kts
```

## 架构说明

### 应用流程

```
onCreate()
  └─ checkPermissions()
       ├─ 已授权 → startCamera()
       └─ 未授权 → permissionLauncher.launch()
                     ├─ 同意 → startCamera()
                     └─ 拒绝 → Toast + finish()

startCamera()
  └─ ProcessCameraProvider.getInstance()
       └─ 配置用例:
            ├─ Preview       → previewView.getSurfaceProvider()
            ├─ ImageCapture  → 高质量模式
            ├─ VideoCapture  → Recorder + 最高画质
            └─ ImageAnalysis → LuminanceAnalyzer（亮度分析）
       └─ bindToLifecycle()  → 所有用例绑定到生命周期

UI 事件:
  btnCapture.onClick  → takePhoto()   → ImageCapture.takePicture()
  btnVideo.onClick    → captureVideo() → Recording.start() / stop()

onDestroy()
  └─ cameraExecutor.shutdown()
```

### 关键设计

- **异步初始化**：通过 `ListenableFuture` + `addListener` 获取 `ProcessCameraProvider`，避免阻塞主线程
- **生命周期感知**：所有 CameraX 用例通过 `bindToLifecycle()` 绑定，随 Activity 生命周期自动启停
- **后置摄像头**：使用 `CameraSelector.DEFAULT_BACK_CAMERA` 选择后置摄像头
- **单线程执行器**：后台线程处理图像分析和文件 I/O，`onDestroy` 中安全关闭

## 构建与运行

### 前提条件

- Android Studio Hedgehog (2023.1.1) 或更高版本
- Android SDK 36
- Gradle 8.13+

### 构建步骤

```bash
# 克隆项目后进入目录
cd CameraXApp

# 调试构建
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

也可直接在 Android Studio 中打开项目，同步 Gradle 后点击 Run 运行。

## 使用的权限

| 权限 | 适用 API | 用途 |
|------|----------|------|
| `CAMERA` | 全部 | 相机预览与拍摄 |
| `RECORD_AUDIO` | 全部 | 视频录制音频采集 |
| `WRITE_EXTERNAL_STORAGE` | ≤28 | 照片/视频文件存储 |
| `READ_EXTERNAL_STORAGE` | ≤32 | 读取已存储的媒体文件 |

## 扩展方向

- 添加前后摄像头切换（`CameraSelector.DEFAULT_FRONT_CAMERA`）
- 集成二维码扫描（基于 `ImageAnalysis` 帧回调）
- 添加人脸检测 / 美颜滤镜
- 支持闪光灯 / 夜间模式控制
- 视频添加时间水印叠加
