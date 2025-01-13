package com.yunsong.bujen;

import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Supplies extends AppCompatActivity {
    TabLayout tab_supplies;
    GridView gridView;
    ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplies);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        tab_supplies.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 当选项卡被选中时更新 TextView 内容
                switch (tab.getPosition()) {
                    case 0:
                        setFish();//木鱼
                        break;
                    case 1:
                        setCushion();//地垫
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
        tab_supplies=findViewById(R.id.tab_supplies);
        gridView=findViewById(R.id.grid_supplies);
        img_back=findViewById(R.id.img_back);
        setFish();
    }
    private void setFish() {
        // 准备数据
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "木鱼 " + i);
            item.put("money", "价格：" + i*100);
            data.add(item);
        }

        // 创建适配器
        String[] from = {"name", "money"}; // 数据源的键
        int[] to = {R.id.txt_hcname, R.id.txt_money}; // 布局文件中的视图 ID
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_supplies, from, to);
        gridView.setAdapter(adapter);

    }

    private void setCushion() {
        // 准备数据
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "地垫 " + i);
            item.put("money", "价格：" + i*100);
            data.add(item);
        }

        // 创建适配器
        String[] from = {"name", "money"}; // 数据源的键
        int[] to = {R.id.txt_hcname, R.id.txt_money}; // 布局文件中的视图 ID
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_supplies, from, to);
        gridView.setAdapter(adapter);
    }


}