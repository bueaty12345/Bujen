package com.yunsong.bujen;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.adapter.LocalLightAdapter;
import com.yunsong.bujen.adapter.LocalMusicAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Local extends AppCompatActivity {
    TabLayout tab_local;
    ListView listView;
    ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_local);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        tab_local.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 当选项卡被选中时更新 TextView 内容
                switch (tab.getPosition()) {
                    case 0:
                        setSound();//音乐
                        break;
                    case 1:
                        setLamplight();//灯光
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void init(){
        tab_local=findViewById(R.id.tab_local);
        listView=findViewById(R.id.list_local);
        img_back=findViewById(R.id.img_back);
        setSound();
    }
    private void setLamplight() {
// 准备数据
        String[] dd={"关闭","绿色","红色","橙色","蓝色"};
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("name", "灯光 " + dd[i]);
            data.add(item);
        }
        LocalLightAdapter adapter;
        // 创建适配器
        String[] from = {"name"}; // 数据源的键
        int[] to = {R.id.txt_mname}; // 布局文件中的视图 ID
        adapter = new LocalLightAdapter(this, data);
        listView.setAdapter(adapter);
    }

    private void setSound() {
        // 准备数据
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("name", "音乐曲目 " + i);
            data.add(item);
        }
        
        // 创建适配器
        LocalMusicAdapter adapter = new LocalMusicAdapter(this, data);
        listView.setAdapter(adapter);
    }
}