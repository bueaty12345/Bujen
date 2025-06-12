package com.yunsong.bujen;

import android.content.SharedPreferences;
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
import com.yunsong.bujen.adapter.LocalLightAdapter;
import com.yunsong.bujen.adapter.LocalMusicAdapter;
import com.yunsong.bujen.adapter.MyTutorialAdapter;

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

    private final String TUTORIAL_URL = BuildConfig.API_SERVER+"/system/favorites/app/myTutorial";

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
                    case 0:
                        setDecompress();//减压
                        break;
                    case 1:
                        setHeal();//疗愈
                    case 2:
                        setWhiteNoise();//白噪音
                    case 3:
                        setEmotion();//情绪管理
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

    private void setEmotion() {
    }

    private void setWhiteNoise() {
    }

    private void setHeal() {
    }

    private void setDecompress() {
        // 准备数据
        String[] title={"尘烟","中心","渐变","梦幻"};
        List<Map<String, Object>> data = new ArrayList<>();
        int[] imageIds = {
                R.drawable.my_tutorial1,
                R.drawable.my_tutorial2,
                R.drawable.my_tutorial3,
                R.drawable.my_tutorial4,
        };
        for (int i = 0; i <title.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name",title[i]);
            item.put("image", imageIds[i]);
            data.add(item);
        }

        // 创建适配器
        MyTutorialAdapter adapter=new MyTutorialAdapter(this,data);
        listView.setAdapter(adapter);
    }

    private void init(){
        tab_local=findViewById(R.id.tab_local);
        listView=findViewById(R.id.list_local);
        img_back=findViewById(R.id.img_back);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = prefs.getString("user_token", null);
        new FetchTutorialTask().execute(token);
        Log.d("MyToken", "token = " + token);

        setDecompress();
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
                    return "请求失败，响应码: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "异常: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("TutorialResponse", result);
            Toast.makeText(MyTutorial.this, "获取成功", Toast.LENGTH_SHORT).show();

        }
    }


}