package com.yunsong.bujen.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;
import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.R;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PhoneNumberBind extends AppCompatActivity implements View.OnClickListener{
    private EditText etPhoneOld, etPhoneNew, etCode;
    private TextView txtGetCode;
    private Context mContext;
    private LinearLayout txtBind;
    private final String USER_INFO_URL= BuildConfig.API_SERVER+"/system/users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bind_new_phone);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    private void init(){
        mContext = this;

        etPhoneOld = findViewById(R.id.et_phone);
        etPhoneNew = findViewById(R.id.et_phone_new);
        etCode = findViewById(R.id.et_code);
        txtGetCode = findViewById(R.id.txt_code);
        txtBind = findViewById(R.id.bind_PhoneNumber);

        findViewById(R.id.img_back).setOnClickListener(v -> finish());
        txtGetCode.setOnClickListener(v -> sendVerificationCode());
        txtBind.setOnClickListener(v -> changePhoneNumber());
    }

    private void sendVerificationCode() {
        String newPhone = etPhoneNew.getText().toString().trim();

        if (TextUtils.isEmpty(newPhone)) {
            Toast.makeText(mContext, "请输入新手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        ThingHomeSdk.getUserInstance().sendVerifyCodeWithUserName(
                newPhone,
                ThingHomeSdk.getUserInstance().getUser().getDomain().getRegionCode(),
                "86",
                7,
                new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(mContext, "发送失败：" + error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "验证码已发送", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void changePhoneNumber() {
        String newPhone = etPhoneNew.getText().toString().trim();
        String code = etCode.getText().toString().trim();

        if (TextUtils.isEmpty(newPhone) || TextUtils.isEmpty(code)) {
            Toast.makeText(mContext, "请输入完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        ThingHomeSdk.getUserInstance().changeUserName(
                "86",
                code,
                ThingHomeSdk.getUserInstance().getUser().getSid(),
                newPhone,
                new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(mContext, "换绑失败：" + error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(mContext, "手机号换绑成功", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        sharedPreferences.edit().putString("user_phone", newPhone).apply();

                        updateUserPhoneToServer(newPhone);

                        // 返回并刷新
                        setResult(RESULT_OK);  // 标记换绑成功
                        finish();
                    }
                }
        );
    }

    private void updateUserPhoneToServer(String newPhone) {
        int userId=UserInfoUtils.getUserId(this);
        String token=UserInfoUtils.getToken(this);
        try {
            JSONObject json = new JSONObject();
            json.put("id", userId);
            json.put("phone", newPhone);

            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(USER_INFO_URL)
                    .put(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(mContext, "手机号上传失败", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        runOnUiThread(() ->
                                Toast.makeText(mContext, "手机号已同步后台", Toast.LENGTH_SHORT).show()
                        );
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(mContext, "后台更新失败：" + response.message(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
