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
import com.yunsong.bujen.R;
import com.yunsong.bujen.utils.UserInfoUtils;

public class PhoneNumberBind extends AppCompatActivity implements View.OnClickListener{
    private EditText etPhoneOld, etPhoneNew, etCode;
    private TextView txtGetCode;
    private Context mContext;
    private LinearLayout txtBind;


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

                        // 返回并刷新
                        setResult(RESULT_OK);  // 标记换绑成功
                        finish();
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.img_back:
//                finish();
//                break;

//        }
    }
}
