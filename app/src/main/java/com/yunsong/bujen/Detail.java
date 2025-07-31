package com.yunsong.bujen;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.fragment.SettingsViewModel;
import com.yunsong.bujen.utils.DataStorageUtils;
import com.yunsong.bujen.utils.ExchangeHelper;
import com.yunsong.bujen.utils.FavoriteHelper;
import com.yunsong.bujen.utils.GddManager;
import com.yunsong.bujen.utils.UserInfoUtils;

import java.io.IOException;


public class Detail extends AppCompatActivity implements View.OnClickListener{
    private TextView txt_name,txt_gdd,txt_author,txt_time,txt_star,txt_date,txt_description,txt_rating,txt_virtuePoints;
    private ImageView img_selet,img_back,img_tu,auditionIcon;
    private Button btn_dh,btn_again;
    private LinearLayout lin_audition;
    private ListView listView;
    private Boolean hart=false;
    private ConfirmDialog dialog;
    private MediaPlayer mediaPlayer;
    String music;
    private SettingsViewModel sharedViewModel;

    private Handler playbackHandler = new Handler(Looper.getMainLooper());
    private Runnable stopRunnable;
    private boolean isPreviewPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        img_selet.setOnClickListener(this);
        img_back.setOnClickListener(this);
        btn_dh.setOnClickListener(this);
        btn_again.setOnClickListener(this);
        lin_audition.setOnClickListener(this);
    }
    private void init(){
        txt_name=findViewById(R.id.txt_mname);
        txt_gdd=findViewById(R.id.txt_gdd);
        txt_author=findViewById(R.id.txt_auther);
        img_selet=findViewById(R.id.img_selet);
        btn_dh=findViewById(R.id.btn_dh);
        img_back=findViewById(R.id.img_back);
        listView=findViewById(R.id.list_dgyx);
        btn_again=findViewById(R.id.btn_again);
        lin_audition=findViewById(R.id.audition);
        img_tu=findViewById(R.id.img_tu);
        txt_time=findViewById(R.id.txt_time);
        txt_star=findViewById(R.id.txt_star);
        txt_date=findViewById(R.id.txt_date);
        txt_description=findViewById(R.id.txt_description);
        txt_rating=findViewById(R.id.txt_rating);
        txt_virtuePoints=findViewById(R.id.virtuePoints);
        auditionIcon = findViewById(R.id.imageView10);
        // 初始化 MediaPlayer，指向你要播放的音频文件
//        mediaPlayer = MediaPlayer.create(this, R.raw.m1); // music_sample.mp3 放在 res/raw 目录下


        sharedViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        GddManager.init(this, sharedViewModel);

        // 监听功德点 ViewModel 更新 txt_gdd
        sharedViewModel.getGddCont().observe(this, gddCount -> {
            txt_virtuePoints.setText(String.valueOf(gddCount));
        });

        setSound();
        Intent intent = getIntent();

        // 接收传递的字符串和整数
        String name = intent.getStringExtra("name");
        int gdd = intent.getIntExtra("gdd",0);
        String singer = intent.getStringExtra("singer");
        String date=intent.getStringExtra("createdAt");
        String description=intent.getStringExtra("description");
        long duration=intent.getLongExtra("duration", 0);
        boolean sc=intent.getBooleanExtra("sc",false);
        boolean dh=intent.getBooleanExtra("dh",false);
        btn_dh.setText(dh ? "已拥有" : "兑换");
        btn_dh.setEnabled(!dh);
        music = intent.getStringExtra("music");
        playMusic(music);
        txt_name.setText(name);
        txt_gdd.setText("需功德值："+gdd);
        txt_author.setText(singer);
        txt_date.setText(date);
        txt_description.setText(description);
        String ratingStr = intent.getStringExtra("rating");
        if (ratingStr != null) {
            txt_rating.setText(ratingStr);
        }

        long minutes = duration / 60;
        long seconds = duration % 60;
        txt_time.setText(String.format("%d:%02d", minutes, seconds));

        //判断是否收藏
        hart = sc;
        if (sc) {
            img_selet.setImageResource(R.drawable.collection_1);
        } else {
            img_selet.setImageResource(R.drawable.collection_2);
        }

        //封面
        String musicCover = intent.getStringExtra("musicCover");
        if (musicCover != null && !musicCover.isEmpty()) {
            Glide.with(this)
                    .load(musicCover)
                    .placeholder(R.drawable.detail_bg)
                    .error(R.drawable.detail_bg)
                    .into(img_tu);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.audition:
            case R.id.btn_again:
                // 开始播放音频
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        stopPreviewPlayback();
                    } else {
                        startPreviewPlayback();
                    }
                }
                break;
            case R.id.img_selet:
                hart = !hart; // 切换收藏状态
                img_selet.setImageResource(hart ? R.drawable.collection_1 : R.drawable.collection_2);

                int userId = UserInfoUtils.getUserId(Detail.this);
                String resourceType = getIntent().getStringExtra("resourceType");;
                int resourceId = getIntent().getIntExtra("musicId", 0);;
                String token=UserInfoUtils.getToken(this);
                FavoriteHelper.updateFavoriteStatus(hart,token, userId, resourceType, resourceId, new FavoriteHelper.Callback() {
                    @Override
                    public void onSuccess() {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
                        setResult(RESULT_OK, resultIntent);
                        finish();
                        Toast.makeText(Detail.this, hart ? "收藏成功" : "取消收藏成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        // 失败，回滚UI和hart状态
                        hart = !hart;
                        runOnUiThread(() -> {
                            img_selet.setImageResource(hart ? R.drawable.collection_1 : R.drawable.collection_2);
                            Toast.makeText(Detail.this, "收藏状态更新失败：" + errorMsg, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                break;
            case R.id.img_back:
                finish();break;
            case R.id.btn_dh:
                if (btn_dh.getText().equals("兑换")) {
                    int requiredGdd = getIntent().getIntExtra("gdd", 0);
                    int localGdd = DataStorageUtils.getGddCount(this);

                    if (localGdd >= requiredGdd) {
                        ExchangeHelper.showExchangeDialog(this, requiredGdd,
                                getIntent().getStringExtra("resourceType"),
                                getIntent().getIntExtra("musicId", 0),
                                btn_dh);
                    } else {
                        Toast.makeText(this, "功德点不足，无法兑换", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void setSound() {

    }

    private void startPreviewPlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            stopPreviewPlayback();
            return;
        }

        if (TextUtils.isEmpty(music)) {
            Toast.makeText(this, "音频链接为空", Toast.LENGTH_SHORT).show();
            return;
        }

        playMusic(music);

        // 播放 10 秒后自动停止
        stopRunnable = () -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                isPreviewPlaying = false;
                auditionIcon.setImageResource(R.drawable.icon_play);
            }
        };
        playbackHandler.postDelayed(stopRunnable, 10000);
    }

    private void stopPreviewPlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            isPreviewPlaying = false;
            auditionIcon.setImageResource(R.drawable.icon_play); // 切换回播放图标
            playbackHandler.removeCallbacks(stopRunnable); // 移除定时器
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String resourceType = getIntent().getStringExtra("resourceType");
        int resourceId = getIntent().getIntExtra("musicId", 0);

        boolean exchanged = ExchangeHelper.isExchanged(this, resourceType, resourceId);
        btn_dh.setText(exchanged ? "已拥有" : "兑换");
        btn_dh.setEnabled(!exchanged);
    }


    private void playMusic(String url) {
        if (TextUtils.isEmpty(url) || !url.startsWith("http")) {
            Toast.makeText(this, "音频链接无效", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release(); // 释放旧的播放器
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // 设置音频流类型
            mediaPlayer.setDataSource(url); // 设置网络地址
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start(); // 准备完成后播放
                isPreviewPlaying = true;
                auditionIcon.setImageResource(R.drawable.icon_start);
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                isPreviewPlaying = false;
                auditionIcon.setImageResource(R.drawable.icon_play);
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(this, "音频播放失败", Toast.LENGTH_SHORT).show();
                return true;
            });

            mediaPlayer.prepareAsync(); // 异步准备
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "加载音频失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停音频播放
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 当 Activity 停止时，确保释放 MediaPlayer 资源
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保在 Activity 销毁时释放资源
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (playbackHandler != null && stopRunnable != null) {
            playbackHandler.removeCallbacks(stopRunnable);
        }
    }
}