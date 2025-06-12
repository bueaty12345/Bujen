package com.yunsong.bujen.setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.R;

public class ProfileEditor extends AppCompatActivity implements View.OnClickListener{
    TextView tv_nickname_info,tv_signature_info;
    ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profileeditor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        loadPhoneNumberFromPrefs();
    }
    private void init(){
        img_back=findViewById(R.id.img_back);
        tv_nickname_info=findViewById(R.id.tv_nickname_info);
        tv_signature_info=findViewById(R.id.tv_signature_info);

        img_back.setOnClickListener(this);
    }

    private void loadPhoneNumberFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String phone = sharedPreferences.getString("user_nickname", "未设置昵称");
        String signature = sharedPreferences.getString("user_signature", " ");
        tv_nickname_info.setText(phone);
        tv_signature_info.setText(signature);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
        }
    }
}
