package com.yunsong.bujen.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.yunsong.bujen.R;
import com.yunsong.bujen.databean.BlessingRecordBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BlessingRecord extends AppCompatActivity {

    private TextView tv_title,tv_content,tv_date,tv_words,tv_event,tv_label;
    private ImageView img_main,btn_back;
    private FrameLayout layoutImageSection;
    private BlessingRecordBean bean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blessingrecord);
        init();

    }

    private void init(){
        bean = getIntent().getParcelableExtra("record");
        if (bean == null) return;

        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        tv_date = findViewById(R.id.tv_date);
        tv_words = findViewById(R.id.tv_words);
        tv_event = findViewById(R.id.tv_event);
        img_main = findViewById(R.id.img_main);
        btn_back = findViewById(R.id.btn_back);
        layoutImageSection=findViewById(R.id.layout_image_section);
        tv_label=findViewById(R.id.tv_label);

        if("Text".equals(bean.blessingMethod)){
            tv_label.setText("基础");
        }else {
            tv_label.setText("进阶");
        }
        tv_title.setText(bean.blessingTitle);
        tv_content.setText(bean.blessingContent);

        tv_date.setText(getCurrentTime());

        switch (bean.blessingMethod) {
            case "Text":
                tv_words.setText("800字限定");
                break;
            case "ImageText":
                tv_words.setText("900字 + 1图限定");
                break;
            case "Audio":
                tv_words.setText("6'00\"语音限定");
                break;
            default:
                tv_words.setText("800字限定");
                break;
        }

        tv_event.setText(bean.achieveTime + " 接福");

        if ((bean.blessingImageUrl == null || bean.blessingImageUrl.isEmpty()) &&
                "Text".equals(bean.blessingMethod)) {
            // 如果是纯文字且没有图片，隐藏整个图片区域
            layoutImageSection.setVisibility(View.GONE);
        } else {
            // 否则正常显示图片
            layoutImageSection.setVisibility(View.VISIBLE);
            if (bean.blessingImageUrl != null && !bean.blessingImageUrl.isEmpty()) {
                if (bean.blessingImageUrl.startsWith("http")) {
                    Glide.with(this).load(bean.blessingImageUrl).into(img_main);
                } else {
                    img_main.setImageURI(Uri.parse(bean.blessingImageUrl));
                }
            }
        }

        btn_back.setOnClickListener(v -> {
            returnResultAndFinish();
        });
    }

    private void returnResultAndFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("viewedId", bean.recordId);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnResultAndFinish();
    }

    // 获取当前时间字符串
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }
}
