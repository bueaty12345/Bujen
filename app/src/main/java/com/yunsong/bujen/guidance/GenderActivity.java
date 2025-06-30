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

public class GenderActivity extends AppCompatActivity {
    private RadioGroup genderGroup;
    private TextView btnContinue;
    private String selectedGender = "";
    private String selectedAge = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex_selection);

        selectedAge = getIntent().getStringExtra("selected_age");

        genderGroup = findViewById(R.id.guidance_group);
        btnContinue = findViewById(R.id.btn_continue);

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.girl:
                        selectedGender = "0";
                        break;
                    case R.id.man:
                        selectedGender = "1";
                        break;
                }
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedGender.isEmpty()) {
                    Toast.makeText(GenderActivity.this, "请选择你的性别", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(GenderActivity.this, StatusActivity.class);
                    intent.putExtra("selected_age", selectedAge);
                    intent.putExtra("selected_gender", selectedGender);
                    startActivity(intent);
                }
            }
        });
    }

}
