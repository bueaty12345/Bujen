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

import com.yunsong.bujen.adapter.PraysAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Prays extends AppCompatActivity {
    TabLayout tab_prays;
    ListView listView;
    ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prays);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        tab_prays.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 当选项卡被选中时更新 TextView 内容
                switch (tab.getPosition()) {
                    case 0:
                        setSquare();//广场
                        break;
                    case 1:
                        setDone();//已完成
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
        tab_prays=findViewById(R.id.tab_prays);
        listView=findViewById(R.id.list_tutorial);
        img_back=findViewById(R.id.img_back);
        setSquare();
    }
    private void setDone() {
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("name", "身体健康 " + i);
            item.put("gdd", "");
            item.put("sc","0");
            item.put("author","");
            item.put("dh","已完成");
            data.add(item);
        }
        PraysAdapter adapter = new PraysAdapter(this, data);
        listView.setAdapter(adapter);
    }


    private void setSquare() {
        // 准备数据
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("name", "身体健康 " + i);
            item.put("gdd", "需功德点：" + i*1000);
            item.put("sc","0");
            item.put("author","");
            item.put("dh","加入祈福");
            data.add(item);
        }

        // 创建适配器
        PraysAdapter adapter = new PraysAdapter(this, data);
        listView.setAdapter(adapter);

    }
}
