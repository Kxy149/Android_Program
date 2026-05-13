package com.example.myapplication3;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.FileOutputOptions;
import androidx.camera.video.PendingRecording;
import androidx.camera.video.Quality;
import androidx.camera.video.QualitySelector;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String[] REQUIRED_PERMISSIONS;
    static {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        } else {
            REQUIRED_PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
            };
        }
    }

    private PreviewView previewView;
    private ImageButton btnCapture;
    private ImageButton btnVideo;
    private TextView tvLuminance;

    private ImageCapture imageCapture;
    private VideoCapture<Recorder> videoCapture;
    private Recording currentRecording;
    private boolean isRecording = false;

    private ExecutorService cameraExecutor;
    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        btnCapture = findViewById(R.id.btnCapture);
        btnVideo = findViewById(R.id.btnVideo);
        tvLuminance = findViewById(R.id.tvLuminance);

        cameraExecutor = Executors.newSingleThreadExecutor();

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean allGranted = true;
                    for (Boolean granted : permissions.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        startCamera();
                    } else {
                        Toast.makeText(this, "应用需要相机和麦克风权限才能运行",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        btnCapture.setOnClickListener(v -> takePhoto());
        btnVideo.setOnClickListener(v -> captureVideo());

        checkPermissions();
    }

    private void checkPermissions() {
        for (String perm : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(REQUIRED_PERMISSIONS);
                return;
            }
        }
        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview 用例
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // ImageCapture 用例
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();

                // VideoCapture 用例
                Recorder recorder = new Recorder.Builder()
                        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                        .build();
                videoCapture = VideoCapture.withOutput(recorder);

                // ImageAnalysis 用例 —— 计算图像平均亮度
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor,
                        new LuminanceAnalyzer(luminance -> runOnUiThread(() ->
                                tvLuminance.setText(String.format(
                                        Locale.getDefault(), "亮度: %.2f", luminance)))));

                // 选择后置摄像头
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();

                // 将所有用例绑定到生命周期
                cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture,
                        videoCapture, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException("相机初始化失败", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_" + timestamp);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/CameraXApp");

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues).build();

        imageCapture.takePicture(outputOptions, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(
                            @NonNull ImageCapture.OutputFileResults output) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                "照片已保存: IMG_" + timestamp + ".jpg",
                                Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                "拍照失败: " + exception.getMessage(),
                                Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void captureVideo() {
        if (videoCapture == null) return;

        if (isRecording) {
            // 停止录制
            if (currentRecording != null) {
                currentRecording.stop();
                currentRecording = null;
            }
            isRecording = false;
            btnVideo.setImageResource(android.R.drawable.ic_media_play);
            Toast.makeText(this, "录制已停止", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());

        // 创建临时视频文件
        File outputDir = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                "CameraXApp");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File videoFile = new File(outputDir, "VID_" + timestamp + ".mp4");

        FileOutputOptions outputOptions = new FileOutputOptions.Builder(videoFile).build();

        PendingRecording pendingRecording = videoCapture.getOutput()
                .prepareRecording(this, outputOptions)
                .withAudioEnabled();

        currentRecording = pendingRecording.start(cameraExecutor, recordEvent -> {
            if (recordEvent instanceof VideoRecordEvent.Finalize) {
                VideoRecordEvent.Finalize finalizeEvent =
                        (VideoRecordEvent.Finalize) recordEvent;
                if (finalizeEvent.hasError()) {
                    currentRecording.close();
                    currentRecording = null;
                    runOnUiThread(() -> Toast.makeText(MainActivity.this,
                            "录制出错: " + finalizeEvent.getError(),
                            Toast.LENGTH_SHORT).show());
                } else {
                    // 将文件保存到 MediaStore
                    saveVideoToMediaStore(videoFile, timestamp);
                }
            }
        });

        isRecording = true;
        btnVideo.setImageResource(android.R.drawable.ic_media_pause);
        Toast.makeText(this, "开始录制", Toast.LENGTH_SHORT).show();
    }

    private void saveVideoToMediaStore(File videoFile, String timestamp) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, "VID_" + timestamp);
            contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            contentValues.put(MediaStore.Video.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_MOVIES + "/CameraXApp");

            android.net.Uri uri = getContentResolver().insert(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);

            if (uri != null) {
                try (OutputStream out = getContentResolver().openOutputStream(uri);
                     FileInputStream in = new FileInputStream(videoFile)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.flush();
                }

                // 删除临时文件
                videoFile.delete();

                runOnUiThread(() -> Toast.makeText(MainActivity.this,
                        "视频已保存: VID_" + timestamp + ".mp4",
                        Toast.LENGTH_SHORT).show());
            }
        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this,
                    "视频保存失败: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    /**
     * 图像分析器：计算相机帧中Y平面的平均亮度
     */
    private static class LuminanceAnalyzer implements ImageAnalysis.Analyzer {

        interface LuminanceCallback {
            void onLuminanceCalculated(double luminance);
        }

        private final LuminanceCallback callback;

        LuminanceAnalyzer(LuminanceCallback callback) {
            this.callback = callback;
        }

        @Override
        public void analyze(@NonNull ImageProxy image) {
            ImageProxy.PlaneProxy yPlane = image.getPlanes()[0];
            ByteBuffer buffer = yPlane.getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            long sum = 0;
            for (byte b : data) {
                sum += b & 0xFF;
            }
            double avgLuminance = (double) sum / data.length;

            callback.onLuminanceCalculated(avgLuminance);
            image.close();
        }
    }
}
