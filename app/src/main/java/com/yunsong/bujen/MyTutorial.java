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
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
                    case 0: showCategory("减压"); break; // 减压
                    case 1: showCategory("疗愈"); break; // 疗愈
                    case 2: showCategory("白噪音"); break; // 白噪音
                    case 3: showCategory("情绪管理"); break; // 情绪管理
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
        showCategory("减压");
    }


    private void init(){
        tab_local=findViewById(R.id.tab_local);
        listView=findViewById(R.id.list_local);
        img_back=findViewById(R.id.img_back);

        tab_local.getTabAt(0).select();
    }

    private void showCategory(String category) {
        String token = UserInfoUtils.getToken(this);
        new FetchTutorialTask(category).execute(token);
    }


    private class FetchTutorialTask extends AsyncTask<String, Void, String> {
        private String category;

        public FetchTutorialTask(String category) {
            this.category = category;
        }

        @Override
        protected String doInBackground(String... params) {
            String token = params[0];
            HttpURLConnection connection = null;
            try {
                URL url = new URL(TUTORIAL_URL);  // 不拼接参数到 URL
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + token);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                JSONObject json = new JSONObject();
                json.put("tutorialCategory", category);

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(json.toString());
                writer.flush();
                writer.close();
                os.close();

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
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.startsWith("Error:")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                Log.e("TutorialDebug", "请求失败：" + result);
                return;
            }

            try {
                Log.d("TutorialDebug", "开始解析JSON: " + result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray rows = jsonObject.getJSONArray("rows");

                List<MyTutorialBean> list = new ArrayList<>();

                for (int i = 0; i < rows.length(); i++) {
                    JSONObject obj = rows.getJSONObject(i);
                    MyTutorialBean item = new MyTutorialBean();
                    item.tutorialName = obj.optString("tutorialName");
                    item.createdAt = obj.optString("createdAt");
                    item.sc = obj.optBoolean("sc");
                    item.dh=obj.optBoolean("dh");
                    item.requiredMeritPoints = obj.optInt("requiredMeritPoints");
                    item.tutorialCategory = obj.optString("tutorialCategory");
                    item.backgroundMusicUrl= obj.getString("backgroundMusicUrl");

                    list.add(item);
                }

                listView.setAdapter(new MyTutorialAdapter(MyTutorial.this, list));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("TutorialDebug", "解析失败，原始数据: " + result);
                Toast.makeText(getApplicationContext(), "解析数据失败", Toast.LENGTH_SHORT).show();
            }

        }
    }


}