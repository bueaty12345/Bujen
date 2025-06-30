package com.yunsong.bujen.guidance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yunsong.bujen.R;

public class AgeActivity extends AppCompatActivity {
    private RadioGroup ageGroup;
    private TextView btnContinue;
    private String selectedAge = "";
    private ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_selection);

        ageGroup = findViewById(R.id.guidance_group);
        btnContinue = findViewById(R.id.btn_continue);
        img_back=findViewById(R.id.img_back);

        img_back.setOnClickListener(v -> {
            finish();
        });

        // 监听选中项
        ageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.age1:
                        selectedAge = "1";
                        break;
                    case R.id.age2:
                        selectedAge = "2";
                        break;
                    case R.id.age3:
                        selectedAge = "3";
                        break;
                    case R.id.age4:
                        selectedAge = "4";
                        break;
                    case R.id.age5:
                        selectedAge = "5";
                        break;
                    case R.id.age6:
                        selectedAge = "6";
                        break;
                }

                Toast.makeText(AgeActivity.this, "你选择的是：" + selectedAge, Toast.LENGTH_SHORT).show();
            }
        });

        // 点击“继续”按钮
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAge.isEmpty()) {
                    Toast.makeText(AgeActivity.this, "请选择你的年龄", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(AgeActivity.this, GenderActivity.class);
                    intent.putExtra("selected_age", selectedAge);
                    startActivity(intent);
                }
            }
        });
    }
}
