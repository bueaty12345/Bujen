package com.yunsong.bujen.init;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.yunsong.bujen.Homepage;
import com.yunsong.bujen.R;
import com.thingclips.smart.android.user.api.ILoginCallback;
import com.thingclips.smart.android.user.api.IRegisterCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CaptchaLogin extends AppCompatActivity implements View.OnClickListener{
    ImageView img_back;
    LinearLayout lin_code;
    TextView txt_pass,txt_code;
    Button btn_add;
    EditText edit_phone,edit_code;
    String phone=null;
    boolean isL=true;
    private CountDownTimer countdownTimer;
    private boolean isCountingDown = false;
    private static final long COUNTDOWN_TIME_IN_MILLIS = 60000; // 60秒
    private final String REGISTER_URL = BuildConfig.API_SERVER+"/app/login"; //登录接口URL
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_captcha_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        init();
    }
    void init() {
        img_back=findViewById(R.id.img_back);
        lin_code=findViewById(R.id.lin_code);
        txt_pass=findViewById(R.id.txt_pass);
        btn_add=findViewById(R.id.btn_add);
        edit_phone=findViewById(R.id.edit_phone);
        edit_code=findViewById(R.id.edit_code);
        txt_code=findViewById(R.id.txt_code);

        //获取传输的手机号
        Intent it=getIntent();
        phone=it.getStringExtra("phone");
        if(phone!=null) edit_phone.setText(phone);
        //密码登录
        txt_pass.setOnClickListener(this);
        //获取验证码
        txt_code.setOnClickListener(this);
        //登录或注册
        btn_add.setOnClickListener(this);
        //返回
        img_back.setOnClickListener(this);
        //国家代码选择
        lin_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        phone=edit_phone.getText().toString();
        switch (view.getId()){
            case R.id.btn_add:
                //判断是否为登录
                if(isL)  isCode(phone,edit_code.getText().toString(),2);
                else isCode(phone,edit_code.getText().toString(),1);
                break;
            case R.id.txt_pass:
                Intent it=new Intent(CaptchaLogin.this, PassLogin.class);
                if(phone!=null&&!(phone.equals("")))
                    it.putExtra("phone",phone);
                startActivity(it);
                finish(); // 销毁当前 Activity
                break;
            case R.id.txt_code:
                if (!isCountingDown) {
                    setCode(phone, 2);
                }
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_code:
                Toast.makeText(this, "暂未开通", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setCode(String phone, int type) {

        // 获取手机验证码
        ThingHomeSdk.getUserInstance().sendVerifyCodeWithUserName(phone, "", "86", type, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                if (error.equals("用户不存在")){
                    Toast.makeText(CaptchaLogin.this, getResources().getString(R.string.login_reg), Toast.LENGTH_SHORT).show();
                    //获取登录验证码跳登录
                    isL=false;
                    setCode(phone,1);
//                    login(phone,code);
                }else {
                    Toast.makeText(CaptchaLogin.this, "error:" + error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess() {
                Toast.makeText(CaptchaLogin.this, getResources().getString(R.string.code_success), Toast.LENGTH_SHORT).show();
                //验证码倒计时
                isCountingDown = true;
                txt_code.setEnabled(false);
                txt_code.setAlpha(0.5f);

                countdownTimer = new CountDownTimer(COUNTDOWN_TIME_IN_MILLIS, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int secondsLeft = (int) (millisUntilFinished / 1000);
                        txt_code.setText(secondsLeft + "s");
                    }

                    @Override
                    public void onFinish() {
                        txt_code.setText(getResources().getString(R.string.hq_code));
                        txt_code.setEnabled(true);
                        isCountingDown = false;
                        txt_code.setAlpha(1f);
                    }
                }.start();
            }
        });
    }

    private void toRegister(String Phone1, String password, String code) {

        ThingHomeSdk.getUserInstance().registerAccountWithPhone("86",Phone1,password,code, new IRegisterCallback() {
            @Override
            public void onSuccess(User user) {
                //注册成功后创建家庭跳设置页面
            }
            @Override
            public void onError(String code1, String error) {
                Toast.makeText(getApplicationContext(), "code: " + code1 + "error:" + error, Toast.LENGTH_SHORT).show();
                //注册失败转登录
                if(error.equals("密码为空")){

                }else if (error.equals("提示用户已存在")){
                    //跳登录
                    login(phone,code);
                }
            }
        });
    }
    private void login(String phone, String code) {
        // 手机验证码登录
        ThingHomeSdk.getUserInstance().loginWithPhone("86", phone, code, new ILoginCallback() {
            @Override
            public void onSuccess(User user) {
                //注登录成功跳首页
                startActivity(new Intent(CaptchaLogin.this, Homepage.class));
//                Toast.makeText(getApplicationContext(), "登录成功，用户名：" +ThingHomeSdk.getUserInstance().getUser().getUsername(), Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                LoginUser(phone,"",user.getUid());
            }
            @Override
            public void onError(String code1, String error) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    void isCode(String phone,String code1,int type){
        //校验验证码
        ThingHomeSdk.getUserInstance().checkCodeWithUserName(phone,"","86",code1,type, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Toast.makeText(CaptchaLogin.this, "error:"+error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                //判断是否为登录
                if(isL)  login(phone,code1);
                else {
                    //跳设置密码页面
                    Intent it=new Intent(CaptchaLogin.this, Register.class);
                    it.putExtra("phone",phone);
                    it.putExtra("code",code1);
                    startActivity(it);
                }
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