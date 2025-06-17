package com.yunsong.bujen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;
import com.yunsong.bujen.adapter.LightAdapter;
import com.yunsong.bujen.adapter.MyCollectAdapter;
import com.yunsong.bujen.adapter.MyPrayAdapter;
import com.yunsong.bujen.adapter.MyTutorialAdapter;
import com.yunsong.bujen.databean.MyMusicBean;
import com.yunsong.bujen.databean.MyPrayBean;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyCollect extends AppCompatActivity {
    TabLayout tab_local;
    ListView listView;
    ImageView img_back;

    private final String MUSIC_INFO_URL = BuildConfig.API_SERVER+"/system/favorites/app/myMusic";
    private final String LIGHT_INFO_URL=BuildConfig.API_SERVER+"/system/favorites/app/myBackground";
    private final String TUTORIAL_INFO_URL=BuildConfig.API_SERVER+"/system/favorites/app/myTutorial";
    private final String PRAY_INFO_URL=BuildConfig.API_SERVER+"/system/favorites/app/myBlessing";

    private List<MyMusicBean> musicList = new ArrayList<>();
    private List<MyTutorialBean> tutorialList = new ArrayList<>();
    private List<MyPrayBean> prayList = new ArrayList<>();
//    private List<MyLightBean> lightList = new ArrayList<>();
    private BaseAdapter currentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mycollect);
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
                        setMusic();
                        break;
                    case 1:
                        setLight();
                        break;
                    case 2:
                        setTutorial();
                        break;
                    case 3:
                        setPray();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class FetchAllDataTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String token = params[0];
            fetchMusic(token);
            fetchTutorial(token);
            fetchPray(token);
            fetchLight(token);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            setMusic();
        }

        private void fetchMusic(String token) {
            try {
                String result = httpGet(MUSIC_INFO_URL, token);
                JSONArray rows = new JSONObject(result).optJSONArray("rows");
                if (rows == null) return;
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject obj = rows.getJSONObject(i);
                    MyMusicBean item = new MyMusicBean();
                    item.musicName = obj.optString("musicName");
                    item.singer = obj.optString("singer");
                    item.duration = obj.optInt("duration");
                    item.musicCover = obj.optString("musicCover");
                    item.musicUrl = obj.optString("musicUrl");
                    item.requiredMeritPoints = obj.optInt("requiredMeritPoints");
                    item.sc = obj.optInt("sc", 0);
                    item.dh = obj.optInt("dh", 0);
                    item.rating = obj.optDouble("rating");
                    musicList.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void fetchTutorial(String token) {
            try {
                String result = httpGet(TUTORIAL_INFO_URL, token);
                Log.d("fetchTutorial","收藏教程"+result);
                JSONArray rows = new JSONObject(result).optJSONArray("rows");
                if (rows == null) return;
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject obj = rows.getJSONObject(i);
                    MyTutorialBean item = new MyTutorialBean();
                    item.tutorialName = obj.optString("tutorialName");
                    item.createdAt = obj.optString("createdAt");
                    item.requiredMeritPoints = obj.optInt("requiredMeritPoints");
                    item.sc = obj.optInt("sc", 0);
                    item.dh = obj.optInt("dh", 0);
                    item.author=obj.optString("author");
                    tutorialList.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void fetchPray(String token) {
            try {
                String result = httpGet(PRAY_INFO_URL, token);
                JSONArray rows = new JSONObject(result).optJSONArray("rows");
                if (rows == null) return;
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject obj = rows.getJSONObject(i);
                    MyPrayBean bean = new MyPrayBean();
                    bean.blessing_id = obj.optInt("blessingId");
                    bean.resource_type = obj.optString("resourceType");
                    bean.blessing_category = obj.optString("blessingCategory");
                    bean.blessing_background_url = obj.optString("blessingBackgroundUrl");
                    bean.blessing_theme = obj.optString("blessingTheme");
                    bean.zen_quote = obj.optString("zenQuote");
                    bean.created_at = obj.optString("createdAt");
                    bean.required_merit_points = obj.optInt("requiredMeritPoints");
                    prayList.add(bean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void fetchLight(String token) {
//            try {
//                String result = httpGet(LIGHT_INFO_URL, token);
//                JSONArray rows = new JSONObject(result).optJSONArray("rows");
//                if (rows == null) return;
//                for (int i = 0; i < rows.length(); i++) {
//                    JSONObject obj = rows.getJSONObject(i);
//                    MyLightBean item = new MyLightBean();
//                    item.lightName = obj.optString("lightName");
//                    item.requiredMeritPoints = obj.optInt("requiredMeritPoints");
//                    lightList.add(item);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        private String httpGet(String urlStr, String token) throws Exception {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            return result.toString();
        }
    }

    private void setMusic() {
        currentAdapter = new MyCollectAdapter(this, new ArrayList<>(musicList));
        listView.setAdapter(currentAdapter);
    }

    private void setTutorial() {
        currentAdapter = new MyCollectAdapter(this, new ArrayList<>(tutorialList));
        listView.setAdapter(currentAdapter);
    }

    private void setPray() {
        currentAdapter = new MyCollectAdapter(this, new ArrayList<>(prayList));
        listView.setAdapter(currentAdapter);
    }

    private void setLight() {
//        currentAdapter = new MyCollectAdapter(this, new ArrayList<>(lightList));
//        listView.setAdapter(currentAdapter);
    }

    private void init(){
        tab_local=findViewById(R.id.tab_local);
        listView=findViewById(R.id.list_local);
        img_back=findViewById(R.id.img_back);
        String token = UserInfoUtils.getToken(this);
        new FetchAllDataTask().execute(token);
    }
}