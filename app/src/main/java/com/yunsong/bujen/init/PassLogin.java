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
import android.widget.TextView;
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
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PassLogin extends AppCompatActivity implements View.OnClickListener{
    ImageView img_back,img_eye;
    LinearLayout lin_code;
    TextView txt_wjmm;
    Button btn_add;
    EditText edit_phone,edit_password;
    String phone=null;
    private boolean isPasswordVisible = false;
    private final String REGISTER_URL = BuildConfig.API_SERVER+"/app/login"; //登录接口URL
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pass_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

    }
    void init() {
        img_back=findViewById(R.id.img_back);
        img_eye=findViewById(R.id.img_eye);
        lin_code=findViewById(R.id.lin_code);
        btn_add=findViewById(R.id.btn_add);
        edit_phone=findViewById(R.id.edit_phone);
        edit_password=findViewById(R.id.edit_password);
        txt_wjmm=findViewById(R.id.txt_wjmm);

        //获取传输的手机号
        Intent it=getIntent();
        phone=it.getStringExtra("phone");
        if(phone!=null) edit_phone.setText(phone);

        //验证码登录-忘记密码
        txt_wjmm.setOnClickListener(this);
        //登录或注册
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
                login(phone,password);
                break;
            case R.id.txt_wjmm:
                Intent it=new Intent(PassLogin.this, CaptchaLogin.class);
                if(phone!=null&&!(phone.equals("")))
                    it.putExtra("phone",phone);
                startActivity(it);
                finish(); // 销毁当前 Activity
                break;
            case R.id.img_eye:
                if (isPasswordVisible) {
                    // 隐藏密码
                    edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    img_eye.setImageResource(R.drawable.icon_eye_slash);
                } else {
                    // 显示密码
                    edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    img_eye.setImageResource(R.drawable.icon_eye);
                }
                // 移动光标到文本末尾
                edit_password.setSelection(edit_password.getText().length());
                isPasswordVisible = !isPasswordVisible;
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_code:
                Toast.makeText(this, "暂未开通", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void login(String phone, String password) {
        ThingHomeSdk.getUserInstance().loginWithPhonePassword("86", phone, password, new ILoginCallback() {
            @Override
            public void onSuccess(User user) {
                //注登录成功跳首页
                startActivity(new Intent(PassLogin.this, Connect.class));
                Toast.makeText(PassLogin.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                Log.d("RegisterTask", "uid: " + user.getUid());
                LoginUser(phone,password,user.getUid());
            }

            @Override
            public void onError(String code, String error) {
                Toast.makeText(PassLogin.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void LoginUser(String phone, String password, String uid) {
        // 使用异步任务执行网络请求
        new LoginTask().execute(phone, password,uid);
    }

    // 异步任务进行网络请求
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String phone = params[0];
            String password = params[1];
            String uid = params[2];

            try {
                // 创建URL对象
                URL url = new URL(REGISTER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);

                // 创建JSON请求体
                String jsonInputString = "{\"username\": \"" + phone + "\", \"password\": \"" + password + "\", \"uuid\": \"" + uid + "\"}";

                // 发送请求体
                try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    outputStream.write(input, 0, input.length);
                }

                // 读取响应
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
            // 在UI线程中处理返回的结果
            if (result.startsWith("Error:")) {
                // 处理错误情况
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    // 使用 Fastjson 解析 JSON 响应
                    JSONObject jsonResponse = JSONObject.parseObject(result);
                    String token = jsonResponse.getString("token"); // 假设返回的数据中有 token 字段
                    Log.d("RegisterTask", "Token: " + token);
                    // 获取 SharedPreferences 实例
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

// 存储 Token
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_token", token);  // "push_token" 为存储 token 的键
                    editor.apply();  // 使用 apply() 异步保存

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d("RegisterTask", "Response: " + result);


        }
    }
}