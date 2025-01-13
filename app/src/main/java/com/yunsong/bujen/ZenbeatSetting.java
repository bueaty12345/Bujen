package com.yunsong.bujen;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZenbeatSetting extends AppCompatActivity {
    GridView gridView;
    ImageView img_back,img_sy;
    Switch aSwitch;
    LinearLayout lin_sy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_zenbeat_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void init(){
        gridView=findViewById(R.id.grid_setting);
        img_back=findViewById(R.id.img_back);
        img_sy=findViewById(R.id.img_sy);
        aSwitch=findViewById(R.id.switch_sy);
        lin_sy=findViewById(R.id.lin_sy);
        setLight();
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {lin_sy.setAlpha(1f);img_sy.setImageResource(R.drawable.laba);}
                else {lin_sy.setAlpha(0.3f);img_sy.setImageResource(R.drawable.erji);}
            }
        });
    }

    private void setLight() {
        // 准备数据
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "声音 " + i);
            data.add(item);
        }

        // 创建适配器
        String[] from = {"name"}; // 数据源的键
        int[] to = {R.id.txt_yy}; // 布局文件中的视图 ID
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_ls, from, to);
        gridView.setAdapter(adapter);
    }
}