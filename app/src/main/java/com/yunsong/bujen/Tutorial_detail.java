package com.yunsong.bujen;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tutorial_detail extends AppCompatActivity {
LinearLayout lin_sk;
VideoView videoView;
GridView gridView;
private boolean isFullScreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        videoView = findViewById(R.id.videoView);
        lin_sk = findViewById(R.id.lin_sk);
        gridView=findViewById(R.id.grid_tj);
        setSquare();

        // 设置视频控制器，允许用户控制播放、暂停等
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // 设置网络视频的 URI
        String videoUrl = "https://media.w3.org/2010/05/sintel/trailer.mp4"; // 替换为实际的视频 URL
        Uri videoUri = Uri.parse(videoUrl);
        videoView.setVideoURI(videoUri);
        lin_sk.setOnClickListener(v -> {
        // 播放视频
        videoView.start();});
        // 点击事件，切换全屏
//        videoView.setOnClickListener(v -> toggleFullScreen());
    }
    // 切换全屏模式
    private void toggleFullScreen() {
        if (isFullScreen) {
            // 恢复原来的 VideoView 大小
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getSupportActionBar().show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  // 恢复竖屏

            // 恢复 VideoView 的布局
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;  // 恢复原来高度
            videoView.setLayoutParams(params);

            isFullScreen = false;
        } else {
            // 进入全屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getSupportActionBar().hide();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  // 强制横屏

            // 设置 VideoView 全屏显示
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;  // 设置为屏幕高度
            videoView.setLayoutParams(params);

            isFullScreen = true;
        }
    }

    private void setSquare() {
        // 准备数据
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "教程 " + i);
            item.put("gdd", "功德值：" + i*100);
            data.add(item);
        }
        gridView.setNumColumns(2);
        // 创建适配器
        String[] from = {"name", "gdd"}; // 数据源的键
        int[] to = {R.id.txt_name, R.id.txt_gdd}; // 布局文件中的视图 ID
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_tutorial, from, to);
        gridView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        // 如果是全屏模式，按返回键时退出全屏
        if (isFullScreen) {
            toggleFullScreen();
        } else {
            super.onBackPressed();
        }
    }

}