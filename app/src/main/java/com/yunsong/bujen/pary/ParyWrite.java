package com.yunsong.bujen.pary;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yunsong.bujen.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

public class ParyWrite extends AppCompatActivity implements View.OnClickListener {
    private ImageView img_main, img_zp, img_xj,img_play,img_stop;
    private ImageView img_back,img_chehui,img_huifu,img_ok;
    private LinearLayout lin_write,lin_high;
    private TextView txt_cancel,txt_duration;
    private EditText edtxt_content,edtxt_title;

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int OPEN_GALLERY_REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_CAMERA = 2;
    private static final int REQUEST_TAKE_PHOTO = 4;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {android.Manifest.permission.RECORD_AUDIO};
    private boolean permissionToRecordAccepted = false;

    private AudioRecorder audioRecorder;
    private String recordedFilePath;
    private MediaPlayer mediaPlayer;

    private long startTime = 0; // 录音开始时间
    private Handler handler = new Handler();
    private Runnable updateTimerRunnable;
    String type;
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private boolean isUserTyping = true; // 标记用户是否正在输入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pary_write);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        img_main = findViewById(R.id.img_main);
        img_zp = findViewById(R.id.img_zp);
        img_xj = findViewById(R.id.img_xj);
        lin_write = findViewById(R.id.lin_write);
        lin_high = findViewById(R.id.lin_high);
        img_back = findViewById(R.id.img_back);
        img_chehui = findViewById(R.id.img_chehui);
        img_huifu = findViewById(R.id.img_huifu);
        img_ok = findViewById(R.id.img_ok);
        edtxt_content = findViewById(R.id.edtxt_content);
        edtxt_title = findViewById(R.id.edtxt_title);





        Intent intent=getIntent();
        type=intent.getStringExtra("type");
        if(type.equals("纯文")){
            lin_high.setVisibility(View.GONE);
        }else if(type.equals("语音")){
            img_main.setImageResource(R.drawable.icon_yuyin);

        }
        audioRecorder = new AudioRecorder();
        img_main.setOnClickListener(this);
        img_zp.setOnClickListener(this);
        img_xj.setOnClickListener(this);
        img_back.setOnClickListener(this);
        img_chehui.setOnClickListener(this);
        img_huifu.setOnClickListener(this);
        img_ok.setOnClickListener(this);

        updateButtonStates();

