package com.yunsong.bujen.device;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.R;
import com.thingclips.smart.device.info.sdk.api.DeviceDetailInfoCallback;
import com.thingclips.smart.device.info.sdk.api.IDeviceDetailInfoManager;
import com.thingclips.smart.device.info.sdk.bean.DeviceDetailInfo;
import com.thingclips.smart.thingdevicedetailkit.ThingDeviceDetailKit;

public class DMessage extends AppCompatActivity {
TextView txt_id,txt_ip,txt_time;
    String id;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dmessage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent it=getIntent();
        id=it.getStringExtra("id");
        txt_id=findViewById(R.id.txt_id);
        txt_ip=findViewById(R.id.txt_ip);
        txt_time=findViewById(R.id.txt_time);
        ThingDeviceDetailKit detailKit = ThingDeviceDetailKit.getInstance();
        IDeviceDetailInfoManager detailInfoManager = detailKit.getDeviceInfoManager();
        detailInfoManager.getDeviceDetailInfo(id, new DeviceDetailInfoCallback() {
            @Override
            public void onDeviceDetailInfoResult(DeviceDetailInfo deviceDetailInfo) {
                // 在这里处理 deviceDetailInfo
                //设备id
                String devId = deviceDetailInfo.devId;
                //设备ip
                String ip = deviceDetailInfo.ip;
                //设备mac
                String mac = deviceDetailInfo.mac;
                //设备时区
                String timezone = deviceDetailInfo.timezone;

                txt_id.setText(devId);
                txt_ip.setText(ip);
                txt_time.setText(timezone);

            }
        });



    }
}