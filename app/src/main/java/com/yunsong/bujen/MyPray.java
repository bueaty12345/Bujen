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

import com.google.android.material.tabs.TabLayout;
import com.yunsong.bujen.adapter.MyPrayAdapter;
import com.yunsong.bujen.adapter.MyTutorialAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPray extends AppCompatActivity {
    TabLayout tab_local;
    ListView listView;
    ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mypray);
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
                        setBasics();//基础
                        break;
                    case 1:
                        setAdvance();//进阶
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

    private void setAdvance() {
    }


    private void setBasics() {
        // 准备数据
        String[] title={"腰缠万贯"};
        List<Map<String, Object>> data = new ArrayList<>();
        int[] imageIds = {
                R.drawable.my_pray1,
//                R.drawable.local_music2,
//                R.drawable.local_music3,
//                R.drawable.local_music4,
//                R.drawable.local_music5
        };
        for (int i = 0; i <title.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", title[i]);
            item.put("image", imageIds[i]);
            data.add(item);
        }

        // 创建适配器
        MyPrayAdapter adapter=new MyPrayAdapter(this,data);
        listView.setAdapter(adapter);
    }

    private void init(){
        tab_local=findViewById(R.id.tab_local);
        listView=findViewById(R.id.list_local);
        img_back=findViewById(R.id.img_back);
        setBasics();
    }


}