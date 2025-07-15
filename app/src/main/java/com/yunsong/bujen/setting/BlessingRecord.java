package com.yunsong.bujen.setting;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.BlessingRecordBean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BlessingRecord extends AppCompatActivity {

    private TextView tv_title,tv_content,tv_date,tv_words,tv_event,tv_label,tvProgress ,tvTotalTime ,tvAudioDuration ;
    private ImageView img_main,btn_back,btnPlay,btnDelete;
    private FrameLayout layoutImageSection;
    private View audioCard;
    private SeekBar seekBar;
    private BlessingRecordBean bean;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blessingrecord);
        init();

    }

    private void init(){
        bean = getIntent().getParcelableExtra("record");
        if (bean == null) return;

        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_date = findViewById(R.id.tv_date);
        tv_words = findViewById(R.id.tv_words);
        tv_event = findViewById(R.id.tv_event);
        img_main = findViewById(R.id.img_main);
        btn_back = findViewById(R.id.btn_back);
        layoutImageSection=findViewById(R.id.layout_image_section);
        tv_label=findViewById(R.id.tv_label);

        audioCard = findViewById(R.id.audio_card);
        btnPlay = audioCard.findViewById(R.id.btn_play);
        seekBar = audioCard.findViewById(R.id.seekBar);
        tvProgress = audioCard.findViewById(R.id.tv_progress);
        tvTotalTime = audioCard.findViewById(R.id.tv_total_time);
        tvAudioDuration = audioCard.findViewById(R.id.tv_audio_duration);
        btnDelete = audioCard.findViewById(R.id.btn_delete);
        btnDelete.setVisibility(View.GONE);

        if("Text".equals(bean.blessingMethod)){
            tv_label.setText("基础");
        }else {
            tv_label.setText("进阶");
        }
        tv_title.setText(bean.blessingTitle);
        tv_content.setText(bean.blessingContent);

        tv_date.setText(getCurrentTime());

        switch (bean.blessingMethod) {
            case "Text":
                tv_words.setText("800字限定");
                break;
            case "ImageText":
                tv_words.setText("900字 + 1图限定");
                break;
            case "Audio":
                tv_words.setText("6'00\"语音限定");
                break;
            default:
                tv_words.setText("800字限定");
                break;
        }

        tv_event.setText(bean.achieveTime + " 接福");

        if ("Audio".equals(bean.blessingMethod)) {
            // 显示语音卡片
            layoutImageSection.setVisibility(View.VISIBLE);
            img_main.setVisibility(View.GONE);
            audioCard.setVisibility(View.VISIBLE);

            tvAudioDuration.setText("00:00");
            tvTotalTime.setText("00:00");
            tv_label.setVisibility(View.GONE);

            btnPlay.setOnClickListener(v -> {
                toggleAudio(bean.blessingAudioUrl, seekBar, tvProgress, tvTotalTime, btnPlay);
            });

        } else {
            // 图片模式
            audioCard.setVisibility(View.GONE);
            if ((bean.blessingImageUrl == null || bean.blessingImageUrl.isEmpty())
                    && "Text".equals(bean.blessingMethod)) {
                layoutImageSection.setVisibility(View.GONE);
            } else {
                layoutImageSection.setVisibility(View.VISIBLE);
                img_main.setVisibility(View.VISIBLE);
                if (bean.blessingImageUrl != null && !bean.blessingImageUrl.isEmpty()) {
                    if (bean.blessingImageUrl.startsWith("http")) {
                        Glide.with(this).load(bean.blessingImageUrl).into(img_main);
                    } else {
                        img_main.setImageURI(Uri.parse(bean.blessingImageUrl));
                    }
                }
            }
        }

//        if ((bean.blessingImageUrl == null || bean.blessingImageUrl.isEmpty()) &&
//                "Text".equals(bean.blessingMethod)) {
//            // 如果是纯文字且没有图片，隐藏整个图片区域
//            layoutImageSection.setVisibility(View.GONE);
//        } else {
//            // 否则正常显示图片
//            layoutImageSection.setVisibility(View.VISIBLE);
//            if (bean.blessingImageUrl != null && !bean.blessingImageUrl.isEmpty()) {
//                if (bean.blessingImageUrl.startsWith("http")) {
//                    Glide.with(this).load(bean.blessingImageUrl).into(img_main);
//                } else {
//                    img_main.setImageURI(Uri.parse(bean.blessingImageUrl));
//                }
//            }
//        }

        btn_back.setOnClickListener(v -> {
            returnResultAndFinish();
        });
    }

    private void toggleAudio(String audioUrl, SeekBar seekBar, TextView tvProgress, TextView tvTotalTime, ImageView btnPlay) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepareAsync();

                mediaPlayer.setOnPreparedListener(mp -> {
                    mediaPlayer.start();
                    isPlaying = true;
                    btnPlay.setImageResource(R.drawable.home_start); // 播放中图标

                    int duration = mediaPlayer.getDuration();
                    tvTotalTime.setText(formatTime(duration));
                    tvAudioDuration.setText(formatTime(duration));
                    seekBar.setMax(duration);

                    // 更新进度条
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null && isPlaying) {
                                int current = mediaPlayer.getCurrentPosition();
                                seekBar.setProgress(current);
                                tvProgress.setText(formatTime(current));
                                handler.postDelayed(this, 500);
                            }
                        }
                    }, 500);
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    isPlaying = false;
                    btnPlay.setImageResource(R.drawable.voice_play);
                    seekBar.setProgress(0);
                    tvProgress.setText("00:00");
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlay.setImageResource(R.drawable.voice_play);
        } else {
            mediaPlayer.start();
            isPlaying = true;
            btnPlay.setImageResource(R.drawable.voice_play);
        }
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec);
    }
    private void returnResultAndFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("viewedId", bean.recordId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnResultAndFinish();
    }

    // 获取当前时间字符串
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}
