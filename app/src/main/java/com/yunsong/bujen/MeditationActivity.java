
package com.yunsong.bujen;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MeditationActivity extends AppCompatActivity implements View.OnClickListener{
    ConstraintLayout main;
    LinearLayout lin_kz;
    ImageView img_circle,img_bf,img_timing,img_next,img_previous,img_reset;
    TextView txt_stateTime,txt_endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meditation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        intView();

    }

    private void intView() {
        main = findViewById(R.id.main);
        lin_kz = findViewById(R.id.lin_kz);
        img_circle = findViewById(R.id.img_circle);
        img_bf = findViewById(R.id.img_bf);
        img_timing = findViewById(R.id.img_timing);
        img_next = findViewById(R.id.img_next);
        img_previous = findViewById(R.id.img_previous);
        img_reset = findViewById(R.id.img_reset);
        txt_stateTime = findViewById(R.id.txt_stateTime);
        txt_endTime = findViewById(R.id.txt_endTime);

        img_bf.setOnClickListener(this);
        img_timing.setOnClickListener(this);
        img_next.setOnClickListener(this);
        img_previous.setOnClickListener(this);
        img_reset.setOnClickListener(this);
        img_circle.setOnClickListener(this);
        main.setOnClickListener(this);

        main.setOnTouchListener(new View.OnTouchListener() {
            private float initialY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 记录触摸点的Y坐标
                        initialY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY =initialY - event.getY();
                        // 判断是否为下滑事件
                        if (deltaY > 100) {  // 当滑动距离超过阈值（比如100px）
                            startActivity(new Intent(MeditationActivity.this, MedLocad.class));
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                        }
                        break;
                }
                return true;
            }
        });

        setVague();


    }

    //模糊显示
    private void setVague(){
        Glide.with(this)
                .load(R.drawable.qf_bg)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(10, 35)))
                .into(new ViewTarget<ConstraintLayout, Drawable>(main) {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Drawable drawable = resource.getCurrent();
                        drawable.setColorFilter(Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
                        main.setBackground(drawable);
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_circle:
            case R.id.main:
                if(lin_kz.getVisibility()==View.VISIBLE){
                    lin_kz.setVisibility(View.INVISIBLE);
                }else {
                    lin_kz.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}