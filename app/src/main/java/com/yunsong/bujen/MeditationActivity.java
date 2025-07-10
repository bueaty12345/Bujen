
package com.yunsong.bujen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yunsong.bujen.adapter.TimerAdapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MeditationActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String EXTRA_TITLE = "tutorialName";
    private static final String EXTRA_VIDEO_URL = "videoUrl";
    private static final String EXTRA_IMAGE_URL = "backgroundMusicUrl";
    ConstraintLayout main;
    LinearLayout lin_kz;
    ImageView img_circle,img_bf,img_timing,img_next,img_previous,img_reset;
    TextView txt_stateTime,txt_endTime,txt_name;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String videoUrl;
    private String backgroundImage;
    private GestureDetector gestureDetector;
    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meditation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        intView();
        initGesture();
        loadIntentData();
        setupUI();
    }

    private void intView() {
        main = findViewById(R.id.main);
        lin_kz = findViewById(R.id.lin_kz);
        img_circle = findViewById(R.id.img_circle);
        img_bf = findViewById(R.id.img_bf);
        img_timing = findViewById(R.id.img_timing);
        img_next = findViewById(R.id.img_next);
        img_previous = findViewById(R.id.img_previous);
        img_reset = findViewById(R.id.img_reset);
        txt_stateTime = findViewById(R.id.txt_stateTime);
        txt_endTime = findViewById(R.id.txt_endTime);
        txt_name=findViewById(R.id.txt_name);

        img_bf.setOnClickListener(this);
        img_timing.setOnClickListener(this);
        img_next.setOnClickListener(this);
        img_previous.setOnClickListener(this);
        img_reset.setOnClickListener(this);
        img_circle.setOnClickListener(this);
        main.setOnClickListener(this);
    }

    private void loadIntentData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
        videoUrl = intent.getStringExtra(EXTRA_VIDEO_URL);
        backgroundImage = intent.getStringExtra(EXTRA_IMAGE_URL);

        txt_name.setText(title != null ? title : "");

        // 1. 先显示默认模糊背景 qi_fu
        setDefaultVagueBackground();

        // 2. 如果传入的背景图不为空，再加载它（异步替换）
        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            setBackgroundWithBlur(backgroundImage);

            // 同时设置圆图
            Glide.with(this)
                    .load(backgroundImage)
                    .into(img_circle);
        }
    }

    private void setDefaultVagueBackground() {
        Glide.with(this)
                .load(R.drawable.qf_bg)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 35)))
                .into(new ViewTarget<ConstraintLayout, Drawable>(main) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Drawable drawable = resource.getCurrent();
                        drawable.setColorFilter(Color.parseColor("#66000000"), PorterDuff.Mode.SRC_OVER);
                        main.setBackground(drawable);
                    }
                });
    }

    private void setBackgroundWithBlur(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 35)))
                .into(new ViewTarget<ConstraintLayout, Drawable>(main) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Drawable drawable = resource.getCurrent();
                        drawable.setColorFilter(Color.parseColor("#66000000"), PorterDuff.Mode.SRC_OVER);
                        main.setBackground(drawable);
                    }
                });
    }

    private void initGesture() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getY() - e2.getY() > 100 && Math.abs(velocityY) > 800) {
                    startActivity(new Intent(MeditationActivity.this, MedLocad.class));
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                    return true;
                }
                return false;
            }
        });

        main.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void setupUI() {
        // 可选初始化播放时间等
        txt_stateTime.setText("00:00");
        txt_endTime.setText("00:00");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_circle:
            case R.id.main:
                if(lin_kz.getVisibility()==View.VISIBLE){
                    lin_kz.setVisibility(View.INVISIBLE);
                }else {
                    lin_kz.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.img_bf:
                toggleMusic();
                break;
            case R.id.img_reset:
                stopMusic();
                break;
            case R.id.img_timing:
                showTimerDialog();
                break;
        }
    }

    private void showTimerDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_timer_select, null);




        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_timer);

        List<Integer> timeList = Arrays.asList(5, 10, 15, 30, 45, 60, 90, 120); // 单位：分钟
        TimerAdapter adapter = new TimerAdapter(timeList, selected -> {
            dialog.dismiss();
            startCountdown(selected * 60); // 单位换成秒
            Toast.makeText(this, "定时 " + selected + " 分钟", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        dialog.setContentView(view);
        dialog.show();
    }

    private void startCountdown(int seconds) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                txt_endTime.setText(String.format("%02d:%02d", minutes, seconds));
            }

            public void onFinish() {
                stopMusic();
                txt_endTime.setText("00:00");
                Toast.makeText(MeditationActivity.this, "播放已结束", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }


    private void toggleMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(videoUrl);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> {
                    mediaPlayer.start();
                    isPlaying = true;
                    img_bf.setImageResource(R.drawable.home_start);
                });
                mediaPlayer.setOnCompletionListener(mp -> {
                    isPlaying = false;
                    img_bf.setImageResource(R.drawable.med_stop);
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
            }
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            img_bf.setImageResource(R.drawable.med_stop);
        } else {
            mediaPlayer.start();
            isPlaying = true;
            img_bf.setImageResource(R.drawable.home_start);
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            img_bf.setImageResource(R.drawable.med_stop);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            img_bf.setImageResource(R.drawable.med_stop);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    public static void start(Context context, String title, String videoUrl, String imageUrl) {
        Intent intent = new Intent(context, MeditationActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_VIDEO_URL, videoUrl);
        intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
        context.startActivity(intent);
    }
}