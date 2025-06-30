package com.yunsong.bujen;


import static com.yunsong.bujen.fragment.HomeFragment.gdd_cont;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.thing.smart.miniappclient.ThingMiniAppClient;
import com.yunsong.bujen.fragment.CenterFragment;
import com.yunsong.bujen.fragment.HFragment;
import com.yunsong.bujen.fragment.SettingsFragment;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingGetHomeListCallback;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.sdk.api.IDevListener;
import com.thingclips.smart.sdk.api.IThingDevice;
import com.yunsong.bujen.utils.DataStorageUtils;
import com.yunsong.bujen.utils.UserInfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Homepage extends AppCompatActivity implements View.OnClickListener {
    private final String USER_INFO_URL = BuildConfig.API_SERVER+"/getAppInfo"; //获取信息接口URL
    public static boolean isPaused = false;
    public static TextView txt_gdd;
    ImageView ivMiniApp;
    TextView txt_home,txt_center,txt_setting;
    FrameLayout ly_content;
    LinearLayout ly_center;
    public static RelativeLayout rl_bg;
    public static LinearLayout ly_tab;
    private FragmentManager fManager;//用于管理 Fragment 的切换
    public static long homeId;
    public static IThingDevice mDevice=null;
    public static String homeColor="#ECE0D2";
    public static int homebg=R.drawable.home_bg2;

    private float dX, dY;
    private float touchDownX;
    private long lastClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);//启用沉浸式（边缘到边缘）布局
        setContentView(R.layout.activity_homepage);
        init();
        //获取家庭信息
        getHomeMessage();
        txt_home.setOnClickListener(this);
        txt_center.setOnClickListener(this);
        txt_setting.setOnClickListener(this);
        gdd_cont = DataStorageUtils.getGddCount(Homepage.this);
        Log.d("TokenCheck", "当前本地token: " + UserInfoUtils.getToken(this));

        loadBgFromPrefs();
    }



    private void getDeviceMessage() {
        ThingHomeSdk.newHomeInstance(homeId).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                if(homeBean.getDeviceList().size()>0){
                    Toast.makeText(Homepage.this, "设备获取成功", Toast.LENGTH_SHORT).show();
                    //获取第一个设备id
                    mDevice = ThingHomeSdk.newDeviceInstance(homeBean.getDeviceList().get(0).getDevId());
                    mDevice.registerDevListener(new IDevListener() {
                        /**
                         * DP 数据更新
                         * devId 设备 ID
                         * dpStr 设备发生变动的功能点，为 JSON 字符串，数据格式：{"101": true}
                         */
                        @Override
                        public void onDpUpdate(String devId, String dpStr){//表示功能点的数据更新
                            Toast.makeText(Homepage.this, "00"+dpStr, Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(dpStr);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            // 提取值
                            boolean value101;
                            try {
                                value101 = jsonObject.getBoolean("101");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            if (value101){
                                gdd_cont++;//gdd_cont是全局变量
                                com.yunsong.bujen.utils.DataStorageUtils.saveGddCount(Homepage.this,gdd_cont);
                            }
                        };

                        /**
                         * 设备移除回调
                         * devId 设备 ID
                         */
                        @Override
                        public void onRemoved(String devId){

                        };

                        /**
                         * 设备上下线回调。如果设备断电或断网，服务端将会在3分钟后回调到此方法。
                         * devId  设备 ID
                         * online 是否在线，在线为 true
                         */
                        @Override
                        public void onStatusChanged(String devId, boolean online){
                            Toast.makeText(Homepage.this, "11"+online, Toast.LENGTH_SHORT).show();
                        };

                        /**
                         * 网络状态发生变动时的回调
                         *  devId  设备 ID
                         *  status 网络状态是否可用，可用为 true
                         */
                        @Override
                        public void onNetworkStatusChanged(String devId, boolean status){

                        };

                        /**
                         * 设备信息更新回调
                         * devId  设备 ID
                         */
                        @Override
                        public void onDevInfoUpdate(String devId){
                        }
                    });
                }else {
                    Toast.makeText(Homepage.this, "没有绑定设备", Toast.LENGTH_SHORT).show();
                    //设置默认Fragment
//                    txt_home.setSelected(true);
//                    ConnectFragment connectFragment=new ConnectFragment();
//                    fManager.beginTransaction()
//                            .replace(R.id.ly_content, connectFragment)
//                            .commit();
                }
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(Homepage.this, "初始化设备失败"+errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getHomeMessage() {
        //异步获取当前用户下的所有“家庭”列表，并通过回调接口返回。
        ThingHomeSdk.getHomeManagerInstance().queryHomeList(new IThingGetHomeListCallback() {
        @Override
        public void onSuccess(List<HomeBean> homeBeans) {
            // do something
            homeId=homeBeans.get(0).getHomeId();//// 获取第一个家庭的 ID
            //初始化家庭设备
            getDeviceMessage();
        }
        @Override
        public void onError(String errorCode, String error) {
            // do something
        }
    });
    }

    private void init(){
        txt_home=findViewById(R.id.txt_home);
        txt_center=findViewById(R.id.txt_centre);
        txt_setting=findViewById(R.id.txt_setting);
        txt_gdd=findViewById(R.id.txt_home_gdd);
        ly_content = findViewById(R.id.ly_content);
        ly_center=findViewById(R.id.ly_center);
        ly_tab=findViewById(R.id.ly_tab_bar);
        rl_bg=findViewById(R.id.rl_bg);
        ivMiniApp=findViewById(R.id.iv_miniapp_float);
        fManager = getSupportFragmentManager();//获取 FragmentManager 实例，方便后续进行 Fragment 切换或替换。
        //设置默认Fragment
        txt_home.setSelected(true);
        HFragment homeFragment=new HFragment();//创建首页 Fragment 实例
        fManager.beginTransaction()
                .replace(R.id.ly_content, homeFragment)
                .commit();
//        ly_tab.setBackgroundColor(Color.parseColor(homeColor));
        ly_center.setOnClickListener(this);
        //从本地获取 token 并调用后台接口拉取用户数据

        String token = UserInfoUtils.getToken(this);

        new GetUserInfoTask().execute(token);
        txt_gdd.setText(String.valueOf(com.yunsong.bujen.utils.DataStorageUtils.getGddCount(this)));

        ivMiniApp.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    touchDownX = event.getRawX();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float newX = event.getRawX() + dX;
                    float newY = event.getRawY() + dY;

                    // 限制边界（防止滑出屏幕）
                    View parent = (View) v.getParent();
                    int parentWidth = parent.getWidth();
                    int parentHeight = parent.getHeight();

                    newX = Math.max(0, Math.min(newX, parentWidth - v.getWidth()));
                    newY = Math.max(0, Math.min(newY, parentHeight - v.getHeight() - 100));

                    v.setX(newX);
                    v.setY(newY);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(event.getRawX() - touchDownX) < 20) {
                        long now = System.currentTimeMillis();
                        if (now - lastClickTime > 300) {
                            lastClickTime = now;
                            openMiniApp();
                        }
                    } else {
                        // 滑动触发
                        openMiniApp();
                    }
                    return true;
            }
            return false;
        });
    }

    private void openMiniApp() {
        Bundle params = new Bundle();
        params.putString("from", "homepage_float_ball");
        ThingMiniAppClient
                .coreClient()
                .openMiniAppByAppId(this, "tyfarinynzhfisqswp", null, null);
    }


    //重置所有文本的选中状态
    private void setSelected(){
        txt_home.setSelected(false);
        txt_center.setSelected(false);
        txt_setting.setSelected(false);
        rl_bg.setBackgroundResource(R.drawable.home_bg2);

    }

    @Override
    public void onClick(View view) {
        setSelected();
        switch (view.getId()){
            case R.id.txt_home:
                txt_home.setSelected(true);

                    HFragment homeFragment=new HFragment();
                    fManager.beginTransaction()
                            .replace(R.id.ly_content, homeFragment)
                            .commit();
                rl_bg.setBackgroundResource(homebg);
                break;
            case R.id.txt_centre:
                txt_center.setSelected(true);
                CenterFragment centerFragment=new CenterFragment();
                fManager.beginTransaction()
                        .replace(R.id.ly_content, centerFragment)
                        .commit();
                break;
            case R.id.txt_setting:
                txt_setting.setSelected(true);
                SettingsFragment settingsFragment=new SettingsFragment();
                fManager.beginTransaction()
                        .replace(R.id.ly_content, settingsFragment)
                        .commit();
                break;
            case R.id.ly_center:
                txt_center.setSelected(true);
                CenterFragment center=new CenterFragment();
                fManager.beginTransaction()
                        .replace(R.id.ly_content, center)
                        .commit();
                break;
        }
    }

    //获取用户信息的异步任务类
    //实现了从服务器请求用户信息、解析数据、存储本地以及更新 UI 的流程
    private class GetUserInfoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {//后台线程处理
            String token = params[0];

            try {
                URL url = new URL(USER_INFO_URL);  // 获取用户信息的 URL
                //设置 Bearer Token 认证头，
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + token);  // 将 token 放在 Authorization 头部

                int responseCode = connection.getResponseCode();//判断HTTP是否响应成功
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
        protected void onPostExecute(String result) {//请求结果处理
            super.onPostExecute(result);

            if (result.startsWith("Error:")) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Log.d("RegisterTask", "Raw result: " + result);

                    if (!result.trim().startsWith("{")) {
                        Log.e("RegisterTask", "返回的不是 JSON 对象，无法解析: " + result);
                        return;
                    }

                    // 解析 JSON 数据
                    JSONObject jsonResponse = new JSONObject(result);
                    JSONObject dataObject = jsonResponse.getJSONObject("user");
                    String nickname = dataObject.optString("nickname");
                    String phone = dataObject.optString("phone");
                    String signature = dataObject.optString("signature");
                    String gender = dataObject.optString("gender");
                    String avatar = dataObject.optString("avatar");
                    Integer id = dataObject.optInt("id");

                    // 保存用户信息到 SharedPreferences,本地
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_nickname", nickname);
                    editor.putString("user_phone", phone);
                    editor.putString("user_signature",signature);
                    editor.putString("user_gender",gender);
                    editor.putString("user_avatar",avatar);
                    editor.putInt("id",id);
                    editor.apply();  // 使用 apply() 异步保存

                    // 在 UI 上显示用户信息
                    updateUIWithUserInfo(nickname, phone,signature);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.d("RegisterTask", "Response: " + result);
        }

        private void updateUIWithUserInfo(String nickname, String phone,String signature) {
            // 更新 UI 显示用户信息

        }
    }

    private void loadBgFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("homepage_config", MODE_PRIVATE);
        String bgUrl = prefs.getString("bg_url", null);

        if (bgUrl != null && !bgUrl.isEmpty()) {
            // 用 Glide 加载网络图片，设置为背景
            Glide.with(this)
                    .load(bgUrl)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            rl_bg.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // 清理时可以设置默认背景
                            rl_bg.setBackground(placeholder);
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int gdd = com.yunsong.bujen.utils.DataStorageUtils.getGddCount(this);
        txt_gdd.setText(String.valueOf(gdd));

        loadBgFromPrefs();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        loadBgFromPrefs();
    }


}