package com.yunsong.bujen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class Tutorial extends AppCompatActivity {
    TabLayout tab_tutorial;
    GridView gridView;
    ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        tab_tutorial.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 当选项卡被选中时更新 TextView 内容
                switch (tab.getPosition()) {
                    case 0:
                        setSquare();//广场
                        break;
                    case 1:
                        setRedeemed();//已兑换
                        break;
                    case 2:
                        setCollection();//收藏
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
        tab_tutorial=findViewById(R.id.tab_tutorial);
        gridView=findViewById(R.id.grid_tutorial);
        img_back=findViewById(R.id.img_back);
        setSquare();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(Tutorial.this, TutorialDetail.class));
            }
        });
    }

    private void setRedeemed() {
        // 准备数据
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "教程 " + i);
            item.put("gdd", "作者：" + i);
            data.add(item);
        }
        gridView.setNumColumns(1);

        // 创建适配器
        String[] from = {"name", "gdd"}; // 数据源的键
        int[] to = {R.id.txt_name, R.id.txt_gdd}; // 布局文件中的视图 ID
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_tutorial2, from, to);
        gridView.setAdapter(adapter);
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
    private void setCollection() {
        // 准备数据
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "收藏 " + i);
            item.put("gdd", "功德值：" + i*100);
            item.put("hart", R.drawable.collection_1);
            data.add(item);
        }
        gridView.setNumColumns(2);

        // 创建适配器
        String[] from = {"name", "gdd","hart"}; // 数据源的键
        int[] to = {R.id.txt_name, R.id.txt_gdd,R.id.img_hart}; // 布局文件中的视图 ID
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_tutorial, from, to);
        gridView.setAdapter(adapter);
    }
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("兑换")
                .setMessage("确定兑换吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 处理取消按钮点击
                        dialog.dismiss();

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}