package com.yunsong.bujen;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yunsong.bujen.adapter.MedHistoryAdapter;
import com.yunsong.bujen.adapter.MeditationModuleAdapter;
import com.yunsong.bujen.databean.ModuleItem;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.databean.TutorialBundleBean;
import com.yunsong.bujen.utils.DataStorageUtils;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MedLocad extends AppCompatActivity implements View.OnClickListener{
    TextView txt_home_gdd;
    ImageView img_back;
    private RecyclerView myMeditationRecycler;

    private final String URL = BuildConfig.API_SERVER + "/system/tutorial/app/myList2";
    private final String TUTORIAL_LIST_URL = BuildConfig.API_SERVER + "/system/package/app/";
    private final String MED_HISTORY_URL=BuildConfig.API_SERVER+"/system/recordh/personal/history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_med_locad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    private void init(){
        myMeditationRecycler = findViewById(R.id.my_meditation);
        myMeditationRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        RecyclerView medHistoryRecycler = findViewById(R.id.med_history_recycler);
        medHistoryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fetchMedHistory(medHistoryRecycler);

        img_back=findViewById(R.id.img_back);
        txt_home_gdd=findViewById(R.id.txt_home_gdd);
        txt_home_gdd.setText(String.valueOf(DataStorageUtils.getGddCount(this)));

        img_back.setOnClickListener(this);

        fetchData();
    }

    private void fetchMedHistory(RecyclerView recyclerView) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(MED_HISTORY_URL)
                .get()
                .addHeader("Authorization", "Bearer " + UserInfoUtils.getToken(this))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MedLocad.this, "历史记录加载失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                String json = response.body().string();
                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray rows = obj.getJSONArray("rows");
                    List<MyTutorialBean> list = new ArrayList<>();

                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject t = rows.getJSONObject(i);
                        MyTutorialBean bean = new MyTutorialBean();
                        bean.tutorialName = t.optString("tutorialName");
                        bean.author = t.optString("author");
                        bean.tutorialContent = t.optString("tutorialContent");
                        bean.dh = t.optBoolean("dh");
                        bean.sc = t.optBoolean("sc");
                        bean.backgroundMusicUrl=t.optString("backgroundMusicUrl");
                        bean.videoUrl=t.optString("videoUrl");
                        list.add(bean);
                    }

                    runOnUiThread(() -> {
                        recyclerView.setLayoutManager(new LinearLayoutManager(MedLocad.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(new MedHistoryAdapter(MedLocad.this, list));
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fetchData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .get()
                .addHeader("Authorization", "Bearer " + UserInfoUtils.getToken(this))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MedLocad.this, "请求失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(MedLocad.this, "请求错误", Toast.LENGTH_SHORT).show());
                    return;
                }

                String json = response.body().string();
                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray rows = obj.getJSONArray("rows");

                    List<TutorialBundleBean> bundles = new ArrayList<>();
                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject item = rows.getJSONObject(i);
                        TutorialBundleBean bean = new TutorialBundleBean();
                        bean.id = item.optInt("id");
                        bean.name = item.optString("name");
                        bean.level = item.optInt("level");
                        bean.description = item.optString("description");
                        bean.tutorialContent = item.optString("tutorialContent");
                        bean.requiredMeritPoints = item.optInt("requiredMeritPoints");
                        bean.priority = item.optInt("priority");
                        bean.deleted = item.optInt("deleted");
                        bean.sc = item.optBoolean("sc");
                        bean.dh = item.optBoolean("dh");
                        bean.resourceType = item.optString("resourceType");
                        bundles.add(bean);
                    }

                    List<ModuleItem> moduleItemList = new ArrayList<>();
                    for (TutorialBundleBean b : bundles) {
                        ModuleItem item = new ModuleItem();
                        item.title = b.name;
                        item.subtitle = b.description;
                        item.tutorials = new ArrayList<>();
                        moduleItemList.add(item);
                    }

                    fetchTutorialDetails(bundles);
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(MedLocad.this, "解析失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void fetchTutorialDetails(List<TutorialBundleBean> bundles) {
        OkHttpClient client = new OkHttpClient();
        List<ModuleItem> moduleItemList = new ArrayList<>();

        for (TutorialBundleBean bundle : bundles) {
            Request request = new Request.Builder()
                    .url(TUTORIAL_LIST_URL + bundle.id)
                    .get()
                    .addHeader("Authorization", "Bearer " + UserInfoUtils.getToken(this))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(MedLocad.this, "加载教程失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        return;
                    }

                    String json = response.body().string();
                    try {
                        JSONObject obj = new JSONObject(json);
                        JSONArray rows = obj.getJSONArray("rows");
                        List<MyTutorialBean> tutorials = new ArrayList<>();

                        for (int j = 0; j < rows.length(); j++) {
                            JSONObject t = rows.getJSONObject(j);
                            MyTutorialBean tut = new MyTutorialBean();
                            tut.tutorialCategory = t.optString("tutorialCategory");
                            tut.tutorialName = t.optString("tutorialName");
                            tut.author = t.optString("author");
                            tut.videoUrl = t.optString("videoUrl");
                            tut.description=t.optString("description");
                            tut.tutorialContent=t.optString("tutorialContent");
                            tut.backgroundMusicUrl=t.optString("backgroundMusicUrl");
                            tut.tutorialId=t.optInt("tutorialId");
                            tutorials.add(tut);
                        }

                        ModuleItem item = new ModuleItem();
                        item.title = bundle.name;
                        item.subtitle = bundle.description;
                        item.tutorials = tutorials;
                        moduleItemList.add(item);

                        runOnUiThread(() -> myMeditationRecycler.setAdapter(new MeditationModuleAdapter(MedLocad.this, moduleItemList)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
        }
    }
}