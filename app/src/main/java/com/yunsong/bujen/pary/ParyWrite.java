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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;
import com.yunsong.bujen.ui.DatePickerDialog;
import com.yunsong.bujen.utils.FileUploadUtils;
import com.yunsong.bujen.utils.FileUtils;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ParyWrite extends AppCompatActivity implements View.OnClickListener {
    private ImageView img_main, img_zp, img_xj,img_play,img_stop;
    private ImageView img_back,img_chehui,img_huifu,img_ok;
    private LinearLayout lin_write,lin_high;
    private TextView txt_cancel,txt_duration,txt_sort ,txt_date ,achieveTime;
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
    private File imageFile;
    private String imageUrl = "";

    private BottomSheetDialog bottomSheetDialog;

    private ConfirmDialog dialog;

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
        achieveTime=findViewById(R.id.achieveTime);

        Intent intent=getIntent();
        type=intent.getStringExtra("type");
        txt_sort = findViewById(R.id.txt_sort);
        txt_date = findViewById(R.id.txt_date);
        if ("纯文".equals(type)) {
            txt_sort.setText("800字限定");
        } else if ("图文".equals(type)) {
            txt_sort.setText("900字+1图限定");
        } else if ("语音".equals(type)) {
            txt_sort.setText("6’00”语音限定");
        } else {
            txt_sort.setText("未知类型");
        }

        if(type.equals("纯文")){
            lin_high.setVisibility(View.GONE);
        }else if(type.equals("语音")){
            img_main.setImageResource(R.drawable.icon_yuyin);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
        String currentDateTime = sdf.format(new Date());
        txt_date.setText(currentDateTime);

        audioRecorder = new AudioRecorder();
        img_main.setOnClickListener(this);
        img_zp.setOnClickListener(this);
        img_xj.setOnClickListener(this);
        img_back.setOnClickListener(this);
        img_chehui.setOnClickListener(this);
        img_huifu.setOnClickListener(this);
        img_ok.setOnClickListener(this);
        achieveTime.setOnClickListener(this);

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


        new Handler().postDelayed(() -> {
            showSelectDateDialog();
        }, 300);
    }

    private void showSelectDateDialog() {
        DatePickerDialog.show(this, (year, month, day) -> {
            String dateStr = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month, day);
            achieveTime.setText(dateStr);
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
            Bitmap bitmap = null;
            if (requestCode == OPEN_GALLERY_REQUEST_CODE && data != null) {
                try {
                    Uri uri = data.getData();
                    imageFile = FileUtils.uriToFile(this, uri);
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO && data != null) {
                Bundle extras = data.getExtras();
                if (extras != null && extras.containsKey("data")) {
                    bitmap = (Bitmap) extras.get("data");
                    try {
                        imageFile = FileUtils.bitmapToTempFile(this, bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (bitmap != null) {
                updateImageView(bitmap); // 展示图片
                uploadImageFile();       // 上传图片
            }
        }
    }

    // 更新 ImageView 或添加新的 ImageView
    private void updateImageView(Bitmap bitmap) {
        View lastChild = lin_write.getChildAt(lin_write.getChildCount() - 1);
        if (lastChild instanceof ImageView) {
            ((ImageView) lastChild).setImageBitmap(bitmap);
        } else {
            ImageView mImg = new ImageView(this);
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            float aspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();
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
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
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

                if (!TextUtils.isEmpty(recordedFilePath)) {
                    addAudioCard(recordedFilePath, txt_duration.getText().toString());
                }

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
                saveBlessing();
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

            case R.id.achieveTime:
                showSelectDateDialog();
                break;
        }
    }

    private void addAudioCard(String audioPath, String duration) {
        View audioCard = LayoutInflater.from(this).inflate(R.layout.item_voiceinfo, null);

        TextView tvTotalTime = audioCard.findViewById(R.id.tv_total_time);
        TextView tvDuration = audioCard.findViewById(R.id.tv_audio_duration);
        SeekBar seekBar = audioCard.findViewById(R.id.seekBar);
        TextView tvProgress = audioCard.findViewById(R.id.tv_progress);
        ImageView btnPlay = audioCard.findViewById(R.id.btn_play);
        ImageView btnDelete = audioCard.findViewById(R.id.btn_delete);

        // 设置时长
        tvTotalTime.setText(duration);
        tvDuration.setText(duration);

        // 播放按钮逻辑
        btnPlay.setOnClickListener(v -> {
            playAudioWithSeekBar(audioPath, seekBar, tvProgress);
        });

        // 删除按钮弹窗确认
        btnDelete.setOnClickListener(v -> {
            ConfirmDialog.Builder builder = new ConfirmDialog.Builder(this);
             dialog = builder
                    .cancelTouchout(false)
                    .view(R.layout.dialog_confirm)
                    .style(R.style.Dialog)
                    .addViewOnclick(R.id.txt_confirm, v1 -> {
                        lin_write.removeView(audioCard);
                        recordedFilePath = null;
                    })
                    .addViewOnclick(R.id.txt_confirm, v1 -> {
                        lin_write.removeView(audioCard);
                        recordedFilePath = null;
                        dialog.dismiss(); // 加这句
                    })
                    .build();

            // 设置弹窗文字
            TextView tvTitle = dialog.findViewById(R.id.txt_title);
            if (tvTitle != null) {
                tvTitle.setText("确定是否删除内容");
            }

            dialog.show();
        });

        lin_write.addView(audioCard);

        // 上传语音文件
        File audioFile = new File(audioPath);
        FileUploadUtils.uploadFile(this, audioFile, new FileUploadUtils.UploadCallback() {
            @Override
            public void onSuccess(String url) {
                recordedFilePath = url;
                runOnUiThread(() -> Toast.makeText(ParyWrite.this, "语音上传成功", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(String errorMsg) {
                runOnUiThread(() -> Toast.makeText(ParyWrite.this, "语音上传失败：" + errorMsg, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void playAudioWithSeekBar(String filePath, SeekBar seekBar, TextView tvProgress) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            seekBar.setMax(mediaPlayer.getDuration());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        int current = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(current);
                        tvProgress.setText(formatTime(current / 1000));
                        handler.postDelayed(this, 500);
                    }
                }
            }, 0);

            mediaPlayer.setOnCompletionListener(mp -> {
                seekBar.setProgress(0);
                tvProgress.setText("00:00");
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
        }
    }


    private void uploadImageFile() {
        if (imageFile == null) return;

        FileUploadUtils.uploadFile(this, imageFile, new FileUploadUtils.UploadCallback() {
            @Override
            public void onSuccess(String url) {
                imageUrl = url;
                runOnUiThread(() ->
                        Toast.makeText(ParyWrite.this, "图片上传成功", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onFailure(String errorMsg) {
                runOnUiThread(() ->
                        Toast.makeText(ParyWrite.this, "图片上传失败：" + errorMsg, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void saveBlessing() {
        String title = edtxt_title.getText().toString().trim();
        String content = edtxt_content.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "标题或内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String blessingMethod = getIntent().getStringExtra("blessingMethod");
        if ("图文".equals(blessingMethod)) {
            if (imageFile == null) {
                Toast.makeText(this, "请先选择或拍摄一张图片", Toast.LENGTH_SHORT).show();
                return;
            }
            postBlessing(title, content, blessingMethod, imageUrl, recordedFilePath);
        } else if ("语音".equals(blessingMethod)) {
            if (TextUtils.isEmpty(recordedFilePath)) {
                Toast.makeText(this, "请先录音", Toast.LENGTH_SHORT).show();
                return;
            }
            postBlessing(title, content, blessingMethod, imageUrl, recordedFilePath);
        } else {
            postBlessing(title, content, blessingMethod, imageUrl, recordedFilePath);
        }

        String selectedAchieveTime = achieveTime.getText().toString();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (today.equals(selectedAchieveTime)) {
            // 清除本地保存的 achieveTime
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .remove("last_achieve_time")
                    .apply();
        }

    }

    private void postBlessing(String title, String content, String blessingMethod,String imageUrl,String audioUrl) {
        String token = UserInfoUtils.getToken(this);
        int userId = UserInfoUtils.getUserId(this);
        int blessingId = getIntent().getIntExtra("blessingId", 1);

        JSONObject json = new JSONObject();
        try {
            json.put("userId", userId);
            json.put("blessingTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            json.put("blessingId", blessingId);
            json.put("blessingTitle", title);
            json.put("blessingMethod", blessingMethod);
            json.put("blessingContent", content);
            json.put("blessingAudioUrl", audioUrl);
            json.put("blessingImageUrl", imageUrl);
            json.put("wishTime", "");
            json.put("achieveTime",  achieveTime.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d("postBlessing","postBlessing"+json);
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BuildConfig.API_SERVER + "/system/recordb")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ParyWrite.this, "保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(ParyWrite.this, "保存成功", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(ParyWrite.this, "保存失败，状态码：" + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
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
