package com.yunsong.bujen.device;

import static com.yunsong.bujen.Homepage.mDevice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;
import com.thingclips.smart.sdk.api.IThingDevice;

public class Device extends AppCompatActivity implements View.OnClickListener{
LinearLayout message,share,upgrade,remove,reset;
TextView txt_id;
ImageView img_back,img_gai;
String id,name;
IThingDevice device ;
private ConfirmDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_device);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    void init(){
        message=findViewById(R.id.lin_message);
        share=findViewById(R.id.lin_share);
        upgrade=findViewById(R.id.lin_upgrade);
        remove=findViewById(R.id.lin_remove);
        txt_id=findViewById(R.id.txt_id);
        img_back=findViewById(R.id.img_back);
        img_gai=findViewById(R.id.img_gai);
        reset=findViewById(R.id.lin_reset);

        message.setOnClickListener(this);
        share.setOnClickListener(this);
        upgrade.setOnClickListener(this);
        remove.setOnClickListener(this);
        img_back.setOnClickListener(this);
        img_gai.setOnClickListener(this);
        reset.setOnClickListener(this);

        Intent it=getIntent();
        id=it.getStringExtra("id");
        name=it.getStringExtra("name");
        txt_id.setText(name);
        device = ThingHomeSdk.newDeviceInstance(id);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lin_message:
                Intent it=new Intent(Device.this,DMessage.class);
                it.putExtra("id",id);
                startActivity(it);
                break;
            case R.id.lin_share:break;
            case R.id.lin_upgrade:
                Intent it1=new Intent(Device.this,Upgrade.class);
                it1.putExtra("id",id);
                startActivity(it1);
                break;
            case R.id.lin_remove:
                ConfirmDialog.Builder builder = new ConfirmDialog.Builder(this);
                dialog = builder.cancelTouchout(false)
                        .view(R.layout.dialog_confirm)
                        .style(R.style.Dialog)
                        .setTitle("你确定要移除设备吗？")
                        .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 点击“确认”按钮后的操作
                                device.removeDevice(new IResultCallback() {
                                    @Override
                                    public void onError(String errorCode, String errorMsg) {
                                        Toast.makeText(Device.this, ""+errorMsg, Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(Device.this, "移除成功", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Device.this,Devices.class));
                                        finish();
                                    }
                                });
                            }
                        })
                        .build();
                dialog.show();
                break;
            case R.id.lin_reset:
                ConfirmDialog.Builder builder1 = new ConfirmDialog.Builder(this);
                dialog = builder1.cancelTouchout(false)
                        .view(R.layout.dialog_confirm)
                        .style(R.style.Dialog)
                        .setTitle("你确定要恢复出厂设置吗？")
                        .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            // 点击“确认”按钮后的操作
                                mDevice.resetFactory(new IResultCallback() {
                                    @Override
                                    public void onError(String errorCode, String errorMsg) {
                                        Toast.makeText(Device.this, ""+errorMsg, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(Device.this, "重置成功", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        })
                        .build();
                dialog.show();
                break;
            case R.id.img_gai:setDailog();break;
            case R.id.img_back:finish();break;
        }

    }

    private void setDailog() {
        // 加载自定义对话框布局
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.wifi_ed, null);
        EditText editId = dialogView.findViewById(R.id.edt_id);
        // 创建并显示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改设备名")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    // 获取用户输入的值
                    String idText = editId.getText().toString();
                    device.renameDevice(idText, new IResultCallback() {
                        @Override
                        public void onError(String code, String error) {
                            // 修改设备名称失败
                            Toast.makeText(Device.this, "修改失败", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess() {
                            // 修改设备名称成功
                            // 保存模板数据或更新 UI
                            txt_id.setText(idText);
                        }
                    });

                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}