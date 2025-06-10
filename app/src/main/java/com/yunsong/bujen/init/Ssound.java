package com.yunsong.bujen.init;

import static com.thingclips.sdk.blelib.utils.BluetoothUtils.getContext;
import static com.yunsong.bujen.Homepage.mDevice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thingclips.smart.sdk.api.IResultCallback;
import com.yunsong.bujen.Homepage;
import com.yunsong.bujen.R;

public class Ssound extends AppCompatActivity implements View.OnClickListener{
    LinearLayout lin_jy,lin_kq, lin_kqbj,lin_ding,lin_da,lin_gua,lin_don,lin_jybg;
    ImageView img_jy,img_kq,img_ding,img_da,img_gua,img_don,vol_minus,vol_plus;
    Button btn_next;
    TextView txt_skip;
    static int volume=1;
    String[] sound={"zero_gear", "one_gear", "two_gear", "three_gear", "four_gear", "max_gear"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ssound);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        lin_jy.setOnClickListener(this);
        lin_kq.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        txt_skip.setOnClickListener(this);
        lin_ding.setOnClickListener(this);
        lin_da.setOnClickListener(this);
        lin_gua.setOnClickListener(this);
        lin_don.setOnClickListener(this);

    }
    private void init(){
        lin_jy=findViewById(R.id.lin_jy);
        lin_kq=findViewById(R.id.lin_kq);
        lin_kqbj=findViewById(R.id.lin_kqbj);
        img_jy=findViewById(R.id.img_jy);
        img_kq=findViewById(R.id.img_kq);
        btn_next=findViewById(R.id.btn_next);
        txt_skip=findViewById(R.id.txt_skip);
        lin_ding=findViewById(R.id.lin_ding);
        lin_da=findViewById(R.id.lin_da);
        lin_gua=findViewById(R.id.lin_gua);
        lin_don=findViewById(R.id.lin_don);
        img_ding=findViewById(R.id.img_ding);
        img_da=findViewById(R.id.img_da);
        img_gua=findViewById(R.id.img_gua);
        img_don=findViewById(R.id.img_don);
        lin_jybg=findViewById(R.id.lin_jybg);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.lin_jy:
                lin_jy.setBackgroundResource(R.drawable.roundgrayb1);
                img_jy.setVisibility(View.VISIBLE);
                lin_kq.setBackgroundResource(R.drawable.roundgrayb0);
                img_kq.setVisibility(View.INVISIBLE);
                lin_kqbj.setVisibility(View.GONE);
                lin_jybg.setVisibility(View.VISIBLE);
                setSound("105","zero_gear");
                break;
            case R.id.lin_kq:
                lin_kq.setBackgroundResource(R.drawable.roundgrayb1);
                img_kq.setVisibility(View.VISIBLE);
                lin_jy.setBackgroundResource(R.drawable.roundgrayb0);
                img_jy.setVisibility(View.INVISIBLE);
                lin_kqbj.setVisibility(View.VISIBLE);
                lin_jybg.setVisibility(View.GONE);
                setSound("105","one_gaer");
                break;
            case R.id.btn_next:
                startActivity(new Intent(Ssound.this,SLighting.class));
                finish();
                break;
            case R.id.txt_skip:
                startActivity(new Intent(Ssound.this, Homepage.class));
                finish();
                break;
            case R.id.lin_ding:
                setSyNull();
                lin_ding.setBackgroundResource(R.drawable.roundgrayb);
                img_ding.setVisibility(View.VISIBLE);
                setSound("103","five_voice");
                break;
            case R.id.lin_da:
                setSyNull();
                lin_da.setBackgroundResource(R.drawable.roundgrayb);
                img_da.setVisibility(View.VISIBLE);
                setSound("103","two_voice");
                break;
            case R.id.lin_gua:
                setSyNull();
                lin_gua.setBackgroundResource(R.drawable.roundgrayb);
                img_gua.setVisibility(View.VISIBLE);
                setSound("103","three_voice");
                break;
            case R.id.lin_don:
                setSyNull();
                lin_don.setBackgroundResource(R.drawable.roundgrayb);
                img_don.setVisibility(View.VISIBLE);
                setSound("103","four_voice");
                break;
//            case R.id.volume_minus:
//                if (volume>1){
//                    volume--;
//                    setSound("105",sound[volume]);
//                }
//                break;
//            case R.id.volume_plus:
//                if (volume<5){
//                    volume++;
//                    setSound("105",sound[volume]);
//                }
//                break;
        }
    }
    private void setSound(String id,String Sound) {
        if (mDevice==null){
            Toast.makeText(getContext(), "设备未连接", Toast.LENGTH_SHORT).show();
            return;
        }
        mDevice.publishDps("{\""+id+"\":\""+Sound+"\"}", new IResultCallback() {
            @Override
            public void onError(String code, String error) {
                Toast.makeText(getContext(), "设置失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "设置成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setSyNull(){
        lin_ding.setBackgroundResource(R.drawable.roundgray00);
        lin_da.setBackgroundResource(R.drawable.roundgray00);
        lin_gua.setBackgroundResource(R.drawable.roundgray00);
        lin_don.setBackgroundResource(R.drawable.roundgray00);
        img_ding.setVisibility(View.INVISIBLE);
        img_da.setVisibility(View.INVISIBLE);
        img_gua.setVisibility(View.INVISIBLE);
        img_don.setVisibility(View.INVISIBLE);
    }
}