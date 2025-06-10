package com.yunsong.bujen.init;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.R;

public class Login extends AppCompatActivity implements View.OnClickListener{
    TextView txt_phone, txt_toast;
    Button btn_add, btn_else;
    String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    void init() {
        txt_phone = findViewById(R.id.txt_phone);
        txt_toast = findViewById(R.id.txt_toast);
        btn_add = findViewById(R.id.btn_add);
        btn_else = findViewById(R.id.btn_elseAdd);

        // 动态获取手机号权限
//        if (checkPermissions()) {
            getPhoneNumber();
//        } else {
//            requestPermissions();
//        }
        //本机直接登录
        btn_add.setOnClickListener(this);
        //其他手机号码登录
        btn_else.setOnClickListener(this);
        //立即体验




        // 获取组件
        LinearLayout linPhone = findViewById(R.id.linearLayout2);


// 为 LinearLayout 创建从下往上的平移动画和渐显动画
        ObjectAnimator linPhoneTranslationY = ObjectAnimator.ofFloat(linPhone, "translationY", 500f, 0f); // 从 500px 位置上移到原位
        ObjectAnimator linPhoneAlpha = ObjectAnimator.ofFloat(linPhone, "alpha", 0f, 1f); // 从透明渐变为不透明
//
// 设置 LinearLayout 动画的延迟（可选）
        linPhoneTranslationY.setStartDelay(50); // 延迟 300ms 后开始动画
        linPhoneAlpha.setStartDelay(50); // 延迟 300ms 后开始渐显动画

// 创建 AnimatorSet，将平移和渐显动画同时执行
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether( linPhoneTranslationY, linPhoneAlpha);

// 设置动画时长
        animatorSet.setDuration(1500); // 动画时长 1000ms

// 启动动画
        animatorSet.start();

    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 及以上：READ_PHONE_NUMBERS 足够
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 12 及以下：两个权限都判断（有些 ROM 可能只识别 READ_PHONE_STATE）
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS
        }, 123);
    }

    private void getPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        phoneNumber = telephonyManager.getLine1Number();

        if (phoneNumber != null && phoneNumber.length() >= 7) {
            try {
                // 获取中间三位和后四位（前3位+****+后4位）
                String front = phoneNumber.substring(3, 6);
                String back = phoneNumber.substring(phoneNumber.length() - 4);
                txt_phone.setText(front + "****" + back);

                // 获取运营商名称
                String operatorName = telephonyManager.getNetworkOperatorName();
                txt_toast.setText(operatorName + getResources().getString(R.string.login_text2));
                Toast.makeText(this, getResources().getString(R.string.get_success), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Log.e("Login", "Phone number formatting failed: " + phoneNumber, e);
                findViewById(R.id.lin_phone).setVisibility(View.INVISIBLE);
                btn_else.setVisibility(View.INVISIBLE);
                Toast.makeText(this, getResources().getString(R.string.login_text3), Toast.LENGTH_SHORT).show();
            }

        } else {
            findViewById(R.id.lin_phone).setVisibility(View.INVISIBLE);
            btn_else.setVisibility(View.INVISIBLE);
            Toast.makeText(this, getResources().getString(R.string.login_text3), Toast.LENGTH_SHORT).show();
        }

    }

    // 处理请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && allPermissionsGranted(grantResults)) {
                getPhoneNumber();
            } else {
                findViewById(R.id.lin_phone).setVisibility(View.INVISIBLE);
                btn_else.setVisibility(View.INVISIBLE);
                Toast.makeText(this, getResources().getString(R.string.login_text4), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                Intent it=new Intent(Login.this, CaptchaLogin.class);
                if(phoneNumber!=null&&!(phoneNumber.equals("")))
                    it.putExtra("phone",phoneNumber.substring(3, phoneNumber.length()));
                startActivity(it);
                break;
            case R.id.btn_elseAdd:
                startActivity(new Intent(Login.this, PassLogin.class));
                break;
        }
    }
}
