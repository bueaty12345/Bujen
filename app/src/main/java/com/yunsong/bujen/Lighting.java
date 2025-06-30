package com.yunsong.bujen;

import static com.thingclips.sdk.blelib.utils.BluetoothUtils.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alibaba.fastjson.JSON;
import com.yunsong.bujen.adapter.LightAdapter;
import com.google.android.material.tabs.TabLayout;
import com.yunsong.bujen.adapter.MusicAdapter;
import com.yunsong.bujen.databean.MusicBean;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.utils.ApiHelper;
import com.yunsong.bujen.utils.ExchangeHelper;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Lighting extends AppCompatActivity {
    TabLayout tab_dgyx;
    ListView listView;
    ImageView img_back;
    private ConfirmDialog dialog;
    int position=0;

    List<MusicBean> dataList = new ArrayList<>();
    List<MyLightBean> lightList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lighting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        tab_dgyx.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 当选项卡被选中时更新 TextView 内容
                switch (tab.getPosition()) {
                    case 0:
                        setSound();//音乐
                        position=0;
                        break;
                    case 1:
                        setLamplight();//灯光
                        position=1;
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (position == 0) {
                    Intent intent;
                    MusicBean musicBean = (MusicBean) adapterView.getAdapter().getItem(i);
                    intent = new Intent(Lighting.this, Detail.class);

                    // 使用 putExtra() 传递数据
                    intent.putExtra("name", musicBean.getMusicName());
                    intent.putExtra("gdd", musicBean.getRequiredMeritPoints());
                    intent.putExtra("singer", musicBean.getSinger());
                    intent.putExtra("music", musicBean.getMusicUrl());
                    intent.putExtra("createdAt",musicBean.getCreatedAt());
                    intent.putExtra("rating", String.valueOf(musicBean.getRating()));
                    intent.putExtra("description",musicBean.getDescription());
                    intent.putExtra("sc",musicBean.getSC());
                    intent.putExtra("dh",musicBean.getDH());
                    intent.putExtra("duration",musicBean.getDuration());
                    intent.putExtra("resourceType",musicBean.getResourceType());
                    intent.putExtra("musicId",musicBean.getMusicId());
                    intent.putExtra("musicCover",musicBean.getMusicCover());

                    startActivityForResult(intent, 1001);
                } else if (position==1) {
                    Intent intent;
                    MyLightBean lightBean = (MyLightBean) adapterView.getAdapter().getItem(i);
                    intent = new Intent(Lighting.this, Detail2.class);

                    // 使用 putExtra() 传递数据
                    intent.putExtra("backgroundName", lightBean.getBackgroundName());
                    intent.putExtra("requiredMeritPoints", lightBean.getRequiredMeritPoints());
                    intent.putExtra("author", lightBean.getAuthor());
                    intent.putExtra("createdAt",lightBean.getCreatedAt());
                    intent.putExtra("rating", String.valueOf(lightBean.getRating()));
                    intent.putExtra("description",lightBean.getDescription());
                    intent.putExtra("sc",lightBean.isSc());
                    intent.putExtra("dh",lightBean.isDh());
                    intent.putExtra("backgroundImageUrl",lightBean.getBackgroundImageUrl());
                    intent.putExtra("description",lightBean.getDescription());
                    intent.putExtra("resourceType",lightBean.getResourceType());
                    intent.putExtra("backgroundId",lightBean.getBackgroundId());

                    startActivityForResult(intent, 1001);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (position == 0) {
                setSound();
            } else {
                setLamplight();
            }
        }
    }


    private void init(){
        tab_dgyx=findViewById(R.id.tab_dgyx);
        listView=findViewById(R.id.list_dgyx);
        img_back=findViewById(R.id.img_back);

        setSound();
    }

    private void setLamplight() {
        String token = UserInfoUtils.getToken(this);
        ApiHelper.fetchLightList(getContext(), token, new ApiHelper.Callback<MyLightBean>() {
            @Override
            public void onSuccess(List<MyLightBean> list) {
                for (MyLightBean item : list) {
                    if (item.isDh()) {
                        ExchangeHelper.markAsExchanged(getContext(), item.getResourceType(), item.getBackgroundId());
                    }
                }
                LightAdapter adapter = new LightAdapter(getContext(), list);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSound() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("user_token", null);  // 从 SharedPreferences 获取 token
        ApiHelper.fetchMusicList(getContext(), token, new ApiHelper.Callback<MusicBean>() {
            @Override
            public void onSuccess(List<MusicBean> list) {
                for (MusicBean item : list) {
                    if (item.getDH()) {
                        ExchangeHelper.markAsExchanged(getContext(), item.getResourceType(), item.getMusicId());
                    }
                }
                MusicAdapter adapter = new MusicAdapter(getContext(), list);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }


        private void updateUIWithUserInfo(List<MusicBean> dataList) {
            // 更新 UI 显示用户信息
            // 准备数据
//            List<Map<String, String>> data = new ArrayList<>();
//            for (int i = 0; i < dataList.size(); i++) {
//                Map<String, String> item = new HashMap<>();
//                item.put("name", dataList.get(i).getMusicName());
//                item.put("auther","作者："+dataList.get(i).getSinger());
//                item.put("gdd", "需功德点：" + dataList.get(i).getRequiredMeritPoints());
//                item.put("music",dataList.get(i).getMusicUrl());
//                item.put("sc","0");
//                data.add(item);
//            }

//            LightAdapter adapter = new LightAdapter(getApplication(), data);
            MusicAdapter adapter = new MusicAdapter(getApplication(), dataList);
            listView.setAdapter(adapter);
        }

}