package com.yunsong.bujen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;
import com.yunsong.bujen.adapter.MyTutorialAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyTutorial extends AppCompatActivity {
    TabLayout tab_local;
    ListView listView;
    ImageView img_back;

    private final String TUTORIAL_URL = BuildConfig.API_SERVER+"/system/tutorial/app/myList";

    private final Map<String, List<MyTutorialBean>> categoryMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mytutorial);
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
                    case 0: showCategory("1"); break; // 减压
                    case 1: showCategory("2"); break; // 疗愈
                    case 2: showCategory("3"); break; // 白噪音
                    case 3: showCategory("4"); break; // 情绪管理
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
        tab_local=findViewById(R.id.tab_local);
        listView=findViewById(R.id.list_local);
        img_back=findViewById(R.id.img_back);
        String token = UserInfoUtils.getToken(this);
        new FetchTutorialTask().execute(token);
    }

    private void showCategory(String categoryId) {
        List<MyTutorialBean> list = categoryMap.getOrDefault(categoryId, new ArrayList<>());
        listView.setAdapter(new MyTutorialAdapter(this, list));
    }

    private class FetchTutorialTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String token = params[0];
            try {
                URL url = new URL(TUTORIAL_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return "Error: Request failed with code: " + responseCode;
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
                Log.d("FetchMusicTask", "我的教程Raw result: " + result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray rows = jsonObject.getJSONArray("rows");

                // 清空旧数据
                categoryMap.clear();

                for (int i = 0; i < rows.length(); i++) {
                    JSONObject obj = rows.getJSONObject(i);
                    MyTutorialBean item = new MyTutorialBean();
                    item.tutorialName = obj.optString("tutorialName");
                    item.createdAt = obj.optString("createdAt");
                    item.sc = obj.optInt("sc");
                    item.requiredMeritPoints = obj.optInt("requiredMeritPoints");
                    item.tutorialCategory=obj.optString("tutorialCategory");
                    // 按分类存入Map
                    List<MyTutorialBean> list = categoryMap.getOrDefault(item.tutorialCategory, new ArrayList<>());
                    list.add(item);
                    categoryMap.put(item.tutorialCategory, list);
                }

                showCategory("1");
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "解析数据失败", Toast.LENGTH_SHORT).show();
            }

        }
    }


}