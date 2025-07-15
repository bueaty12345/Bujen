package com.yunsong.bujen.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;
import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;
import com.yunsong.bujen.init.Register;
import com.yunsong.bujen.utils.UserInfoUtils;

public class Account extends AppCompatActivity implements View.OnClickListener{
    ConstraintLayout log_out;

    LinearLayout lin_edit,lin_account,lin_mailEdit,lin_tripartite;
    ImageView img_back;

    TextView tv_account_number;

    private ConfirmDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accountsetting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    private void init(){
        log_out=findViewById(R.id.log_out);
        img_back=findViewById(R.id.img_back);
        lin_edit=findViewById(R.id.lin_edit);
        lin_account=findViewById(R.id.lin_account);
        lin_mailEdit=findViewById(R.id.lin_mailEdit);
        lin_tripartite=findViewById(R.id.lin_tripartite);
        tv_account_number=findViewById(R.id.tv_account_number);

        img_back.setOnClickListener(this);
        log_out.setOnClickListener(this);
        lin_edit.setOnClickListener(this);
        lin_account.setOnClickListener(this);
        lin_mailEdit.setOnClickListener(this);
        lin_tripartite.setOnClickListener(this);

        tv_account_number.setText(UserInfoUtils.getUserPhone(this));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.log_out:
                showDialog("确定要注销账号吗？","一周后才会真正注销，注销前登录则会取消注销，确定注销");
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_edit:
                startActivity(new Intent(Account.this, EditPassword.class));
                break;
            case R.id.lin_account:
                startActivity(new Intent(Account.this, PhoneNumber.class));
                break;
            case R.id.lin_mailEdit:
                startActivity(new Intent(Account.this, EditMail.class));
                break;
            case R.id.lin_tripartite:
                startActivity(new Intent(Account.this,Tripartite.class));
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
                        if ("确定要注销账号吗？".equals(title)) {
                            Log.d("DialogDebug", "执行 toLogout()");
                            toLogout();
                        } else if ("退出".equals(title)) {
//                            toQuit();
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

    private void toLogout() {
        ThingHomeSdk.getUserInstance().cancelAccount(new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Toast.makeText(Account.this, "注销失败: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(Account.this, "注销成功", Toast.LENGTH_SHORT).show();

                // 清理用户状态
                // 清理 SharedPreferences 或 Session
                // UserSession.clear();

                // 跳转到注册（或登录）界面
                Intent intent = new Intent(Account.this, Register.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                // 关闭当前界面
                finish();
            }
        });
    }


}


