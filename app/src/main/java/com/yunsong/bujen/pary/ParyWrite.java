package com.yunsong.bujen.pary;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.R;

public class ParyWrite extends AppCompatActivity {
    private ImageView img_main, img_zp, img_xj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pary_write);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        img_main = findViewById(R.id.img_main);
        img_zp = findViewById(R.id.img_zp);
        img_xj = findViewById(R.id.img_xj);

        img_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在点击主按钮时，开始扇形展开动画
                img_zp.setVisibility(View.VISIBLE);
                img_xj.setVisibility(View.VISIBLE);

                // 按钮1的动画：从主按钮位置向上移动
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(img_zp, "translationY", 0f, -250f);
                animator1.setDuration(700);

                // 按钮2的动画：从主按钮位置向右上方发散
                ObjectAnimator animator2X = ObjectAnimator.ofFloat(img_xj, "translationX", 0f, 200f); // X轴向右移动
                ObjectAnimator animator2Y = ObjectAnimator.ofFloat(img_xj, "translationY", 0f, -200f); // Y轴向上移动
                animator2X.setDuration(700);
                animator2Y.setDuration(700);

                // 动画执行
                animator1.start();
                animator2X.start();
                animator2Y.start();
            }
        });

    }
}