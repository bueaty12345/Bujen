package com.yunsong.bujen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.adapter.LocalLightAdapter;
import com.yunsong.bujen.adapter.LocalMusicAdapter;
import com.google.android.material.tabs.TabLayout;
import com.yunsong.bujen.databean.MusicBean;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.databean.MyMusicBean;
import com.yunsong.bujen.utils.CommonFetchTask;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Local extends AppCompatActivity {
    TabLayout tab_local;
    ListView listView;
    ImageView img_back;
    int position=0;

    private final String MUSIC_INFO_URL = BuildConfig.API_SERVER+"/system/music/app/myList";
    private final String LIGHT_INFO_URL=BuildConfig.API_SERVER+"/system/background/app/myList";

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
                        setMusic();//音乐
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
    }

    private class FetchMusicTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String token = params[0];
            try {
                URL url = new URL(MUSIC_INFO_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return "Error: Request failed with code " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.startsWith("Error:")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                Log.e("FetchMusicTask", result);
                return;
            }

            try {
                Log.d("FetchMusicTask", "本地音乐Raw result: " + result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray rows = jsonObject.getJSONArray("rows");


                List<MyMusicBean> data = new ArrayList<>();
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject obj = rows.getJSONObject(i);
                    MyMusicBean item = new MyMusicBean();
                    item.musicName = obj.optString("musicName");
                    item.singer = obj.optString("singer");
                    item.duration = obj.optInt("duration");
                    item.musicCover = obj.optString("musicCover");
                    item.musicUrl = obj.optString("musicUrl");
                    item.requiredMeritPoints = obj.optInt("requiredMeritPoints");
                    item.sc = obj.optBoolean("sc");
                    item.dh = obj.optBoolean("dh");
                    item.rating=obj.optDouble("rating");
                    data.add(item);
                }

//                LocalMusicAdapter adapter = new LocalMusicAdapter(Local.this, data);
//                listView.setAdapter(adapter);
                new Handler(Looper.getMainLooper()).post(() -> {
                    listView.setAdapter(new LocalMusicAdapter(Local.this, data));
                });
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "解析音乐数据错", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void init(){
        tab_local=findViewById(R.id.tab_local);
        listView=findViewById(R.id.list_local);
        img_back=findViewById(R.id.img_back);

        setMusic();

    }
    private void setLamplight() {
        String token = UserInfoUtils.getToken(this);
        new CommonFetchTask<>(this, LIGHT_INFO_URL,
                rows -> {
                    List<MyLightBean> data = new ArrayList<>();
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject obj = rows.getJSONObject(i);
                        MyLightBean item = new MyLightBean();
                        item.backgroundId = obj.optInt("backgroundId");
                        item.resourceType = obj.optString("resourceType");
                        item.backgroundImageUrl = obj.optString("backgroundImageUrl");
                        item.backgroundName = obj.optString("backgroundName");
                        item.author = obj.optString("author");
                        item.rating = obj.optDouble("rating");
                        item.description = obj.optString("description");
                        item.createdAt = obj.optString("createdAt");
                        item.requiredMeritPoints = obj.optInt("requiredMeritPoints");
                        item.sc = obj.optBoolean("sc");
                        item.dh = obj.optBoolean("dh");
                        data.add(item);
                    }
                    return data;
                },
                data -> listView.setAdapter(new LocalLightAdapter(Local.this, data)),
                "LightFetch"
        ).execute(token);
    }

    private void setMusic() {
        String token = UserInfoUtils.getToken(this);
        new CommonFetchTask<>(this, MUSIC_INFO_URL,
                rows -> {
                    List<MyMusicBean> data = new ArrayList<>();
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject obj = rows.getJSONObject(i);
                        MyMusicBean item = new MyMusicBean();
                        item.musicName = obj.optString("musicName");
                        item.singer = obj.optString("singer");
                        item.duration = obj.optInt("duration");
                        item.musicCover = obj.optString("musicCover");
                        item.musicUrl = obj.optString("musicUrl");
                        item.requiredMeritPoints = obj.optInt("requiredMeritPoints");
                        item.sc = obj.optBoolean("sc");
                        item.dh = obj.optBoolean("dh");
                        item.rating = obj.optDouble("rating");
                        data.add(item);
                    }
                    return data;
                },
                data -> listView.setAdapter(new LocalMusicAdapter(Local.this, data)),
                "MusicFetch"
        ).execute(token);

    }

}