        edtxt_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (isUserTyping) {
                    undoStack.push(s.toString()); // 记录当前状态
                    if (count != 0 || after != 0) {
                        redoStack.clear(); // 只有用户输入新内容时才清空 redoStack
                    }
                }
                updateButtonStates();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    // 申请存储权限
    private void applyPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            openGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: // 存储权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    Toast.makeText(this, "你拒绝使用存储权限！", Toast.LENGTH_SHORT).show();
                    Log.d("HL", "你拒绝使用存储权限！");
                }
                break;

            case PERMISSIONS_REQUEST_CAMERA: // 相机权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(this, "需要相机权限才能拍照！", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_RECORD_AUDIO_PERMISSION: // 录音权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionToRecordAccepted = true;
                    setDialog();
                } else {
                    permissionToRecordAccepted = false;
                    Toast.makeText(this, "你拒绝使用录音权限！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    // 打开相册
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE);
    }

    // 请求相机权限
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        } else {
            takePhoto();
        }
    }

    // 启动相机并拍照
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_GALLERY_REQUEST_CODE && data != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    updateImageView(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null && extras.containsKey("data")) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    updateImageView(imageBitmap);
                }
            }
        }
    }

    // 更新 ImageView 或添加新的 ImageView
    private void updateImageView(Bitmap bitmap) {
        // 获取最后一个子视图
        View lastChild = lin_write.getChildAt(lin_write.getChildCount() - 1);
        // 判断最后一个子视图是否为 ImageView 类型
        if (lastChild instanceof ImageView) {
            ((ImageView) lastChild).setImageBitmap(bitmap);
        } else {
            ImageView mImg = new ImageView(this);
            // 获取屏幕宽度
            int screenWidth = getResources().getDisplayMetrics().widthPixels;  // 单位为像素

            float aspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();

// 根据屏幕宽度计算高度
            int height = (int) (screenWidth / aspectRatio);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, height);
            mImg.setLayoutParams(params);
            mImg.setImageBitmap(bitmap);
            lin_write.addView(mImg);
            mImg.setOnLongClickListener(view -> {
                lin_write.removeView(mImg);
                return true;
            });
        }
    }

    public void setDialog(){
        // 参数2：设置BottomSheetDialog的主题样式；将背景设置为transparent，这样我们写的shape_bottom_sheet_dialog.xml才会起作用
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
//不传第二个参数
//BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);

// 底部弹出的布局
        View bottomView = LayoutInflater.from(this).inflate(R.layout.bottom_voice_layout, null);
        img_play = bottomView.findViewById(R.id.img_play);
        img_stop = bottomView.findViewById(R.id.img_stop);
        txt_cancel = bottomView.findViewById(R.id.txt_Cancel);
        txt_duration = bottomView.findViewById(R.id.txt_duration);

        VisualizerView visualizerView = bottomView.findViewById(R.id.visualizerView);
        audioRecorder.setVisualizerView(visualizerView);
        img_play.setOnClickListener(this);
        img_stop.setOnClickListener(this);
        txt_cancel.setOnClickListener(v->{
            audioRecorder.cancelRecording();
            handler.removeCallbacks(updateTimerRunnable); // 停止更新
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomView);
//设置点击dialog外部不消失
//bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.show();
    }

    private void playRecording(String filePath) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "播放录音", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "出错"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 动画效果
    private void animZP() {
        if (img_xj.getVisibility() == View.VISIBLE) {
            img_zp.setVisibility(View.INVISIBLE);
            img_xj.setVisibility(View.INVISIBLE);
            return;
        }

        img_zp.setVisibility(View.VISIBLE);
        img_xj.setVisibility(View.VISIBLE);

        // 动画效果
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(img_zp, "translationY", 0f, -250f);
        animator1.setDuration(700);

        ObjectAnimator animator2X = ObjectAnimator.ofFloat(img_xj, "translationX", 0f, 200f);
        ObjectAnimator animator2Y = ObjectAnimator.ofFloat(img_xj, "translationY", 0f, -200f);
        animator2X.setDuration(700);
        animator2Y.setDuration(700);

        animator1.start();
        animator2X.start();
        animator2Y.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_main:
                if(type.equals("图文")){
                    animZP();
                }else {
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

                }
                break;
            case R.id.img_zp:
                applyPermission();
                break;
            case R.id.img_xj:
                requestCameraPermission();
                break;
            case R.id.img_stop:
                audioRecorder.stopRecording();
                stopRecordingTimer(); // 停止计时
                Toast.makeText(this, "录音已保存: " + recordedFilePath, Toast.LENGTH_LONG).show();
                break;
            case R.id.img_play:
                recordedFilePath = audioRecorder.startRecording();
                if (recordedFilePath != null) {
                    startRecordingTimer(); // 开始计时
                    Toast.makeText(this, "录音开始", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this, "录音开始", Toast.LENGTH_SHORT).show();
                break;
            case R.id.img_ok:
                break;
            case R.id.img_huifu:
                if (!redoStack.isEmpty()) {
                    undoStack.push(edtxt_content.getText().toString()); // 先保存当前状态到 undo
                    String nextText = redoStack.pop(); // 取出下一个状态
                    isUserTyping = false;
                    edtxt_content.setText(nextText);
                    edtxt_content.setSelection(nextText.length()); // 光标移到末尾
                    isUserTyping = true;
                }
                break;
            case R.id.img_chehui:
                if (!undoStack.isEmpty()) {
                    redoStack.push(edtxt_content.getText().toString()); // 先保存当前状态到 redo
                    String previousText = undoStack.pop(); // 取出上一个状态
                    isUserTyping = false;
                    edtxt_content.setText(previousText);
                    edtxt_content.setSelection(previousText.length()); // 光标移到末尾
                    isUserTyping = true;
                }
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.txt_Cancel:

                //播放
//                if (recordedFilePath != null) {
//                    playRecording(recordedFilePath);
//                }
//                break;
        }
    }
    private void startRecordingTimer() {
        startTime = System.currentTimeMillis();
        updateTimerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                txt_duration.setText(formatTime(elapsedTime));

                if (elapsedTime >= 60) { // 60秒自动停止
                    audioRecorder.stopRecording();
                    stopRecordingTimer();
                    Toast.makeText(ParyWrite.this, "录音已达到最大时长", Toast.LENGTH_SHORT).show();
                    return;
                }

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateTimerRunnable);
    }


    private void stopRecordingTimer() {
        handler.removeCallbacks(updateTimerRunnable); // 停止更新
        txt_duration.setText("00:00"); // 复位时间显示
    }

    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long sec = seconds % 60;
        return String.format("%02d:%02d", minutes, sec);
    }
    /**
     * 更新撤回和恢复按钮的状态
     */
    private void updateButtonStates() {
        if(!undoStack.isEmpty()){
            img_chehui.setBackgroundColor(Color.WHITE);
        }
        if(!redoStack.isEmpty()){
            img_huifu.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
