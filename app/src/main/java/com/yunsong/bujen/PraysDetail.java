package com.yunsong.bujen;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PraysDetail extends AppCompatActivity {
GridView gridView;
private boolean isFullScreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prays_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
    }

    private void initView() {
        gridView = findViewById(R.id.grid_tj);
        setSquare();
    }

    private void setSquare() {
        // 准备数据
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "祈福 " + i);
            item.put("gdd", "功德值：" + i*100);
            data.add(item);
        }
        gridView.setNumColumns(2);
        // 创建适配器
        String[] from = {"name", "gdd"}; // 数据源的键
        int[] to = {R.id.txt_name, R.id.txt_gdd}; // 布局文件中的视图 ID
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_prays, from, to);
        gridView.setAdapter(adapter);

    }


}