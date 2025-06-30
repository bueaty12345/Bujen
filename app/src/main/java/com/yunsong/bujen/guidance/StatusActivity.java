package com.yunsong.bujen.guidance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yunsong.bujen.R;

public class StatusActivity extends AppCompatActivity {
    private RadioGroup StatusGroup;
    private TextView btnContinue;
    private String selectedGender = "";
    private String selectedAge = "";
    private String selectedStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_selection);

        selectedAge = getIntent().getStringExtra("selected_age");
        selectedGender=getIntent().getStringExtra("selected_gender");

        StatusGroup = findViewById(R.id.guidance_group);
        btnContinue = findViewById(R.id.btn_continue);

        StatusGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.status1:
                        selectedStatus = "0";
                        break;
                    case R.id.status2:
                        selectedStatus = "1";
                        break;
                }
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedStatus.isEmpty()) {
                    Toast.makeText(StatusActivity.this, "请选择状态", Toast.LENGTH_SHORT).show();
                } else {
                    // 跳转到状态页
                    Intent intent = new Intent(StatusActivity.this, CircumstanceActivity.class);
                    intent.putExtra("selected_age", selectedAge);
                    intent.putExtra("selected_gender", selectedGender);
                    intent.putExtra("selected_status", selectedStatus);
                    startActivity(intent);
                }
            }
        });
    }
}
