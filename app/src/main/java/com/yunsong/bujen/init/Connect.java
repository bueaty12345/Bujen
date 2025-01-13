package com.yunsong.bujen.init;

import static com.thingclips.sdk.blelib.utils.BluetoothUtils.isBluetoothEnabled;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.GetWifi;
import com.yunsong.bujen.Homepage;
import com.yunsong.bujen.R;
import com.bumptech.glide.Glide;
import com.thingclips.smart.android.ble.api.BleScanResponse;
import com.thingclips.smart.android.ble.api.LeScanSetting;
import com.thingclips.smart.android.ble.api.ScanDeviceBean;
import com.thingclips.smart.android.ble.api.ScanType;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class Connect extends AppCompatActivity implements View.OnClickListener{
    Button btn_add;
    TextView txt_skip;
    public static ScanDeviceBean mScanDeviceBean;
    ImageView myGifImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connect);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_add=findViewById(R.id.btn_add);
        txt_skip=findViewById(R.id.txt_skip);
        myGifImageView = findViewById(R.id.myGifImageView);
        btn_add.setOnClickListener(this);
        txt_skip.setOnClickListener(this);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                if (btn_add.getText().equals("开始搜索")) {
                    // 检查并请求蓝牙和位置权限
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                        // 权限未被授予，申请权限
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.BLUETOOTH_SCAN,
                                        Manifest.permission.BLUETOOTH_CONNECT},
                                1);  // 1 为请求码，可以根据实际需要更改
                    } else {
                        // 权限已被授予，开始扫描
                        saoMao();
                    }

                } else {
                    // 停止扫描蓝牙设备
                    ThingHomeSdk.getBleOperator().stopLeScan();  // 停止扫描
                    btn_add.setText("开始搜索");  // 更新按钮文本为“开始搜索”
                    myGifImageView.setImageResource(R.drawable.wifi);  // 重置图标或显示初始状态
                }

//                //开始联网
//                getHomeMassage();
//                startActivity(new Intent(Connect.this, GetWifi.class));

                break;
            case R.id.txt_skip:
                startActivity(new Intent(Connect.this, Homepage.class));
                finish();
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，开始扫描
                saoMao();
            } else {
                // 权限被拒绝，提示用户
                Toast.makeText(this, "权限被拒绝，无法扫描蓝牙设备", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saoMao(){
        btn_add.setText("搜索中...");
        Glide.with(this)
                .asGif()
                .load(R.drawable.wifi) // 或者使用网络链接
                .into(myGifImageView);
        Toast.makeText(this, "1111111111", Toast.LENGTH_SHORT).show();

            boolean bluetoothEnabled = isBluetoothEnabled();
            boolean locationPermissionGranted = isLocationPermissionGranted(this);

            if (bluetoothEnabled) {
                Log.d("Bluetooth", "蓝牙已开启");
            } else {
                Log.d("Bluetooth", "蓝牙未开启");
            }

            if (locationPermissionGranted) {
                Log.d("Location Permission", "定位权限已被允许");
            } else {
                Log.d("Location Permission", "定位权限未被允许");
                //请求权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    String wifiName = getCurrentWifiName(this);
                    // 使用 wifiName
                }

            }
//        Toast.makeText(this, "222222222222", Toast.LENGTH_SHORT).show();
//        ProgressDialog progressDialog1 = new ProgressDialog(Connect.this);
//        progressDialog1.setMessage("正在扫描中");
        LeScanSetting scanSetting = new LeScanSetting.Builder()
                .setTimeout(6000) // 扫描的超时时间：ms
                .addScanType(ScanType.SINGLE) // 若需要扫描蓝牙设备，则只需要添加 ScanType.SINGLE
                // .addScanType(ScanType.SIG_MESH) 可同时添加其他类型设备
                .build();

// 开始扫描
        ThingHomeSdk.getBleOperator().startLeScan(scanSetting, new BleScanResponse() {
            @Override
            public void onResult(ScanDeviceBean bean) {
//                Toast.makeText(Connect.this, "结果", Toast.LENGTH_SHORT).show();
                // 回调扫描的结果 TODO
                //        //停止扫描
                ThingHomeSdk.getBleOperator().stopLeScan();
                btn_add.setText("开始搜索");
                myGifImageView.setImageResource(R.drawable.wifi);
                //跳转并显示搜索结果
                startActivity(new Intent(Connect.this, GetWifi.class));
                mScanDeviceBean=bean;

            }
        });
//        progressDialog1.setCanceledOnTouchOutside(false);
//        progressDialog1.show();
//        //停止扫描
//        ThingHomeSdk.getBleOperator().stopLeScan();
//        progressDialog1.dismiss();
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

    public boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


}