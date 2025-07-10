package com.yunsong.bujen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;
import com.yunsong.bujen.adapter.LightAdapter;
import com.yunsong.bujen.adapter.MyCollectAdapter;
import com.yunsong.bujen.adapter.MyPrayAdapter;
import com.yunsong.bujen.adapter.MyTutorialAdapter;
import com.yunsong.bujen.databean.BlessingBean;
import com.yunsong.bujen.databean.MusicBean;
import com.yunsong.bujen.databean.MyLightBean;
import com.yunsong.bujen.databean.MyMusicBean;
import com.yunsong.bujen.databean.MyPrayBean;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.databean.TutorialBundleBean;
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
    private List<TutorialBundleBean> tutorialList = new ArrayList<>();
    private List<BlessingBean> prayList = new ArrayList<>();
    private List<MyLightBean> lightList = new ArrayList<>();
    private BaseAdapter currentAdapter;

    private int currentTabIndex = 0;


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
                currentTabIndex = tab.getPosition();
                switch (currentTabIndex) {
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
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent;

            switch (currentTabIndex) {
                case 0:
                    MyMusicBean musicBean = (MyMusicBean) parent.getAdapter().getItem(position);
                    intent = new Intent(MyCollect.this, Detail.class);
                    intent.putExtra("name", musicBean.getMusicName());
                    intent.putExtra("gdd", musicBean.getRequiredMeritPoints());
                    intent.putExtra("singer", musicBean.getSinger());
                    intent.putExtra("music", musicBean.getMusicUrl());
                    intent.putExtra("createdAt", musicBean.getCreatedAt());
                    intent.putExtra("rating", String.valueOf(musicBean.getRating()));
                    intent.putExtra("description", musicBean.getDescription());
                    intent.putExtra("sc", musicBean.getSc());
                    intent.putExtra("dh", musicBean.getDh());
                    intent.putExtra("duration", musicBean.getDuration());
                    intent.putExtra("resourceType", musicBean.getResourceType());
                    intent.putExtra("musicId", musicBean.getMusicId());
                    intent.putExtra("musicCover", musicBean.getMusicCover());
                    intent.putExtra("position", position);
                    break;

                case 1:
                    MyLightBean lightBean = (MyLightBean) parent.getAdapter().getItem(position);
                    intent = new Intent(MyCollect.this, Detail2.class);
                    intent.putExtra("backgroundName", lightBean.getBackgroundName());
                    intent.putExtra("requiredMeritPoints", lightBean.getRequiredMeritPoints());
                    intent.putExtra("author", lightBean.getAuthor());
                    intent.putExtra("createdAt", lightBean.getCreatedAt());
                    intent.putExtra("rating", String.valueOf(lightBean.getRating()));
                    intent.putExtra("description", lightBean.getDescription());
                    intent.putExtra("sc", lightBean.isSc());
                    intent.putExtra("dh", lightBean.isDh());
                    intent.putExtra("backgroundImageUrl", lightBean.getBackgroundImageUrl());
                    intent.putExtra("resourceType", lightBean.getResourceType());
                    intent.putExtra("backgroundId", lightBean.getBackgroundId());
                    intent.putExtra("position", position);
                    break;

                case 2:
                    TutorialBundleBean tutorialBean = (TutorialBundleBean) parent.getAdapter().getItem(position);
                    intent = new Intent(MyCollect.this, TutorialDetail.class);
                    intent.putExtra("name", tutorialBean.getName());
                    intent.putExtra("sc", tutorialBean.isSc());
                    intent.putExtra("dh", tutorialBean.isDh());
                    intent.putExtra("description", tutorialBean.getDescription());
                    intent.putExtra("tutorialContent", tutorialBean.getTutorialContent());
                    intent.putExtra("gdd", tutorialBean.getRequiredMeritPoints());
                    intent.putExtra("resourceType", tutorialBean.getResourceType());
                    intent.putExtra("tutorialId", tutorialBean.getId());
                    intent.putExtra("packageUrl",tutorialBean.getPackageUrl());
                    intent.putExtra("position", position);
                    break;

                case 3:
                    BlessingBean selected = (BlessingBean) parent.getAdapter().getItem(position);
                    intent = new Intent(MyCollect.this, PraysDetail.class);
                    intent.putExtra("blessing", selected);
                    intent.putExtra("resourceType", selected.getResourceType());
                    intent.putExtra("blessingId", selected.getBlessingId());
                    intent.putExtra("gdd", selected.getRequiredMeritPoints());
                    intent.putExtra("blessingBackgroundUrl", selected.getBlessingBackgroundUrl());
                    intent.putExtra("blessingMethod", selected.getBlessingMethod());
                    intent.putExtra("position", position);
                    intent.putExtra("exchangeQuantity",selected.getExchangeQuantity());
                    intent.putExtra("blessingMethod",selected.getBlessingMethod());
                    intent.putExtra("blessingCategory",selected.getBlessingCategory());
                    break;

                default:
                    return;
            }

            intent.putExtra("from", "MyCollect");
            startActivityForResult(intent, 1001);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            int position = data.getIntExtra("position", -1);
            if (position != -1 && currentAdapter != null) {
                // 从当前 Adapter 中移除项并刷新
                if (currentAdapter instanceof MyCollectAdapter) {
                    ((MyCollectAdapter) currentAdapter).removeItem(position);
                }
            }
        }
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
                    item.sc = obj.optBoolean("sc");
                    item.dh = obj.optBoolean("dh");
                    item.rating = obj.optDouble("rating");
                    item.resourceType=obj.optString("resourceType");

                    Log.d("音乐","音乐==="+obj);

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
                    TutorialBundleBean item = new TutorialBundleBean();
                    item.name = obj.optString("name");
                    item.createTime = obj.optString("createTime");
                    item.requiredMeritPoints = obj.optInt("requiredMeritPoints");
                    item.sc = obj.optBoolean("sc");
                    item.dh = obj.optBoolean("dh");
                    item.description= obj.getString("description");
                    item.tutorialContent= obj.getString("tutorialContent");
