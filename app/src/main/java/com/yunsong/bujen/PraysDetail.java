package com.yunsong.bujen;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yunsong.bujen.databean.BlessingBean;
import com.yunsong.bujen.fragment.SettingsViewModel;
import com.yunsong.bujen.utils.DataStorageUtils;
import com.yunsong.bujen.utils.ExchangeHelper;
import com.yunsong.bujen.utils.FavoriteHelper;
import com.yunsong.bujen.utils.GddManager;
import com.yunsong.bujen.utils.UserInfoUtils;

public class PraysDetail extends AppCompatActivity implements View.OnClickListener{
    private GridView gridView;
    private ImageView img_back, img_selet;
    private Button btn_dh, btn_again;
    private RelativeLayout img_cover;
    private TextView txt_mname, txt_blessingCategory, txt_date, txt_gdd, txt_zen,txt_blessingMethod,txt_virtuePoints;

    private boolean hart = false;
    private SettingsViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prays_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        BlessingBean bean = (BlessingBean) getIntent().getSerializableExtra("blessing");
        if (bean != null) {
            bindData(bean);
        }

    }

    private void initView() {
        gridView = findViewById(R.id.grid_tj);
        img_back = findViewById(R.id.img_back);
        img_selet = findViewById(R.id.img_selet);
        btn_dh = findViewById(R.id.btn_dh);
        btn_again = findViewById(R.id.btn_again);
        img_cover = findViewById(R.id.img_cover);

        txt_mname = findViewById(R.id.txt_mname);
        txt_blessingCategory = findViewById(R.id.txt_blessingCategory);
        txt_date = findViewById(R.id.txt_date);
        txt_gdd = findViewById(R.id.txt_gdd);
        txt_zen = findViewById(R.id.textView22);

        txt_blessingMethod=findViewById(R.id.txt_blessingMethod);

        txt_virtuePoints=findViewById(R.id.virtuePoints);
        sharedViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        GddManager.init(this, sharedViewModel);

        // 监听功德点 ViewModel 更新 txt_gdd
        sharedViewModel.getGddCont().observe(this, gddCount -> {
            txt_virtuePoints.setText(String.valueOf(gddCount));
        });

        img_back.setOnClickListener(this);
        img_selet.setOnClickListener(this);
        btn_dh.setOnClickListener(this);


    }

    private void bindData(BlessingBean bean) {
        txt_mname.setText(bean.getBlessingTheme());
        txt_blessingCategory.setText("基础".equals(bean.getBlessingCategory()) ? "基础" : "进阶");
        txt_date.setText(bean.getCreatedAt());
        txt_gdd.setText("需功德值：" + bean.getRequiredMeritPoints());
        txt_zen.setText(bean.getZenQuote());
        txt_blessingMethod.setText(bean.blessingMethod);
        if (bean.blessingMethod == null) {
            txt_blessingMethod.setText("800字限定");
        } else {
            switch (bean.blessingMethod) {
                case "Text":
                    txt_blessingMethod.setText("800字限定");
                    break;
                case "ImageText":
                    txt_blessingMethod.setText("900字 + 1图限定");
                    break;
                case "Audio":
                    txt_blessingMethod.setText("6'00\"语音限定");
                    break;
                default:
                    txt_blessingMethod.setText("800字限定");
                    break;
            }
        }


        boolean dh=bean.isDh();

        int count = ExchangeHelper.getExchangeCount(this, bean.getResourceType(), bean.getBlessingId());
        btn_again.setText("已拥有" + count);

        Button btnOwn = findViewById(R.id.btn_again);
        btnOwn.setText("已拥有0");

        //判断是否收藏

        img_selet = findViewById(R.id.img_selet);
        boolean sc=bean.isSc();
        hart = sc;
        if (sc) {
            img_selet.setImageResource(R.drawable.collection_1);
        } else {
            img_selet.setImageResource(R.drawable.collection_2);
        }

        String bgUrl  = bean.getBlessingBackgroundUrl();
        if (bgUrl != null && !bgUrl.isEmpty()) {
            Glide.with(this)
                    .load(bgUrl)
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

//    private void setSquare() {

//
//    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_selet:
                hart = !hart; // 切换收藏状态
                img_selet.setImageResource(hart ? R.drawable.collection_1 : R.drawable.collection_2);

                int userId = UserInfoUtils.getUserId(PraysDetail.this);
                String resourceType = getIntent().getStringExtra("resourceType");
                int resourceId = getIntent().getIntExtra("blessingId", 0);  ;
                String token=UserInfoUtils.getToken(this);
                FavoriteHelper.updateFavoriteStatus(hart,token, userId, resourceType, resourceId, new FavoriteHelper.Callback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(PraysDetail.this, hart ? "收藏成功" : "取消收藏成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        hart = !hart;
                        runOnUiThread(() -> {
                            img_selet.setImageResource(hart ? R.drawable.collection_1 : R.drawable.collection_2);
                            Toast.makeText(PraysDetail.this, "收藏状态更新失败：" + errorMsg, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                break;
            case R.id.btn_dh:
                if (btn_dh.getText().equals("兑换")) {
                    int requiredGdd = getIntent().getIntExtra("gdd", 0);
                    int localGdd = DataStorageUtils.getGddCount(this);

                    if (localGdd >= requiredGdd) {
                        ExchangeHelper.showPrayExchangeDialog(this, requiredGdd,
                                getIntent().getStringExtra("resourceType"),
                                getIntent().getIntExtra("blessingId", 0),
                                btn_dh);
                    } else {
                        Toast.makeText(this, "功德点不足，无法兑换", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        String resourceType = getIntent().getStringExtra("resourceType");
        int resourceId = getIntent().getIntExtra("blessingId", 0);

        int exchangedCount = ExchangeHelper.getExchangeCount(this, resourceType, resourceId);
        Button btnOwn = findViewById(R.id.btn_again);
        btnOwn.setText("已拥有" + exchangedCount);
    }

}