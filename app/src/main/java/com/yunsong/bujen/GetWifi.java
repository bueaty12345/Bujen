package com.yunsong.bujen;

import static com.yunsong.bujen.Homepage.homeId;
import static com.yunsong.bujen.init.Connect.mScanDeviceBean;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.yunsong.bujen.init.Ssound;

import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingGetHomeListCallback;
import com.thingclips.smart.sdk.api.IMultiModeActivatorListener;
import com.thingclips.smart.sdk.api.IThingActivatorGetToken;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.thingclips.smart.sdk.bean.MultiModeActivatorBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetWifi extends AppCompatActivity implements View.OnClickListener{
    EditText wifi_name,wifi_password;
    Button btn_add;
    ImageView img_back,img_eye,img_add;
    ListView listView;
    TextView ing;
    private boolean isPasswordVisible = false;
    Dialog mCameraDialog;
    LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_wifi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

    }
    private void init(){
        img_back=findViewById(R.id.img_back);
        listView=findViewById(R.id.list_connect);

        setlist();

        //返回
        img_back.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setDialog();
                img_add=view.findViewById(R.id.img_selet);
                ing=view.findViewById(R.id.txt_ing);
                lottieAnimationView=view.findViewById(R.id.lottieAnimationView);
            }
        });
//        img_add.setOnClickListener(this);


    }

    private void setlist() {
// 准备数据
        List<Map<String, Object>> data = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("name", mScanDeviceBean.getProductId());
            data.add(item);

        // 创建适配器
        String[] from = {"name"}; // 数据源的键
        int[] to = {R.id.txt_mname}; // 布局文件中的视图 ID
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_connect, from, to);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                //开始联网
                getHomeMassage();
                break;
            case R.id.img_eye:
                if (isPasswordVisible) {
                    // 隐藏密码
                    wifi_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    img_eye.setImageResource(R.drawable.icon_eye_slash);
                } else {
                    // 显示密码
                    wifi_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    img_eye.setImageResource(R.drawable.icon_eye);
                }
                // 移动光标到文本末尾
                wifi_password.setSelection(wifi_password.getText().length());
                isPasswordVisible = !isPasswordVisible;
                break;
            case R.id.img_selet:
                setDialog();
                break;
            case R.id.img_back:
                finish();
//                ThingHomeSdk.getActivator().newMultiModeActivator().stopActivator(mScanDeviceBean.getUuid());
                break;
        }
    }
    private void getHomeMassage() {
        mCameraDialog.dismiss();
        ing.setVisibility(View.VISIBLE);
//        img_add.setImageResource(R.drawable.connect_ing);
        img_add.setVisibility(View.GONE);
        // 动画完成后跳转页面
                lottieAnimationView.setVisibility(View.VISIBLE) ;
                lottieAnimationView.playAnimation();  // 播放动画

        listView.setEnabled(false);
        ThingHomeSdk.getHomeManagerInstance().queryHomeList(new IThingGetHomeListCallback() {
            @Override
            public void onSuccess(List<HomeBean> homeBeans) {
                // do something
                homeId=homeBeans.get(0).getHomeId();
                //初始化家庭设备
                getToken();
            }
            @Override
            public void onError(String errorCode, String error) {
                // do something
                Toast.makeText(GetWifi.this, "错误"+error, Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void getToken() {
        Toast.makeText(this, "home"+homeId, Toast.LENGTH_SHORT).show();
        //获取token
        ThingHomeSdk.getActivatorInstance().getActivatorToken(homeId,
                new IThingActivatorGetToken() {

                    @Override
                    public void onSuccess(String token) {
                        String ssid = wifi_name.getText().toString();
                        String password = wifi_password.getText().toString();
                        getWifi(token, ssid, password);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {

                    }
                });
    }

    private void getWifi(String token, String ssid, String password) {
        // mScanDeviceBean 来自于扫描回调的 ScanDeviceBean

        MultiModeActivatorBean multiModeActivatorBean = new MultiModeActivatorBean(mScanDeviceBean);

// mScanDeviceBean 来自于扫描回调的 ScanDeviceBean
        multiModeActivatorBean.deviceType = mScanDeviceBean.getDeviceType(); // 设备类型
        multiModeActivatorBean.uuid = mScanDeviceBean.getUuid(); // 设备 uuid
        multiModeActivatorBean.address = mScanDeviceBean.getAddress(); // 设备地址
        multiModeActivatorBean.mac = mScanDeviceBean.getMac(); // 设备 mac
        multiModeActivatorBean.ssid = ssid; // Wi-Fi SSID
        multiModeActivatorBean.pwd = password; // Wi-Fi 密码
        multiModeActivatorBean.token = token; // 获取的 Token
        multiModeActivatorBean.homeId =homeId ; // 当前家庭 homeId
        multiModeActivatorBean.timeout = 120000; // 超时时间

// 开始配网
        ThingHomeSdk.getActivator().newMultiModeActivator().startActivator(multiModeActivatorBean, new IMultiModeActivatorListener() {
            @Override
            public void onSuccess(DeviceBean deviceBean) {
                // 配网成功
                Toast.makeText(GetWifi.this, "设备加入成功", Toast.LENGTH_SHORT).show();
                Homepage.mDevice = ThingHomeSdk.newDeviceInstance(deviceBean.getDevId());
                startActivity(new Intent(GetWifi.this, Ssound.class));
            }

            @Override
            public void onFailure(int code, String msg, Object handle) {
                Toast.makeText(GetWifi.this, msg, Toast.LENGTH_SHORT).show();
                // 配网失败
                ing.setVisibility(View.GONE);
                img_add.setImageResource(R.drawable.connect_add);
                img_add.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.GONE);
                listView.setEnabled(true);
            }
        });

    }

    //获取WiFi名称
    public String getCurrentWifiName(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // 获取 WiFi 名称
        String ssid = wifiInfo.getSSID();

        if (ssid == null || ssid.equals("<unknown ssid>")) {
            return null; // 可以返回一个友好的提示
        }

        // 去掉引号
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }

        return ssid;
    }

    //底部弹框
    private void setDialog() {
        mCameraDialog = new Dialog(this, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.bottom_dialog, null);
        // 初始化视图
        img_eye=root.findViewById(R.id.img_eye);
        btn_add=root.findViewById(R.id.btn_add);
        wifi_name=root.findViewById(R.id.edit_wifiname);
        wifi_password=root.findViewById(R.id.edit_wifipassword);

        //连接
        btn_add.setOnClickListener(this);
        //密码可见或不可见
        img_eye.setOnClickListener(this);

        //请求权限
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            String wifiName = getCurrentWifiName(this);
            // 使用 wifiName
        }

        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 使用 MATCH_PARENT
        root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 1.0f; // 设置为 1.0f 以确保不透明

        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
        String name=getCurrentWifiName(this);
        if(name!=null) wifi_name.setText(name);
        else {
            Toast.makeText(this, "请连接wifi", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
            mCameraDialog.dismiss();
        }

    }

}