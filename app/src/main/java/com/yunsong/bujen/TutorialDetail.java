package com.yunsong.bujen;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yunsong.bujen.adapter.TutorialSquareAdapter;
import com.yunsong.bujen.databean.MyTutorialBean;
import com.yunsong.bujen.utils.ApiHelper;
import com.yunsong.bujen.utils.DataStorageUtils;
import com.yunsong.bujen.utils.ExchangeHelper;
import com.yunsong.bujen.utils.FavoriteHelper;
import com.yunsong.bujen.utils.UserInfoUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorialDetail extends AppCompatActivity implements View.OnClickListener {
LinearLayout lin_sk;
GridView gridView;
    RelativeLayout img_cover;

TextView txt_mname,txt_content,txt_gdd,txt_description,txt_star;
ImageView img_selet,img_back;
    Button btn_dh;
    private ConfirmDialog dialog;
    Boolean hart=false;
private boolean isFullScreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tutorial_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSquare();

        init();
        btn_dh.setOnClickListener(this);
        img_back.setOnClickListener(this);
        img_selet.setOnClickListener(this);
        // 点击事件，切换全屏
//        videoView.setOnClickListener(v -> toggleFullScreen());
    }

    private void init(){
        txt_mname=findViewById(R.id.txt_mname);
        txt_content=findViewById(R.id.txt_content);
        txt_gdd=findViewById(R.id.txt_gdd);
        img_selet=findViewById(R.id.img_selet);
        txt_description=findViewById(R.id.txt_description);
        btn_dh=findViewById(R.id.btn_dh);
        img_back=findViewById(R.id.img_back);
        img_cover=findViewById(R.id.img_cover);
        gridView=findViewById(R.id.grid_tj);

        Intent intent=getIntent();

        String name = intent.getStringExtra("name");
        int gdd = intent.getIntExtra("gdd",0);
        String description=intent.getStringExtra("description");
        boolean sc=intent.getBooleanExtra("sc",false);
        String tutorialContent=intent.getStringExtra("tutorialContent");

        txt_mname.setText(name);
        txt_content.setText(tutorialContent);
        txt_gdd.setText("需功德值："+gdd);
        //判断是否收藏
        hart=sc;
        if (sc) {
            img_selet.setImageResource(R.drawable.collection_1);
        } else {
            img_selet.setImageResource(R.drawable.collection_2);
        }
        txt_description.setText(description);


        boolean dh=intent.getBooleanExtra("dh",false);
        btn_dh.setText(dh ? "已拥有" : "兑换");
        btn_dh.setEnabled(!dh);

        String videoUrl = intent.getStringExtra("videoUrl");
        if (videoUrl != null && !videoUrl.isEmpty()) {
            Glide.with(this)
                    .load(videoUrl)
                    .placeholder(R.drawable.detail_bg)
                    .error(R.drawable.detail_bg)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            img_cover.setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            img_cover.setBackground(placeholder);
                        }
                    });

        }
    }
    // 切换全屏模式
    private void toggleFullScreen() {
//        if (isFullScreen) {
//            // 恢复原来的 VideoView 大小
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
////            getSupportActionBar().show();
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  // 恢复竖屏
//
//            // 恢复 VideoView 的布局
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
//            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;  // 恢复原来高度
//            videoView.setLayoutParams(params);
//
//            isFullScreen = false;
//        } else {
//            // 进入全屏
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
////            getSupportActionBar().hide();
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  // 强制横屏
//
//            // 设置 VideoView 全屏显示
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
//            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;  // 设置为屏幕高度
//            videoView.setLayoutParams(params);
//
//            isFullScreen = true;
//        }
    }

    private void setSquare() {
        int level=getIntent().getIntExtra("level",0);
        String token = UserInfoUtils.getToken(this);

        ApiHelper.fetchTutorialPackageDetail(this, token, level, new ApiHelper.Callback<MyTutorialBean>() {
            @Override
            public void onSuccess(List<MyTutorialBean> list) {
                TutorialSquareAdapter adapter = new TutorialSquareAdapter(TutorialDetail.this, list);
                gridView.setAdapter(adapter);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(TutorialDetail.this, "加载教程失败：" + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 如果是全屏模式，按返回键时退出全屏
        if (isFullScreen) {
            toggleFullScreen();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_dh:
                if (btn_dh.getText().equals("兑换")) {
                    int requiredGdd = getIntent().getIntExtra("gdd", 0);
                    int localGdd = DataStorageUtils.getGddCount(this);

                    if (localGdd >= requiredGdd) {
                        ExchangeHelper.showExchangeDialog(this, requiredGdd,
                                getIntent().getStringExtra("resourceType"),
                                getIntent().getIntExtra("tutorialId", 0),
                                btn_dh);
                    } else {
                        Toast.makeText(this, "功德点不足，无法兑换", Toast.LENGTH_SHORT).show();
                    }
                }
                    break;
            case R.id.img_back:
                finish();
                break;
            case R.id.img_selet:
                hart = !hart; // 切换收藏状态
                img_selet.setImageResource(hart ? R.drawable.collection_1 : R.drawable.collection_2);

                int userId = UserInfoUtils.getUserId(TutorialDetail.this);
                String resourceType = getIntent().getStringExtra("resourceType");;
                int resourceId = getIntent().getIntExtra("id", 0);;
                String token=UserInfoUtils.getToken(this);
                FavoriteHelper.updateFavoriteStatus(hart,token, userId, "package", resourceId, new FavoriteHelper.Callback() {
                    @Override
                    public void onSuccess() {
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
//                        finish();
                        Toast.makeText(TutorialDetail.this, hart ? "收藏成功" : "取消收藏成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        // 失败，回滚UI和hart状态
                        hart = !hart;
                        runOnUiThread(() -> {
                            img_selet.setImageResource(hart ? R.drawable.collection_1 : R.drawable.collection_2);
                            Toast.makeText(TutorialDetail.this, "收藏状态更新失败：" + errorMsg, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String resourceType = getIntent().getStringExtra("resourceType");
        int resourceId = getIntent().getIntExtra("id", 0);

        boolean exchanged = ExchangeHelper.isExchanged(this, resourceType, resourceId);
        btn_dh.setText(exchanged ? "已拥有" : "兑换");
        btn_dh.setEnabled(!exchanged);
    }
}