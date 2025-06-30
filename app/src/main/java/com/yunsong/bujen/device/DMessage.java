package com.yunsong.bujen.device;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
TextView device_id,ip_info,device_time,signal_strength;

    ImageView img_back;
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
        device_id=findViewById(R.id.device_id);
        ip_info=findViewById(R.id.ip_info);
        device_time=findViewById(R.id.device_time);
        signal_strength=findViewById(R.id.signal_strength);

        findViewById(R.id.img_back).setOnClickListener(v -> {
            finish();
        });
        ThingDeviceDetailKit detailKit = ThingDeviceDetailKit.getInstance();
        IDeviceDetailInfoManager detailInfoManager = detailKit.getDeviceInfoManager();
        detailInfoManager.getDeviceDetailInfo(id, new DeviceDetailInfoCallback() {
            @Override
            public void onDeviceDetailInfoResult(DeviceDetailInfo deviceDetailInfo) {
                runOnUiThread(() -> {
                    String devId = deviceDetailInfo.devId;
                    String ip = deviceDetailInfo.ip;
                    String timezone = deviceDetailInfo.timezone;
                    Integer signal = deviceDetailInfo.wifiSignal;

                    Log.d("Device","信号"+signal);
                    device_id.setText(devId);
                    ip_info.setText(ip);
                    device_time.setText(timezone);
                    signal_strength.setText(String.valueOf(signal));
                });
            }
        });


        findViewById(R.id.txt_copy_id).setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("device_id", device_id.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "已复制设备ID", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.txt_view_ip).setOnClickListener(v -> {
            String ip = ip_info.getText().toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 自定义View
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_ip_info, null);
            TextView tvContent = dialogView.findViewById(R.id.tv_ip_content);
            Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
            Button btnCopy = dialogView.findViewById(R.id.btn_copy);

            // 设置文字内容
            tvContent.setText("您的IP地址为：" + ip + "\nIP信息会涉及到您个人隐私信息，请谨慎使用");

            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            dialog.show();

            // 取消按钮
            btnCancel.setOnClickListener(view -> dialog.dismiss());

            // 复制按钮
            btnCopy.setOnClickListener(view -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("IP地址", ip);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "已复制IP地址", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });

        findViewById(R.id.txt_signal_tips).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("信号强度参考")
                    .setMessage("-30dBm：极好\n-50~-60dBm：较好\n-70dBm以下：较弱")
                    .setPositiveButton("知道了", null)
                    .show();
        });



    }
}