package com.yunsong.bujen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yunsong.bujen.utils.DataStorageUtils;
import com.yunsong.bujen.utils.ExchangeHelper;
import com.yunsong.bujen.utils.FavoriteHelper;
import com.yunsong.bujen.utils.UserInfoUtils;

public class Detail2 extends AppCompatActivity implements View.OnClickListener{
    private ConfirmDialog dialog;
    TextView txt_name,txt_gdd,txt_author,txt_star;
    ImageView img_hart,img_back;
    Button btn_dh,btn_tryout;
    Boolean hart=false;
    ConstraintLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        img_hart.setOnClickListener(this);
        img_back.setOnClickListener(this);
        btn_dh.setOnClickListener(this);
        btn_tryout.setOnClickListener(this);
    }
    private void init(){
        txt_name=findViewById(R.id.txt_mname);
        txt_gdd=findViewById(R.id.txt_gdd);
        txt_author=findViewById(R.id.txt_auther);
        img_hart=findViewById(R.id.img_selet);
        btn_dh=findViewById(R.id.btn_dh);
        img_back=findViewById(R.id.img_back);
        btn_tryout=findViewById(R.id.btn_tryout);
        txt_star=findViewById(R.id.txt_star);
        main=findViewById(R.id.main);
        Intent intent = getIntent();

        // 接收传递的字符串和整数
        String name = intent.getStringExtra("backgroundName");
        int gdd = intent.getIntExtra("requiredMeritPoints",0);
        String auther = intent.getStringExtra("author");
        boolean sc=intent.getBooleanExtra("sc",false);
        boolean dh=intent.getBooleanExtra("dh",false);
        btn_dh.setText(dh ? "已拥有" : "兑换");
        btn_dh.setEnabled(!dh);
        txt_name.setText(name);
        txt_gdd.setText("需功德值："+gdd);
        txt_author.setText("作者："+auther);

        String ratingStr = intent.getStringExtra("rating");
        if (ratingStr != null) {
            txt_star.setText(ratingStr);
        }

        //判断是否收藏
        hart=sc;
        if (sc) {
            img_hart.setImageResource(R.drawable.collection_1);
        } else {
            img_hart.setImageResource(R.drawable.collection_2);
        }

        //封面
        String backgroundImageUrl = intent.getStringExtra("backgroundImageUrl");
        Log.d("bg","bg"+backgroundImageUrl);
        if (backgroundImageUrl != null && !backgroundImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(backgroundImageUrl)
                    .placeholder(R.drawable.detail_bg)
                    .error(R.drawable.detail_bg)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            main.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            main.setBackground(placeholder);
                        }
                    });

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_tryout:
                setDialog();
                break;
            case R.id.img_selet:
                hart = !hart; // 切换收藏状态
                img_hart.setImageResource(hart ? R.drawable.collection_1 : R.drawable.collection_2);

                int userId = UserInfoUtils.getUserId(Detail2.this);
                String resourceType = getIntent().getStringExtra("resourceType");;
                int resourceId = getIntent().getIntExtra("backgroundId", 0);;
                String token=UserInfoUtils.getToken(this);
                FavoriteHelper.updateFavoriteStatus(hart,token, userId, resourceType, resourceId, new FavoriteHelper.Callback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(Detail2.this, hart ? "收藏成功" : "取消收藏成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        // 失败，回滚UI和hart状态
                        hart = !hart;
                        runOnUiThread(() -> {
                            img_hart.setImageResource(hart ? R.drawable.collection_1 : R.drawable.collection_2);
                            Toast.makeText(Detail2.this, "收藏状态更新失败：" + errorMsg, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                break;
            case R.id.img_back:finish();break;
            case R.id.btn_dh:
                if (btn_dh.getText().equals("兑换")) {
                    int requiredGdd = getIntent().getIntExtra("requiredMeritPoints", 0);
                    int localGdd = DataStorageUtils.getGddCount(this);

                    if (localGdd >= requiredGdd) {
                        ExchangeHelper.showExchangeDialog(this, requiredGdd,
                                getIntent().getStringExtra("resourceType"),
                                getIntent().getIntExtra("backgroundId", 0),
                                btn_dh);
                    } else {
                        Toast.makeText(this, "功德点不足，无法兑换", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }

    private void setDialog() {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(this);
        dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_tryout_confirm)
                .style(R.style.Dialog)
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        String bgUrl = getIntent().getStringExtra("backgroundImageUrl");

                        SharedPreferences prefs = getSharedPreferences("homepage_config", MODE_PRIVATE);
                        prefs.edit()
                                .putString("bg_url", bgUrl)
                                .putBoolean("should_update", true)
                                .apply();

                        Toast.makeText(Detail2.this, "试用已生效，返回首页查看效果", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Detail2.this, Homepage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();

                    }
                })
                .build();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String resourceType = getIntent().getStringExtra("resourceType");
        int resourceId = getIntent().getIntExtra("backgroundId", 0); // 或 backgroundId

        boolean exchanged = ExchangeHelper.isExchanged(this, resourceType, resourceId);
        btn_dh.setText(exchanged ? "已拥有" : "兑换");
        btn_dh.setEnabled(!exchanged);
    }
}