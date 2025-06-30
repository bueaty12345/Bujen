package com.yunsong.bujen;

import static com.thingclips.sdk.blelib.utils.BluetoothUtils.getContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.yunsong.bujen.adapter.TutorialAdapter;
import com.yunsong.bujen.databean.MusicBean;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.utils.ApiHelper;
import com.yunsong.bujen.utils.ExchangeHelper;

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
//        tab_tutorial.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                // 当选项卡被选中时更新 TextView 内容
//                switch (tab.getPosition()) {
//                    case 0:
//                        setSquare();//广场
//                        break;
//                    case 1:
//                        setRedeemed();//已兑换
//                        break;
//                    case 2:
//                        setCollection();//收藏
//                        break;
//                }
//            }
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void init(){
//        tab_tutorial=findViewById(R.id.tab_tutorial);
        gridView=findViewById(R.id.grid_tutorial);
        img_back=findViewById(R.id.img_back);
        setRedeemed();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent;
                MyTutorialBean tutorialBean=(MyTutorialBean) adapterView.getAdapter().getItem(i);
                intent = new Intent(Tutorial.this, TutorialDetail.class);
                intent.putExtra("name",tutorialBean.getTutorialName());
                intent.putExtra("sc",tutorialBean.getSc());
                intent.putExtra("dh",tutorialBean.getDh());
                intent.putExtra("description",tutorialBean.getDescription());
                intent.putExtra("tutorialContent",tutorialBean.getTutorialContent());
                intent.putExtra("description",tutorialBean.getDescription());
                intent.putExtra("gdd",tutorialBean.getRequiredMeritPoints());
                intent.putExtra("rating",tutorialBean.getRating());
                intent.putExtra("resourceType",tutorialBean.getResourceType());
                intent.putExtra("tutorialId",tutorialBean.getTutorialId());
                intent.putExtra("videoUrl",tutorialBean.getVideoUrl());

                startActivityForResult(intent, 1001);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            setRedeemed();
        }
    }


    private void setRedeemed() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("user_token", null);
        ApiHelper.fetchTutorialList(this, token, new ApiHelper.Callback<MyTutorialBean>() {
            @Override
            public void onSuccess(List<MyTutorialBean> list) {
                for (MyTutorialBean item : list) {
                    if (item.getDh()) {
                        ExchangeHelper.markAsExchanged(getContext(), item.getResourceType(), item.getTutorialId());
                    }
                }
                TutorialAdapter adapter = new TutorialAdapter(Tutorial.this, list, R.layout.item_tutorial2);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }

}