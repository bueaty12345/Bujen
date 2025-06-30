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
    String phone=null,code="0";
    private boolean isPasswordVisible = false;
    private final String REGISTER_URL = BuildConfig.API_SERVER+"/app/register"; //注册接口URL

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
                    //涂鸦注册
                    toRegister(phone,password,code);
                }else Toast.makeText(this, getResources().getString(R.string.no_password), Toast.LENGTH_SHORT).show();
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

    private void toRegister(String Phone, String password, String code) {
        //电话号码注册
        ThingHomeSdk.getUserInstance().registerAccountWithPhone("86",phone,password,code, new IRegisterCallback() {
            @Override
            public void onSuccess(User user) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.reg_succes), Toast.LENGTH_SHORT).show();

                //后台注册
                registerUser(phone,password,user.getUid());
                //登录
                login(Phone,password);
            }
            @Override
            public void onError(String code, String error) {
                Toast.makeText(getApplicationContext(),  "1error:" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String phone, String password) {
        ThingHomeSdk.getUserInstance().loginWithPhonePassword("86", phone, password, new ILoginCallback() {
            @Override
            public void onSuccess(User user) {
                LoginUser(phone,password,user.getUid());
                //注册成功后默认创建一个家庭
                creHome();
            }

            @Override
            public void onError(String code, String error) {
                Toast.makeText(getApplicationContext(), "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void creHome() {
        List<String> rooms=new ArrayList<>();
        ThingHomeSdk.getHomeManagerInstance().createHome("myhome", 0, 0, "", rooms, new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean bean) {
                // do something
                Toast.makeText(Register.this, "家庭id"+bean.getHomeId(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Register.this, Connect.class));
            }
            @Override
            public void onError(String errorCode, String errorMsg) {
                // do something
            }
        });
    }

    //判断密码是否合格
    public boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        return password.matches(passwordPattern);
    }
    private void registerUser(String phone, String password, String uid) {
        // 使用异步任务执行网络请求
        new RegisterTask().execute(phone, password,uid);
    }

    // 异步任务进行网络请求
    private class RegisterTask extends AsyncTask<String, Void, String> {

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
                    String msg = jsonResponse.getString("msg"); // 假设返回的数据中有 token 字段
                    Log.d("RegisterTask", "msg: " + msg);
                    Toast.makeText(Register.this, ""+msg, Toast.LENGTH_SHORT).show();
//                    // 获取 SharedPreferences 实例
//                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
//
//// 存储 Token
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("push_token", token);  // "push_token" 为存储 token 的键
//                    editor.apply();  // 使用 apply() 异步保存

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d("RegisterTask", "Response: " + result);


        }
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