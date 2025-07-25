package com.yunsong.bujen.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.R;
import com.yunsong.bujen.utils.UserInfoUtils;

public class PhoneNumber extends AppCompatActivity implements View.OnClickListener{
    ImageView img_back;
    TextView phoneNumber;
    LinearLayout bind_PhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phonenumber);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    private void init(){
        img_back=findViewById(R.id.img_back);
        phoneNumber=findViewById(R.id.phoneNumber);
        bind_PhoneNumber=findViewById(R.id.bind_PhoneNumber);

        String number = UserInfoUtils.getUserPhone(this);
        phoneNumber.setText(maskPhoneNumber(number));

        img_back.setOnClickListener(this);
        bind_PhoneNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.bind_PhoneNumber:
                Intent intent = new Intent(PhoneNumber.this, PhoneNumberBind.class);
                startActivityForResult(intent, 1001);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // 刷新手机号
            String number = UserInfoUtils.getUserPhone(this);
            phoneNumber.setText(maskPhoneNumber(number));
        }
    }

    private String maskPhoneNumber(String phone) {
        if (phone.length() >= 11) {
            return phone.substring(0, 3) + "****" + phone.substring(7);
        } else {
            return phone;
        }
    }
}