//                    item.author=obj.optString("author");
//                    item.rating=obj.optInt("rating");
                    item.resourceType=obj.optString("resourceType");
                    item.packageUrl= obj.getString("packageUrl");
                    item.id=obj.getInt("id");

                    Log.d("fetchTutorial","fetchTutorial==="+obj);
                    tutorialList.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void fetchPray(String token) {
            try {
                String result = httpGet(PRAY_INFO_URL, token);
                Log.d("fetchTutorial","祈福"+result);
                JSONArray rows = new JSONObject(result).optJSONArray("rows");
                Log.d("fetchPray", "rows size: " + (rows != null ? rows.length() : -1));
                if (rows == null) return;
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject obj = rows.getJSONObject(i);
                    BlessingBean bean = new BlessingBean();
                    bean.blessingId = obj.optInt("blessingId");
                    bean.resourceType = obj.optString("resourceType");
                    bean.blessingCategory = obj.optString("blessingCategory");
                    bean.blessingBackgroundUrl = obj.optString("blessingBackgroundUrl");
                    bean.blessingTheme = obj.optString("blessingTheme");
                    bean.zenQuote = obj.optString("zenQuote");
                    bean.createdAt = obj.optString("createdAt");
                    bean.requiredMeritPoints = obj.optInt("requiredMeritPoints");
                    bean.resourceType=obj.optString("resourceType");
                    bean.blessingId= obj.optInt("blessingId");
                    bean.exchangeQuantity= obj.optInt("exchangeQuantity");
                    bean.blessingMethod= obj.optString("blessingMethod","Text");

                    Log.d("fetchPray","fetchPray==="+obj);
                    prayList.add(bean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void fetchLight(String token) {
            try {
                String result = httpGet(LIGHT_INFO_URL, token);
                Log.d("fetchLight","收藏教程"+result);
                JSONArray rows = new JSONObject(result).optJSONArray("rows");
                if (rows == null) return;
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject obj = rows.getJSONObject(i);
                    MyLightBean item = new MyLightBean();
                    item.backgroundName = obj.optString("backgroundName");
                    item.author = obj.optString("author");
                    item.backgroundImageUrl = obj.optString("backgroundImageUrl");
                    item.requiredMeritPoints = obj.optInt("requiredMeritPoints");
                    item.sc = obj.optBoolean("sc");
                    item.dh = obj.optBoolean("dh");
                    item.rating = obj.optDouble("rating");
                    item.resourceType=obj.optString("resourceType");
                    item.backgroundId= obj.getInt("backgroundId");

                    Log.d("fetchLight","fetchLight==="+obj);
                    lightList.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        currentAdapter = new MyCollectAdapter(this, new ArrayList<>(lightList));
        listView.setAdapter(currentAdapter);
    }

    private void init(){
        tab_local=findViewById(R.id.tab_local);
        listView=findViewById(R.id.list_local);
        img_back=findViewById(R.id.img_back);
        String token = UserInfoUtils.getToken(this);
        new FetchAllDataTask().execute(token);
    }
}