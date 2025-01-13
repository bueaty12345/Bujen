package com.yunsong.bujen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alibaba.fastjson.JSON;
import com.yunsong.bujen.adapter.LightAdapter;
import com.google.android.material.tabs.TabLayout;
import com.yunsong.bujen.adapter.MusicAdapter;
import com.yunsong.bujen.databean.MusicBean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lighting extends AppCompatActivity {
    TabLayout tab_dgyx;
    ListView listView;
    ImageView img_back;
    private ConfirmDialog dialog;
    int position=0;
    private final String USER_INFO_URL = BuildConfig.API_SERVER+"/dev-api/system/music/app/list"; //获取音乐接口URL
    List<MusicBean> dataList = new ArrayList<>();
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
                    case 2:
                        setCollection();//收藏
                        position=2;
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
                    intent.putExtra("auther", musicBean.getSinger());
                    intent.putExtra("music", musicBean.getMusicUrl());

// 启动 Activity B
                    startActivity(intent);
                }

//                TextView txt_neme=view.findViewById(R.id.txt_mname),
//                        txt_gdd=view.findViewById(R.id.txt_gdd),
//                        txt_hy=view.findViewById(R.id.txt_hy),
//                        txt_auther=view.findViewById(R.id.txt_auther);
//                ImageView img_hy=view.findViewById(R.id.img_selet);
//
//                Map<String, String> item = (Map<String, String>) adapterView.getAdapter().getItem(i);
//                Toast.makeText(Lighting.this, ""+item.get("name"), Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void init(){
        tab_dgyx=findViewById(R.id.tab_dgyx);
        listView=findViewById(R.id.list_dgyx);
        img_back=findViewById(R.id.img_back);


        setSound();
    }
    private void setCollection() {
        // 准备数据
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("name", "收藏 " + i);
            item.put("auther","作者："+i);
            item.put("gdd", "需功德点：" + i*1000);
            item.put("sc","1");
            data.add(item);
        }
        LightAdapter adapter = new LightAdapter(this, data);
        listView.setAdapter(adapter);
    }

    private void setLamplight() {
// 准备数据
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, String> item = new HashMap<>();
            item.put("name", "灯光 " + i);
            item.put("auther","作者："+i);
            item.put("gdd", "需功德点：" + i*1000);
            item.put("sc","0");
            data.add(item);
        }

        // 创建适配器
        String[] from = {"name", "gdd"}; // 数据源的键
        int[] to = {R.id.txt_mname, R.id.txt_gdd}; // 布局文件中的视图 ID
        LightAdapter adapter = new LightAdapter(this, data);
        listView.setAdapter(adapter);
    }

    private void setSound() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("user_token", null);  // 从 SharedPreferences 获取 token
        new GetMusicInfoTask().execute(token);

    }
    private Boolean showDialog() {
        final Boolean[] on = {false};
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(this);
        dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        on[0] =true;
                        dialog.dismiss();  // 这里添加取消对话框的代码
                    }
                })
                .build();
        dialog.show();
        return on[0];
    }



    private class GetMusicInfoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String token = params[0];

            try {
                URL url = new URL(USER_INFO_URL);  // 获取用户信息的 URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);  // 将 token 放在 Authorization 头部

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString(); // 返回响应内容
                } else {
                    return "Request failed with response code: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.startsWith("Error:")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonResponse = new JSONObject(result);
                    String json=jsonResponse.getString("rows");
                    // 你可以根据接口返回的字段设置用户的其他信息
                    dataList.clear();
                    // 将 JSON 数组转换为 List<MyBean>
                    dataList = JSON.parseArray(json, MusicBean.class);
                    Log.d("RegisterTask", "dataList: " + dataList.get(0).toString());
                    Log.d("RegisterTask", "dataList: " + dataList.get(1).toString());
                    Log.d("RegisterTask", "dataList: " + dataList.get(2).toString());
                    updateUIWithUserInfo(dataList);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d("RegisterTask", "Response: " + result);
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
}