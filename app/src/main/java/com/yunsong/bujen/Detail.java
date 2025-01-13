package com.yunsong.bujen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import com.yunsong.bujen.adapter.LightAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Detail extends AppCompatActivity implements View.OnClickListener{
TextView txt_name,txt_gdd,txt_author;
ImageView img_hart,img_back;
Button btn_dh,btn_again;
LinearLayout lin_st;
ListView listView;
Boolean hart=false;
private ConfirmDialog dialog;
private MediaPlayer mediaPlayer;
    String music;
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
        img_hart.setOnClickListener(this);
        img_back.setOnClickListener(this);
        btn_dh.setOnClickListener(this);
        btn_again.setOnClickListener(this);
        lin_st.setOnClickListener(this);
    }
    private void init(){
        txt_name=findViewById(R.id.txt_mname);
        txt_gdd=findViewById(R.id.txt_gdd);
        txt_author=findViewById(R.id.txt_auther);
        img_hart=findViewById(R.id.img_selet);
        btn_dh=findViewById(R.id.btn_dh);
        img_back=findViewById(R.id.img_back);
        listView=findViewById(R.id.list_dgyx);
        btn_again=findViewById(R.id.btn_again);
        lin_st=findViewById(R.id.lin_st);
        // 初始化 MediaPlayer，指向你要播放的音频文件
//        mediaPlayer = MediaPlayer.create(this, R.raw.m1); // music_sample.mp3 放在 res/raw 目录下

        setSound();
        Intent intent = getIntent();

        // 接收传递的字符串和整数
        String name = intent.getStringExtra("name");
        String gdd = intent.getStringExtra("gdd");
        String auther = intent.getStringExtra("auther");
        music = intent.getStringExtra("music");
        playMusic(music);
        txt_name.setText(name);
        txt_gdd.setText(gdd);
        txt_author.setText(auther);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lin_st:
            case R.id.btn_again:
                // 开始播放音频
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    } else {
                        mediaPlayer.start();
                    }
                }
                break;
            case R.id.img_selet:
                if (hart){
                    img_hart.setImageResource(R.drawable.collection_2);
                    hart=false;
                }else {
                    img_hart.setImageResource(R.drawable.collection_1);
                    hart=true;
                }
                break;
            case R.id.img_back:
                finish();break;
            case R.id.btn_dh:
                if(btn_dh.getText().equals("兑换")) showDialog();
                break;
        }
    }
    private void showDialog() {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(this);
        dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn_dh.setText("已拥有");
                        dialog.dismiss();  // 这里添加取消对话框的代码
                    }
                })
                .build();
        dialog.show();
    }
    private void setSound() {
        // 准备数据
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("name", "音乐曲目 " + i);
            item.put("auther","作者："+i);
            item.put("gdd", "需功德点：" + i*1000);
            item.put("sc","0");
            data.add(item);
        }

        LightAdapter adapter = new LightAdapter(this, data);
        listView.setAdapter(adapter);
    }


    private void playMusic(String url) {
        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(Detail.this, "Error playing audio", Toast.LENGTH_SHORT).show();
                return false;
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(Detail.this, "Playback completed", Toast.LENGTH_SHORT).show();
                mediaPlayer.release();
                mediaPlayer = null;
            });
        }

        try {
            mediaPlayer.setDataSource(url);  // 设置音频源为网络URL
            mediaPlayer.prepareAsync();  // 异步准备音频
//            mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());  // 准备好后开始播放
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading audio", Toast.LENGTH_SHORT).show();
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
    }
}