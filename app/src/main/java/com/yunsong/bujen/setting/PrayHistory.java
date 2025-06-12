package com.yunsong.bujen.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.R;

public class PrayHistory extends AppCompatActivity implements View.OnClickListener{
    GridLayout gridLayout;
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prayhistory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    private void init(){
        img_back=findViewById(R.id.img_back);
        gridLayout=findViewById(R.id.grid_container);

        img_back.setOnClickListener(this);

        int[] imageResIds = {
                R.drawable.icon_history1,
                R.drawable.icon_history2,
                R.drawable.icon_history3,
        };

        for (int i=0;i<imageResIds.length;i++) {
            final int index = i;
            int resId = imageResIds[i];
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(resId);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = dpToPx(100);
            params.height = dpToPx(100);
            params.setMargins(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PrayHistory.this, "你点击了第 " + (index + 1) + " 张图", Toast.LENGTH_SHORT).show();
                }
            });

            gridLayout.addView(imageView);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
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
