package com.yunsong.bujen.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thingclips.smart.android.user.api.ILogoutCallback;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;
import com.yunsong.bujen.device.Devices;
import com.yunsong.bujen.init.Connect;
import com.yunsong.bujen.init.Login;
import com.yunsong.bujen.init.Register;

public class Setting extends AppCompatActivity implements View.OnClickListener{

    LinearLayout log_out,lin_account;
    ImageView img_back;
    private ConfirmDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    private void init(){
        img_back=findViewById(R.id.img_back);
        log_out=findViewById(R.id.log_out);
        lin_account=findViewById(R.id.lin_account);

        log_out.setOnClickListener(this);
        lin_account.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.log_out:
                showDialog("退出","你确定要退出登录吗？");
                break;
            case R.id.lin_account:
                startActivity(new Intent(Setting.this, Account.class));
        }
    }

    private void showDialog(String title, String message) {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(this);
        dialog = builder
                .cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .setTitle(title)
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 处理确定按钮点击
                        if ("注销".equals(title)) {
//                            toLogout();
                        } else if ("退出".equals(title)) {
                            toQuit();
                        }
                        dialog.dismiss();
                    }
                })
                .addViewOnclick(R.id.txt_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 处理取消按钮点击
                        dialog.dismiss();
                    }
                })
                .build();
        dialog.show();
    }


    private void toQuit() {
        ThingHomeSdk.getUserInstance().logout(new ILogoutCallback() {
            @Override
            public void onSuccess() {
                // 退出登录成功
                Intent intent = new Intent(Setting.this, Login.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(Setting.this, "退出失败: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

}