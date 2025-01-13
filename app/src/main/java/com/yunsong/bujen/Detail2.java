package com.yunsong.bujen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Detail2 extends AppCompatActivity implements View.OnClickListener{
    private ConfirmDialog dialog;
    TextView txt_name,txt_gdd,txt_author;
    ImageView img_hart,img_back;
    Button btn_dh,btn_again;
    Boolean hart=false;

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
        btn_again.setOnClickListener(this);
    }
    private void init(){
        txt_name=findViewById(R.id.txt_mname);
        txt_gdd=findViewById(R.id.txt_gdd);
        txt_author=findViewById(R.id.txt_auther);
        img_hart=findViewById(R.id.img_selet);
        btn_dh=findViewById(R.id.btn_dh);
        img_back=findViewById(R.id.img_back);
        btn_again=findViewById(R.id.btn_again);
        Intent intent = getIntent();

        // 接收传递的字符串和整数
        String name = intent.getStringExtra("name");
        String gdd = intent.getStringExtra("gdd");
        String auther = intent.getStringExtra("auther");
        txt_name.setText(name);
        txt_gdd.setText(gdd);
        txt_author.setText(auther);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_again: break;
            case R.id.img_selet:
                if (hart){
                    img_hart.setImageResource(R.drawable.collection_2);
                    hart=false;
                }else {
                    img_hart.setImageResource(R.drawable.collection_1);
                    hart=true;
                }
                break;
            case R.id.img_back:finish();break;
            case R.id.btn_dh:
                if(btn_dh.getText().equals("兑换")) {
                   setDialog();
                }

                break;
        }
    }

    private void setDialog() {
        ConfirmDialog.Builder builder = new ConfirmDialog.Builder(this);
        dialog = builder.cancelTouchout(false)
                .view(R.layout.dialog_confirm)
                .style(R.style.Dialog)
                .addViewOnclick(R.id.txt_confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn_dh.setText("已拥有" );
                        dialog.dismiss();  // 这里添加取消对话框的代码
                    }
                })
                .build();
        dialog.show();
    }

}