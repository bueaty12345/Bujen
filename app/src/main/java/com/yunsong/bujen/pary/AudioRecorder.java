package com.yunsong.bujen.pary;

import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder recorder;
    private String outputFile;
    private boolean isRecording = false;
    private boolean isPaused = false;

    private VisualizerView visualizerView;
    private Handler handler = new Handler();

    public void setVisualizerView(VisualizerView view) {
        this.visualizerView = view;
    }

    private Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (recorder != null && isRecording) {
                int amplitude = recorder.getMaxAmplitude();
                if (visualizerView != null) {
                    visualizerView.updateVolume(amplitude);
                }
                handler.postDelayed(this, 50); // 提高更新频率，让音波更流畅
            }
        }
    };


    public String startRecording() {
        if (isRecording) {
            Log.e("AudioRecorder", "录音已在进行中");
            return null; // 避免重复启动
        }

        // ✅ **修正路径**，适配 Android 10+
        File storageDir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "MyRecordings");
        } else {
            storageDir = new File(Environment.getExternalStorageDirectory(), "MyRecordings");
        }

        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("AudioRecorder", "无法创建录音存储目录！");
            return null;
        }

        outputFile = new File(storageDir, "record_" + System.currentTimeMillis() + ".mp3").getAbsolutePath();
        Log.d("AudioRecorder", "录音文件路径：" + outputFile);

        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(outputFile);
//            recorder.setMaxDuration(60000); // 设置最大时长（毫秒）
//
//            recorder.setOnInfoListener((mr, what, extra) -> {
//                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
//                    Log.d("AudioRecorder", "达到最大录音时间，自动停止...");
//                    stopRecording();
//                }
//            });

            recorder.prepare();
            recorder.start();
            isRecording = true;
            Log.d("AudioRecorder", "录音开始...");
            handler.post(updateVisualizer);

        } catch (IOException e) {
            Log.e("AudioRecorder", "录音启动失败: " + e.getMessage());
            isRecording = false;
            return null;
        }
        return outputFile;
    }

    public void stopRecording() {
        if (recorder != null && isRecording) {
            try {
                recorder.stop();
                Log.d("AudioRecorder", "录音停止...");
                handler.removeCallbacks(updateVisualizer);
            } catch (IllegalStateException e) {
                Log.e("AudioRecorder", "录音停止失败: " + e.getMessage());
            } finally {
                recorder.release();
                recorder = null;
                isRecording = false;
            }
        } else {
            Log.e("AudioRecorder", "无法停止录音，状态错误！");
        }
    }
    // 暂停录音 (仅 Android 7.0+ 支持)
    public void pauseRecording() {
        if (recorder != null && isRecording && !isPaused) {
            try {
                recorder.pause();
                isPaused = true;
                Log.d("AudioRecorder", "录音暂停...");
            } catch (IllegalStateException e) {
                Log.e("AudioRecorder", "暂停失败: " + e.getMessage());
            }
        }
    }

    // 恢复录音 (仅 Android 7.0+ 支持)
    public void resumeRecording() {
        if (recorder != null && isRecording && isPaused) {
            try {
                recorder.resume();
                isPaused = false;
                Log.d("AudioRecorder", "录音继续...");
            } catch (IllegalStateException e) {
                Log.e("AudioRecorder", "恢复失败: " + e.getMessage());
            }
        }
    }

    // 取消录音 (不保存文件)
    public void cancelRecording() {
        if (recorder != null && isRecording) {
            try {
                recorder.stop();
            } catch (IllegalStateException e) {
                Log.e("AudioRecorder", "取消失败: " + e.getMessage());
            } finally {
                recorder.release();
                recorder = null;
                isRecording = false;
                isPaused = false;

                // 删除录音文件
                File file = new File(outputFile);
                if (file.exists()) {
                    file.delete();
                    Log.d("AudioRecorder", "录音已取消，文件删除");
                }
            }
        } else {
            Log.e("AudioRecorder", "无法取消录音，状态错误！");
        }
    }
    public boolean isRecording() {
        return isRecording;
    }

    public String getRecordedFilePath() {
        return outputFile;
    }
}
