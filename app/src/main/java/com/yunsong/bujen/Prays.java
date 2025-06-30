package com.yunsong.bujen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;
import com.yunsong.bujen.adapter.PraysAdapter;
import com.yunsong.bujen.databean.BlessingBean;
import com.yunsong.bujen.utils.ApiHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Prays extends AppCompatActivity {
    TabLayout tab_prays;
    GridView gridView;
    ImageView img_back;
    private List<BlessingBean> allBlessings = new ArrayList<>();
    private List<BlessingBean> currentList = new ArrayList<>();

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
                        setSquare();//基础
                        break;
                    case 1:
                        setDone();//进阶
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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Prays.this, PraysDetail.class);
                BlessingBean selected = currentList.get(i);
                intent.putExtra("blessing", selected);
                intent.putExtra("resourceType", selected.getResourceType());
                intent.putExtra("blessingId", selected.getBlessingId());
                intent.putExtra("gdd",selected.getRequiredMeritPoints());
                intent.putExtra("blessingBackgroundUrl",selected.getBlessingBackgroundUrl());
                intent.putExtra("blessingMethod",selected.getBlessingMethod());
                startActivityForResult(intent, 1001);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            int selectedTabPosition = tab_prays.getSelectedTabPosition();
            if (selectedTabPosition == 0) {
                setSquare();
            } else {
                setDone();
            }
        }
    }
    private void init() {
        tab_prays = findViewById(R.id.tab_prays);
        gridView = findViewById(R.id.grid_parys);
        img_back = findViewById(R.id.img_back);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("user_token", null);
        ApiHelper.fetchPrayList(this, token, new ApiHelper.Callback<BlessingBean>() {
            @Override
            public void onSuccess(List<BlessingBean> list) {
                allBlessings = list;
                setSquare(); // 默认显示基础
            }

            @Override
            public void onError(String message) {
                Toast.makeText(Prays.this, "获取祈福数据失败：" + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setDone() {
        currentList.clear();
        List<Map<String, String>> data = new ArrayList<>();
        for (BlessingBean bean : allBlessings) {
            if ("进阶".equals(bean.getBlessingCategory())) { // 进阶
                currentList.add(bean);
                Map<String, String> item = new HashMap<>();
                item.put("name", bean.getBlessingTheme());
                item.put("gdd", "需功德点：" + bean.getRequiredMeritPoints());
                item.put("sc", String.valueOf(bean.isSc()));
                item.put("author", "");
                item.put("dh", bean.isDh() ? "已完成" : "加入祈福");
                item.put("resourceType", bean.getResourceType());
                item.put("blessingId", String.valueOf(bean.getBlessingId()));
                item.put("blessingBackgroundUrl",bean.getBlessingBackgroundUrl());
                item.put("blessingMethod",bean.getBlessingMethod());
                data.add(item);
            }
        }
        PraysAdapter adapter = new PraysAdapter(this, data);
        gridView.setAdapter(adapter);
    }


    private void setSquare() {
        currentList.clear();
        List<Map<String, String>> data = new ArrayList<>();
        for (BlessingBean bean : allBlessings) {
            if ("基础".equals(bean.getBlessingCategory())) { // 基础
                currentList.add(bean);
                Map<String, String> item = new HashMap<>();
                item.put("name", bean.getBlessingTheme());
                item.put("gdd", "需功德点：" + bean.getRequiredMeritPoints());
                item.put("sc", String.valueOf(bean.isSc()));
                item.put("author", "");
                item.put("dh", bean.isDh() ? "已完成" : "加入祈福");
                item.put("resourceType",bean.getResourceType());
                item.put("blessingId", String.valueOf(bean.getBlessingId()));
                item.put("blessingBackgroundUrl",bean.getBlessingBackgroundUrl());
                item.put("blessingMethod",bean.getBlessingMethod());
                data.add(item);
            }
        }
        PraysAdapter adapter = new PraysAdapter(this, data);
        gridView.setAdapter(adapter);

    }
}
