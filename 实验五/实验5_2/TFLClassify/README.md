# 石头剪刀布 TensorFlow Lite 图像分类器

本项目按实验教程完成了石头、剪刀、布图像分类模型训练，并将模型接入 Android CameraX 应用中，实现实时相机识别。

## 项目内容

- `../train_rps_model.py`：使用本地 `rps/rps` 训练集和 `rps-test-set/rps-test-set` 验证集训练 CNN 模型。
- `../rps_model.keras`：训练后保存的 Keras 模型。
- `start/src/main/assets/RpsModel.tflite`：Android 应用实际加载的 TensorFlow Lite 模型。
- `start`：Android 相机识别应用，可将摄像头画面分类为 `paper`、`rock`、`scissors`。

## 重新训练模型

先在 Python 环境中安装 TensorFlow，然后在实验根目录运行：

```powershell
python train_rps_model.py --epochs 20 --batch-size 126
```

脚本会导出模型到：

```text
TFLClassify/start/src/main/assets/RpsModel.tflite
```

## 构建 Android 应用

在当前 `TFLClassify` 目录运行：

```powershell
.\gradlew.bat :start:assembleDebug
```

构建成功后，调试 APK 位于：

```text
start/build/outputs/apk/debug/start-debug.apk
```
