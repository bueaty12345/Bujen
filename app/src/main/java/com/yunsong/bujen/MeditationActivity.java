
package com.yunsong.bujen;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import com.yunsong.bujen.fragment.MusicController;
import com.yunsong.bujen.fragment.MusicService;

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
    ImageView img_circle,img_play,img_timing,img_next,img_previous,img_reset;
    TextView txt_stateTime,txt_endTime,txt_name;
    SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private MusicService.MusicControl musicControl;

    private boolean isPlaying = false;
    private String videoUrl;
    private String backgroundImage;
    private GestureDetector gestureDetector;
    private CountDownTimer countDownTimer;

    private static MeditationActivity currentActivity;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (musicControl != null) {
                int duration = musicControl.getDuration();
                int current = musicControl.getCurrentPosition();
                txt_stateTime.setText(formatTime(current));
                txt_endTime.setText(formatTime(duration));
                seekBar.setMax(duration);
                seekBar.setProgress(current);
                img_play.setImageResource(musicControl.isPlay() ? R.drawable.home_start : R.drawable.med_stop);
            }
            handler.postDelayed(this, 1000);
        }
    };
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicControl = (MusicService.MusicControl) service;
            MusicController.getInstance().setMusicControl(musicControl);
            if (videoUrl != null && !videoUrl.isEmpty()) {
                musicControl.playFromUrl(videoUrl);
                handler.postDelayed(() -> {
                    int duration = musicControl.getDuration();
                    txt_endTime.setText(formatTime(duration));
                    seekBar.setMax(duration);
                }, 500);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicControl = null;
        }
    };


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

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);

        currentActivity = this;
    }

    private void intView() {
        main = findViewById(R.id.main);
        lin_kz = findViewById(R.id.lin_kz);
        img_circle = findViewById(R.id.img_circle);
        img_play = findViewById(R.id.img_play);
        img_timing = findViewById(R.id.img_timing);
        img_next = findViewById(R.id.img_next);
        img_previous = findViewById(R.id.img_previous);
        img_reset = findViewById(R.id.img_reset);
        txt_stateTime = findViewById(R.id.txt_stateTime);
        txt_endTime = findViewById(R.id.txt_endTime);
        txt_name=findViewById(R.id.txt_name);
        seekBar = findViewById(R.id.seekBar);

        img_play.setOnClickListener(this);
        img_timing.setOnClickListener(this);
        img_next.setOnClickListener(this);
        img_previous.setOnClickListener(this);
        img_reset.setOnClickListener(this);
        img_circle.setOnClickListener(this);
        main.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicControl != null) {
                    musicControl.seekTo(seekBar.getProgress());
                }
            }
        });
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
                    startActivityForResult(new Intent(MeditationActivity.this, MedLocad.class), 1001);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                    return true;
                }
                return false;
            }
        });

        main.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void setupUI() {
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
            case R.id.img_play:
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

        List<Integer> timeList = Arrays.asList(5, 10, 15, 30, 45, 60, 90, 120);
        TimerAdapter adapter = new TimerAdapter(timeList, selected -> {
            dialog.dismiss();
            startCountdown(selected * 60);
            Toast.makeText(this, "定时 " + selected + " 分钟", Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        dialog.setContentView(view);
        dialog.show();

        View bottomSheet = dialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }
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
        if (musicControl != null) {
            if (musicControl.isPlay()) {
                musicControl.pausePlay();
                img_play.setImageResource(R.drawable.med_stop); // 主动更新暂停图标
            } else {
                musicControl.continuePlay();
                img_play.setImageResource(R.drawable.home_start); // 主动更新播放图标
            }
        }
    }

    private void stopMusic() {
        if (musicControl != null && musicControl.isPlay()) {
            musicControl.pausePlay();
        }
    }

    private static String formatTime(int ms) {
        int seconds = ms / 1000;
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateRunnable);
        if (musicControl != null && musicControl.isPlay()) {
            musicControl.pausePlay();
            img_play.setImageResource(R.drawable.med_stop);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        handler.removeCallbacks(updateRunnable);
        currentActivity = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("tutorialName");
            String newVideoUrl = data.getStringExtra("videoUrl");
            String newBackground = data.getStringExtra("backgroundMusicUrl");

            if (newVideoUrl != null && !newVideoUrl.isEmpty()) {
                this.videoUrl = newVideoUrl;
                this.backgroundImage = newBackground;
                txt_name.setText(title);

                if (musicControl != null) {
                    musicControl.playFromUrl(videoUrl);
                    handler.removeCallbacks(updateRunnable);
                    handler.post(updateRunnable);

                    // 主动更新播放图标
                    img_play.setImageResource(R.drawable.home_start);
                }

                setBackgroundWithBlur(backgroundImage);
                Glide.with(this).load(backgroundImage).into(img_circle);
            }
        }
    }

    public static void start(Context context, String title, String videoUrl, String imageUrl) {
        Intent intent = new Intent(context, MeditationActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_VIDEO_URL, videoUrl);
        intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String newTitle = intent.getStringExtra(EXTRA_TITLE);
        String newVideoUrl = intent.getStringExtra(EXTRA_VIDEO_URL);
        String newImageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);

        // 如果 URL 相同，则什么都不做（避免重复播放）
        if (videoUrl != null && videoUrl.equals(newVideoUrl)) {
            return;
        }

        // 更新数据
        this.videoUrl = newVideoUrl;
        this.backgroundImage = newImageUrl;

        txt_name.setText(newTitle != null ? newTitle : "");

        // 替换背景图与圆图
        setBackgroundWithBlur(backgroundImage);
        Glide.with(this).load(backgroundImage).into(img_circle);

        // 播放新音乐
        if (musicControl != null) {
            musicControl.playFromUrl(videoUrl);
            handler.removeCallbacks(updateRunnable);
            handler.post(updateRunnable);
            img_play.setImageResource(R.drawable.home_start); // 播放图标
        }
    }
}