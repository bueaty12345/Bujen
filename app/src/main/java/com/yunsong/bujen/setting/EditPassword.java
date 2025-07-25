package com.yunsong.bujen.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thingclips.smart.android.user.api.IResetPasswordCallback;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;
import com.yunsong.bujen.R;
import com.yunsong.bujen.init.Login;

public class EditPassword extends AppCompatActivity implements View.OnClickListener{
    private EditText edit_phone, edit_code, edit_password;
    private TextView txt_code;
    private ImageView img_back, img_eye;
    private Button btn_add;
    private boolean isPasswordVisible = false;
    private boolean isCountingDown = false;
    private CountDownTimer countdownTimer;
    private static final long COUNTDOWN_TIME_IN_MILLIS = 60000; // 60秒



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editpassword);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    private void init(){
        edit_phone = findViewById(R.id.edit_phone);
        edit_code = findViewById(R.id.edit_code);
        edit_password = findViewById(R.id.edit_password);
        txt_code = findViewById(R.id.txt_code);
        img_eye = findViewById(R.id.img_eye);
        img_back = findViewById(R.id.img_back);
        btn_add = findViewById(R.id.btn_add);

        txt_code.setOnClickListener(this);
        img_eye.setOnClickListener(this);
        img_back.setOnClickListener(this);
        btn_add.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String phone = edit_phone.getText().toString().trim();
        String code = edit_code.getText().toString().trim();
        String newPassword = edit_password.getText().toString().trim();

        switch (v.getId()){
            case R.id.txt_code:
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                getVerifyCode(phone);
                break;
            case R.id.btn_add:
                if (!isValidPassword(newPassword)) {
                    Toast.makeText(this, "密码必须为8-16位，包含数字和字母", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkVerificationCode(phone, code, () -> resetPassword(phone, newPassword, code));
                break;

            case R.id.img_eye:
                togglePasswordVisibility();
                break;

            case R.id.img_back:
                finish();
                break;

        }

    }

    // 获取验证码
    private void getVerifyCode(String phone) {
        ThingHomeSdk.getUserInstance().sendVerifyCodeWithUserName(phone, "", "86", 3, new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Toast.makeText(EditPassword.this, "验证码发送失败：" + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                startCountdown();
                Toast.makeText(EditPassword.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startCountdown() {
        isCountingDown = true;
        txt_code.setEnabled(false);
        txt_code.setAlpha(0.5f);
        countdownTimer = new CountDownTimer(COUNTDOWN_TIME_IN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txt_code.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                txt_code.setText(getResources().getString(R.string.hq_code));
                txt_code.setEnabled(true);
                txt_code.setAlpha(1f);
                isCountingDown = false;
            }
        }.start();
    }

    //校验验证码
    private void checkVerificationCode(String phone, String code, Runnable onSuccess) {
        ThingHomeSdk.getUserInstance().checkCodeWithUserName(
                phone,
                "",
                "86",
                code,
                3,
                new IResultCallback() {
                    @Override
                    public void onSuccess() {
                        onSuccess.run(); // 验证成功后执行传入的逻辑
                    }

                    @Override
                    public void onError(String errCode, String error) {
                        Toast.makeText(EditPassword.this, "验证码无效：" + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // 重置密码
    private void resetPassword(String phone, String newPassword, String code) {
        Log.d("password","data"+phone);
        Log.d("password","data"+newPassword);
        Log.d("password","data"+code);
        ThingHomeSdk.getUserInstance().resetPhonePassword("86", phone, code, newPassword,new IResetPasswordCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditPassword.this, "密码重置成功，请重新登录", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditPassword.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // 清空栈，避免返回按钮回到本页
                startActivity(intent);
            }

            @Override
            public void onError(String code, String error) {
                Log.d("重置失败","error"+error);
                Toast.makeText(EditPassword.this, "重置失败：" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidPassword(String password) {
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$";
        return password.matches(pattern);
    }

    // 切换密码显示隐藏
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            img_eye.setImageResource(R.drawable.icon_eye_slash);
        } else {
            edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            img_eye.setImageResource(R.drawable.icon_eye);
        }
        edit_password.setSelection(edit_password.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }
}
