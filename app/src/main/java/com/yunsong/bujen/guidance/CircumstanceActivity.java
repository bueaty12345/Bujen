package com.yunsong.bujen.guidance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.R;
import com.yunsong.bujen.init.Connect;
import com.yunsong.bujen.init.Register;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CircumstanceActivity extends AppCompatActivity {

    private String selectedAge = "";
    private String selectedGender = "";
    private String selectedStatus = "";
    private List<String> selectedStatusList = new ArrayList<>();
    private TextView btnContinue;
    private ImageView img_back;

    private final String MENTAL_URL = BuildConfig.API_SERVER+"/system/mental";

    private static final Map<String, String> fieldMap = new HashMap<String, String>() {{
        put("压力", "stress");
        put("失恋", "heartbreak");
        put("暴躁", "irritability");
        put("空虚", "emptiness");
        put("厌世", "tiredOfLife");
        put("失眠", "insomnia");
        put("抑郁", "hasDepression");
    }};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circumstance_selection);

        // 接收前面页面传过来的数据
        selectedAge = getIntent().getStringExtra("selected_age");
        selectedGender = getIntent().getStringExtra("selected_gender");
        selectedStatus= getIntent().getStringExtra("selected_status");

        btnContinue = findViewById(R.id.btn_continue);
        img_back=findViewById(R.id.img_back);
        img_back.setOnClickListener(v -> {
            finish();
        });


        TextView[] options = {
                findViewById(R.id.option1),
                findViewById(R.id.option2),
                findViewById(R.id.option3),
                findViewById(R.id.option4),
                findViewById(R.id.option5),
                findViewById(R.id.option6),
                findViewById(R.id.option7),
        };

        // 设置点击事件，控制选中状态
        for (TextView option : options) {
            option.setOnClickListener(v -> {
                boolean selected = v.isSelected();
                v.setSelected(!selected);

                String text = ((TextView) v).getText().toString();
                if (!selected) {
                    selectedStatusList.add(text);
                } else {
                    selectedStatusList.remove(text);
                }
            });
        }

        // 点击“确定”按钮
        btnContinue.setOnClickListener(v -> {
            if (selectedStatusList.isEmpty()) {
                Toast.makeText(CircumstanceActivity.this, "请至少选择一个具体情况", Toast.LENGTH_SHORT).show();
            } else {
//                // 拼接所有选择情况
//                String selectedCircumstance = TextUtils.join(",", selectedStatusList);
//
//                Intent intent = new Intent(CircumstanceActivity.this, Connect.class);
//                intent.putExtra("selected_age", selectedAge);
//                intent.putExtra("selected_gender", selectedGender);
//                intent.putExtra("selected_status", selectedStatus);
//                intent.putExtra("selected_circumstance", selectedCircumstance);
//                startActivity(intent);
                uploadMentalStatus();
            }
        });
    }

    private void uploadMentalStatus() {
        new Thread(() -> {
            try {
                // 获取 token 和 userId
                String token = UserInfoUtils.getToken(this);
                int userId =UserInfoUtils.getUserId(this);

                Log.d("用户token","token="+token);
                Log.d("用户ID","ID="+userId);
                if (TextUtils.isEmpty(token) || userId == -1) {
                    runOnUiThread(() -> Toast.makeText(CircumstanceActivity.this, "用户未登录或信息缺失", Toast.LENGTH_SHORT).show());
                    return;
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userId", userId);
                jsonObject.put("ageGroup", Integer.parseInt(selectedAge));
                jsonObject.put("gender", Integer.parseInt(selectedGender));
                jsonObject.put("mutual", Integer.parseInt(selectedStatus));
                jsonObject.put("reportTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

                // 默认所有状态字段为 0
                for (String field : fieldMap.values()) {
                    jsonObject.put(field, 0);
                }

                // 选中的状态设置为 1
                for (String selected : selectedStatusList) {
                    String field = fieldMap.get(selected);
                    if (field != null) {
                        jsonObject.put(field, 1);
                    }
                }

                Log.d("uploadMentalStatus", "Request Body: " + jsonObject.toString());

                // 建立连接
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(
                        jsonObject.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );
                Request request = new Request.Builder()
                        .url(MENTAL_URL)
                        .addHeader("Authorization", "Bearer " + token)
                        .post(requestBody)
                        .build();

                // 执行请求
                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "";

                Log.d("OkHttp response", responseBody);
                Log.d("OkHttp responseCode", String.valueOf(response.code()));

                if (response.isSuccessful()) {
                    JSONObject result = new JSONObject(responseBody);
                    int code = result.optInt("code", 0);
                    String message = result.optString("message", "未知错误");

                    runOnUiThread(() -> {
                        if (code == 200) {
                            Toast.makeText(CircumstanceActivity.this, "状态提交成功", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(CircumstanceActivity.this, Connect.class));
                            creHome();

                        } else {
                            Toast.makeText(CircumstanceActivity.this, "提交失败: " + message, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(CircumstanceActivity.this, "请求失败: " + response.code(), Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(CircumstanceActivity.this, "异常: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void creHome() {
        List<String> rooms=new ArrayList<>();
        ThingHomeSdk.getHomeManagerInstance().createHome("myhome", 0, 0, "", rooms, new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                // do something
                Toast.makeText(CircumstanceActivity.this, "家庭id"+bean.getHomeId(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CircumstanceActivity.this, Connect.class));
            }
            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(CircumstanceActivity.this, "创建家庭失败: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
