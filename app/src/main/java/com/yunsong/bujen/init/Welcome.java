package com.yunsong.bujen.init;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSONObject;
import com.facebook.soloader.SoLoader;
import com.gzl.smart.gzlminiapp.miniapp.GZLMiniAppSDK;
import com.thing.smart.miniappclient.ThingMiniAppClient;
import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.Homepage;
import com.yunsong.bujen.R;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Welcome extends AppCompatActivity {
    Button btn_login;
    ImageView img_hs,imgShu1,imgShu2;
//    LottieAnimationView imgShu2;
private final String REGISTER_URL = BuildConfig.API_SERVER+"/dev-api/app/login"; //登录接口URL
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        ThingHomeSdk.init(this.getApplication());
        init();
    }
    public void init(){
        btn_login=findViewById(R.id.btn_login);
        img_hs=findViewById(R.id.img_hs);
        imgShu1 = findViewById(R.id.img_shu1);
        imgShu2 = findViewById(R.id.img_shu2);
//        // 获取屏幕高度
//        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
//
//// 获取视图的高度（假设 img_hs 是你的 ImageView）
//        int viewHeight = img_hs.getHeight();
//
//// 动画的起始位置从屏幕底部，结束位置为屏幕的中间
//        float startTranslationY = screenHeight/2 - viewHeight;
//        float endTranslationY = screenHeight/20  - viewHeight ;
//
//// 创建 ObjectAnimator，设置从底部到中间的移动
//        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(img_hs, "translationY", startTranslationY, endTranslationY);
//
//// 创建渐变动画 (alpha)，从完全透明到完全不透明
//        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(img_hs, "alpha", 0f, 1f);
//
//// 创建一个 AnimatorSet，两个动画同时执行
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(translationYAnimator, alphaAnimator);
//
//// 设置动画时长（例如：500ms）
//        animatorSet.setDuration(1500);
//
//// 启动动画
//        animatorSet.start();


// 创建 img_hs 缩小的动画
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(img_hs, "scaleX", 1f, 0.84f); // 缩小到 380dp
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(img_hs, "scaleY", 1f, 0.84f);

// 创建 img_shu1 向右平移的动画
        ObjectAnimator translationXAnimator1 = ObjectAnimator.ofFloat(imgShu1, "translationX", 0f, 200f); // 向右平移 200dp

// 创建 img_shu2 向左平移的动画
        ObjectAnimator translationXAnimator2 = ObjectAnimator.ofFloat(imgShu2, "translationX", -150f, -495f); // 向左平移 200dp

// 创建 img_hs 的动画完成后再显示 Button 的渐显动画
        ObjectAnimator fadeInButton = ObjectAnimator.ofFloat(btn_login, "alpha", 0f, 1f);
        fadeInButton.setDuration(1500);  // 设置 Button 渐显动画的时长（500ms）

// 创建一个 AnimatorSet 来同时执行前面的动画，并在动画完成后显示 Button
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, translationXAnimator1, translationXAnimator2);

// 设置动画监听器，在前三个动画结束后开始 Button 渐显动画
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 设置新的高度
                // 获取 img_hs 的布局参数
                ViewGroup.LayoutParams layoutParams = img_hs.getLayoutParams();
                // 设置 img_hs 的高度为 280dp
                float targetHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 380, getResources().getDisplayMetrics());
                layoutParams.height = (int) targetHeight;
//                img_hs.setLayoutParams(layoutParams);  // 更新布局参数
                // 启动渐显动画
                fadeInButton.start();
            }
        });

// 设置所有动画的时长
        animatorSet.setDuration(1500);  // 所有动画的总时长 1000ms

// 启动前三个动画
        animatorSet.start();
//        imgShu2.playAnimation();  // 播放动画

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnim();

            }
        });
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);
                // 动画完成后跳转页面
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lottieAnimationView.setVisibility(View.VISIBLE) ;
                lottieAnimationView.playAnimation();  // 播放动画
            }
        });



    }

    private void setAnim() {
        // 获取屏幕顶部的 Y 坐标（50dp）
        float targetTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());

// 获取当前图片的 top 位置
        float currentTop = img_hs.getTop(); // 获取当前组件距离父布局顶部的距离

// 计算需要上移的距离
        float distanceToMove = currentTop - targetTop;

// 创建 img_hs 缩小的动画
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(img_hs, "scaleX", 0.84f, 0.54f); // 缩小到 0.54
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(img_hs, "scaleY", 0.84f, 0.54f); // 缩小到 0.54

// 创建 img_hs 上移到目标位置的动画
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(img_hs, "translationY", 0f, -distanceToMove); // 上移到距离父布局顶端50dp

// 创建 img_hs 透明度渐变动画（从不透明渐变到透明）
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(img_hs, "alpha", 1f, 0f); // 从完全不透明变为透明


// 创建一个 AnimatorSet 来同时执行缩小、上移和透明度的动画s
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, translationYAnimator,alphaAnimator);

// 设置动画时长
        animatorSet.setDuration(600);  // 动画时长 1200ms

// 设置动画插值器（可以平滑或加速减速动画效果）
        animatorSet.setInterpolator(new DecelerateInterpolator());  // 使用加速减速插值器（可以根据需要更换）



//        // 动画完成后跳转页面
//        animatorSet.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                // 页面跳转
//                if(ThingHomeSdk.getUserInstance().isLogin()){
//                    startActivity(new Intent(Welcome.this, Homepage.class));
//                }else {
//                    startActivity(new Intent(Welcome.this, Login.class));
//                    overridePendingTransition(0, R.anim.anim_zoom_out);
//                }
//            }
//        });

        // 启动动画
        animatorSet.start();
        // 页面跳转
        if(ThingHomeSdk.getUserInstance().isLogin()){
            String uid=ThingHomeSdk.getUserInstance().getUser().getUid();
            LoginUser("","",uid);
        }else {
            startActivity(new Intent(Welcome.this, Login.class));
            overridePendingTransition(0, R.anim.anim_zoom_out);
            finish();
        }
    }
    private void LoginUser(String phone, String password, String uid) {
        // 使用异步任务执行网络请求
        new LoginTask().execute(phone, password,uid);
    }

    // 异步任务进行网络请求，用于在后台线程中执行登录操作。
    //第一个string:...params是传入的参数（用户名，密码，UUID）
    //void：表示不在执行过程中汇报进度
    //String:最终执行完成后返回的结果类型（服务器返回的响应）
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {//在子线程中执行，不能更新 UI。
            String phone = params[0];
            String password = params[1];
            String uid = params[2];

            try {
                // 创建URL对象
                URL url = new URL(REGISTER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//设置请求方式为 POST
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");//设置内容类型为 JSON
                connection.setDoOutput(true);//打开输出流用于传输数据

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

        //用于在网络请求完成后在主线程中处理返回结果、保存登录 token 并跳转页面
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

                    startActivity(new Intent(Welcome.this, Homepage.class));
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d("RegisterTask", "Response: " + result);


        }
    }
}