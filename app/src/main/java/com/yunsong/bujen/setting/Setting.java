package com.yunsong.bujen.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thingclips.smart.android.user.api.ILogoutCallback;
import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;
import com.yunsong.bujen.BuildConfig;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;
import com.yunsong.bujen.device.Devices;
import com.yunsong.bujen.init.Connect;
import com.yunsong.bujen.init.Login;

import java.io.File;
import java.text.DecimalFormat;

public class Setting extends AppCompatActivity implements View.OnClickListener{

    LinearLayout log_out,lin_account,lin_edit,lin_about,lin_history,lin_clear_cache;
    ImageView img_back;
    Switch sw_battery, sw_notice;
    TextView cacheSizeText;
    private ConfirmDialog dialog;
    private final String NOTICE_RECEIVE= BuildConfig.API_SERVER+"/system/recordb/app/receive";

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
        lin_edit=findViewById(R.id.lin_edit);
        lin_about=findViewById(R.id.lin_about);
        lin_history=findViewById(R.id.lin_history);
        lin_clear_cache=findViewById(R.id.lin_clear_cache);
//        sw_battery=findViewById(R.id.sw_battery);
        sw_notice=findViewById(R.id.sw_notice);
        cacheSizeText=findViewById(R.id.tv_cache_size);

        img_back.setOnClickListener(this);
        log_out.setOnClickListener(this);
        lin_account.setOnClickListener(this);
        lin_edit.setOnClickListener(this);
        lin_about.setOnClickListener(this);
        lin_history.setOnClickListener(this);
        lin_clear_cache.setOnClickListener(this);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
//        sw_battery.setChecked(prefs.getBoolean("battery_reminder", false));
        sw_notice.setChecked(prefs.getBoolean("notification_reminder", false));

//        sw_battery.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            prefs.edit().putBoolean("battery_reminder", isChecked).apply();
//            Toast.makeText(this, isChecked ? "已开启电池提醒" : "已关闭电池提醒", Toast.LENGTH_SHORT).show();
//        });

        sw_notice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notification_reminder", isChecked).apply();
        });

        if (cacheSizeText != null) {
            cacheSizeText.setText(getFormattedSize(getDirSize(getCacheDir())));
        }
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
                break;
            case R.id.lin_edit:
                startActivity(new Intent(Setting.this,ProfileEditor.class));
                break;
            case R.id.lin_about:
                startActivity(new Intent(Setting.this,About.class));
                break;
            case R.id.lin_history:
                startActivity(new Intent(Setting.this,History.class));
                break;
            case R.id.lin_clear_cache:
                showDialog("清除缓存", "确定清除应用缓存吗？");
                break;

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
                        if ("退出".equals(title)) {
                            toQuit();
                        } else if ("清除缓存".equals(title)) {
                            clearAppCache();
                            if (cacheSizeText != null) {
                                cacheSizeText.setText(getFormattedSize(getDirSize(getCacheDir())));
                            }
                            Toast.makeText(Setting.this, "缓存已清除", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                })
                .addViewOnclick(R.id.txt_cancel, view -> dialog.dismiss())
                .build();
        dialog.show();
    }


    private void toQuit() {
        ThingHomeSdk.getUserInstance().logout(new ILogoutCallback() {
            @Override
            public void onSuccess() {
                // 退出登录成功
                BizBundleInitializer.onLogout(Setting.this);
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

    private void clearAppCache() {
        try {
            File cacheDir = getCacheDir();
            deleteDir(cacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) return false;
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private long getDirSize(File dir) {
        long size = 0;
        if (dir != null && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    size += getDirSize(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }

    private String getFormattedSize(long size) {
        DecimalFormat df = new DecimalFormat("0.00");
        float kb = size / 1024f;
        float mb = kb / 1024f;
        if (mb >= 1) {
            return df.format(mb) + "MB";
        } else {
            return df.format(kb) + "KB";
        }
    }
}