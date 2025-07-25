package com.yunsong.bujen.setting;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.ConfirmDialog;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.BlessingRecordBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PrayHistory extends AppCompatActivity implements View.OnClickListener{
    GridLayout gridLayout;
    ImageView img_back;
    ArrayList<BlessingRecordBean> blessingList;
    private static ConfirmDialog dialog;

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

        blessingList = (ArrayList<BlessingRecordBean>) getIntent().getSerializableExtra("blessingList");
        if (blessingList != null) {
            populateGrid(blessingList);
        }
    }
    private void init(){
        img_back=findViewById(R.id.img_back);
        gridLayout=findViewById(R.id.grid_container);

        img_back.setOnClickListener(this);
    }

    private void populateGrid(ArrayList<BlessingRecordBean> list) {
        gridLayout.removeAllViews();
        for (BlessingRecordBean bean : list) {
            int coverResId = getCoverResId(bean);

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = dpToPx(100);
            params.height = dpToPx(130); // 额外高度容纳文字
            params.setMargins(dpToPx(5), dpToPx(5), dpToPx(5), dpToPx(5));
            itemLayout.setLayoutParams(params);
            itemLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            ImageView imageView = new ImageView(this);
            imageView.setImageResource(coverResId);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(100), dpToPx(100)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            TextView textView = new TextView(this);
            textView.setText(bean.blessingTitle);
            textView.setTextSize(14);
            textView.setGravity(Gravity.CENTER);
            textView.setMaxLines(1);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            itemLayout.setOnClickListener(v -> {
                long now = System.currentTimeMillis();
                long achieveMillis = parseTime(bean.achieveTime);
                boolean isUnlocked = now >= achieveMillis;
                boolean isViewed = isRecordViewed(bean.recordId);

                if (!isUnlocked) {
                    Toast.makeText(this, "还未达到接福时间", Toast.LENGTH_SHORT).show();
                } else if (!isViewed) {
                    showUnlockDialog(bean);
                } else {
                    Intent intent = new Intent(PrayHistory.this, BlessingRecord.class);
                    intent.putExtra("record", bean);
                    startActivityForResult(intent, 1001);
                }
            });

            itemLayout.addView(imageView);
            itemLayout.addView(textView);
            gridLayout.addView(itemLayout);
        }
    }

    private int getCoverResId(BlessingRecordBean bean) {
        long now = System.currentTimeMillis();
        long achieveMillis = parseTime(bean.achieveTime);
        boolean isUnlocked = now >= achieveMillis;
        boolean isViewed = isRecordViewed(bean.recordId);

        if (!isUnlocked) return R.drawable.icon_praying;
        else return isViewed ? R.drawable.icon_history2 : R.drawable.icon_pray;
    }

    private long parseTime(String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(timeStr);
            return date != null ? date.getTime() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private boolean isRecordViewed(int recordId) {
        SharedPreferences sp = getSharedPreferences("pray_history", MODE_PRIVATE);
        return sp.getBoolean("viewed_" + recordId, false);
    }

    private void markRecordViewed(int recordId) {
        SharedPreferences sp = getSharedPreferences("pray_history", MODE_PRIVATE);
        sp.edit().putBoolean("viewed_" + recordId, true).apply();
    }
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            int viewedId = data.getIntExtra("viewedId", -1);
            if (viewedId != -1) {
                markRecordViewed(viewedId);
                populateGrid(blessingList);
            }
        }
    }

    private void showUnlockDialog(BlessingRecordBean bean) {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(this);
        dialog = builder
                .cancelTouchout(false)
                .view(R.layout.dialog_prayrecordconfirm)
                .style(R.style.Dialog)
                .build();

        dialog.show();

        TextView tvRow1Right = dialog.findViewById(R.id.tv_row1_right);
        TextView tvRow2Right = dialog.findViewById(R.id.tv_row2_right);

        tvRow1Right.setText(formatDate(bean.blessingTime));
        tvRow2Right.setText(formatDate(bean.achieveTime));

        TextView txtCancel = dialog.findViewById(R.id.txt_cancel);
        TextView txtConfirm = dialog.findViewById(R.id.txt_confirm);

        txtCancel.setOnClickListener(v -> dialog.dismiss());

        txtConfirm.setOnClickListener(v -> {
            dialog.dismiss();

            markRecordViewed(bean.recordId);
            populateGrid(blessingList);

            Intent intent = new Intent(PrayHistory.this, BlessingRecord.class);
            intent.putExtra("record", bean);
            startActivityForResult(intent, 1001);
        });
    }

    private String formatDate(String rawDate) {
        if (TextUtils.isEmpty(rawDate)) return "/";
        try {
            SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat toFormat = new SimpleDateFormat("yyyy 年 MM 月 dd 日", Locale.getDefault());
            Date date = fromFormat.parse(rawDate);
            return date != null ? toFormat.format(date) : "/";
        } catch (Exception e) {
            e.printStackTrace();
            return "/";
        }
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
