package com.yunsong.bujen.init;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alibaba.fastjson.JSONObject;
import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.R;
import com.thingclips.smart.android.user.api.ILoginCallback;
import com.thingclips.smart.android.user.api.IRegisterCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.yunsong.bujen.guidance.AgeActivity;
import com.yunsong.bujen.utils.UserAuthManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity implements View.OnClickListener{
    ImageView img_back,img_eye;
    LinearLayout lin_code;
    Button btn_add;
    EditText edit_phone,edit_password;
    String phone=null,uid,code;
    private boolean isPasswordVisible = false;
    private final String REGISTER_URL = BuildConfig.API_SERVER+"/app/register"; //注册接口URL
    private final String LOGIN_URL = BuildConfig.API_SERVER + "/app/login"; // 登录接口
    private UserAuthManager userAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ThingHomeSdk.init(this.getApplication());
        userAuthManager = new UserAuthManager(this);
        init();


    }
    private void init(){
        img_back=findViewById(R.id.img_back);
        img_eye=findViewById(R.id.img_eye);
        lin_code=findViewById(R.id.lin_code);
        btn_add=findViewById(R.id.btn_add);
        edit_phone=findViewById(R.id.edit_phone);
        edit_password=findViewById(R.id.edit_password);

        //获取传输的手机号
        Intent it=getIntent();
        phone=it.getStringExtra("phone");
        code=it.getStringExtra("code");
        if(phone!=null) edit_phone.setText(phone);

        //登录
        btn_add.setOnClickListener(this);
        //返回
        img_back.setOnClickListener(this);
        //密码可见或不可见
        img_eye.setOnClickListener(this);
        //国家代码选择
        lin_code.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        phone=edit_phone.getText().toString();
        String password=edit_password.getText().toString();
        switch (view.getId()){
            case R.id.btn_add:
                if(isValidPassword(password)){
                    doRegisterFlow(phone, password, code);
                }else{
                    Toast.makeText(this, getResources().getString(R.string.no_password), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_eye:
                togglePasswordVisibility();
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_code:
                Toast.makeText(this, "暂未开通", Toast.LENGTH_SHORT).show();
                break;


        }
    }

    //判断密码是否合格
    public boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        return password.matches(passwordPattern);
    }
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            img_eye.setImageResource(R.drawable.icon_eye_slash);
        } else {
            edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            img_eye.setImageResource(R.drawable.icon_eye);
        }
        edit_password.setSelection(edit_password.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void doRegisterFlow(String phone, String password, String code) {
        userAuthManager.registerAndLogin(phone, password, code, new UserAuthManager.ResultCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(Register.this, "注册并登录成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Register.this, AgeActivity.class));
//                creHome();
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Register.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }


//    private void creHome() {
//        List<String> rooms=new ArrayList<>();
//        ThingHomeSdk.getHomeManagerInstance().createHome("myhome", 0, 0, "", rooms, new IThingHomeResultCallback() {
//            @Override
//            public void onSuccess(HomeBean bean) {
//                // do something
//                Toast.makeText(Register.this, "家庭id"+bean.getHomeId(), Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(Register.this, Connect.class));
//            }
//            @Override
//            public void onError(String errorCode, String errorMsg) {
//                Toast.makeText(Register.this, "创建家庭失败: " + errorMsg, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


}