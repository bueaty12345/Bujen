package com.yunsong.bujen;

import static com.thingclips.sdk.blelib.utils.BluetoothUtils.getContext;
import static com.yunsong.bujen.Homepage.mDevice;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thingclips.smart.sdk.api.IResultCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZenbeatSetting extends AppCompatActivity implements View.OnClickListener{
    ImageView img_back,img_sy,img_ding,img_dong,img_fc,img_jk,img_tmd,vol_minus,vol_plus;
    Switch aSwitch;
    LinearLayout lin_sy,lin_yl,lin_ding,lin_dong,lin_fc,lin_jk,lin_tmd;
    static int volume=1;
    String[] sound={"zero_gear", "one_gear", "two_gear", "three_gear", "four_gear", "max_gear"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_zenbeat_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    private void init(){
        img_back=findViewById(R.id.img_back);
        img_sy=findViewById(R.id.img_sy);
        aSwitch=findViewById(R.id.switch_sy);
        lin_sy=findViewById(R.id.lin_sy);
        lin_yl=findViewById(R.id.lin_yl);
        lin_ding=findViewById(R.id.lin_ding);
        lin_dong=findViewById(R.id.lin_dong);
        lin_fc=findViewById(R.id.lin_fc);
        lin_jk=findViewById(R.id.lin_jk);
        lin_tmd=findViewById(R.id.lin_tmd);
        img_ding=findViewById(R.id.img_ding);
        img_dong=findViewById(R.id.img_dong);
        img_fc=findViewById(R.id.img_fc);
        img_jk=findViewById(R.id.img_jk);
        img_tmd=findViewById(R.id.img_tmd);
        vol_minus=findViewById(R.id.volume_minus);
        vol_plus=findViewById(R.id.volume_plus);

        lin_ding.setOnClickListener(this);
        lin_dong.setOnClickListener(this);
        lin_fc.setOnClickListener(this);
        lin_jk.setOnClickListener(this);
        lin_tmd.setOnClickListener(this);
        img_back.setOnClickListener(this);
        vol_minus.setOnClickListener(this);
        vol_plus.setOnClickListener(this);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {lin_sy.setVisibility(View.VISIBLE);
                    lin_yl.setVisibility(View.VISIBLE);
                    img_sy.setImageResource(R.drawable.laba);
                    setSound("105","one_gaer");}
                else {
                    lin_sy.setVisibility(View.GONE);
                    lin_yl.setVisibility(View.GONE);
                    img_sy.setImageResource(R.drawable.erji);
                    setSound("105","zero_gear");
                    }
            }
        });
    }

    private void setSyNull() {
        img_ding.setVisibility(View.INVISIBLE);
        img_dong.setVisibility(View.INVISIBLE);
        img_fc.setVisibility(View.INVISIBLE);
        img_jk.setVisibility(View.INVISIBLE);
        img_tmd.setVisibility(View.INVISIBLE);

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.lin_ding:
                setSyNull();
                setSound("103","one_voice");
                img_ding.setVisibility(View.VISIBLE);
                break;
            case R.id.lin_dong:
                setSyNull();
                setSound("103","two_voice");
                img_dong.setVisibility(View.VISIBLE);
                break;
            case R.id.lin_fc:
                setSyNull();
                setSound("103","three_voice");
                img_fc.setVisibility(View.VISIBLE);
                break;
            case R.id.lin_jk:
                setSyNull();
                setSound("103","four_voice");
                img_jk.setVisibility(View.VISIBLE);
                break;
            case R.id.lin_tmd:
                setSyNull();
                setSound("103","five_voice");
                img_tmd.setVisibility(View.VISIBLE);
                break;
            case R.id.volume_minus:
                if (volume>1){
                    volume--;
                    setSound("105",sound[volume]);
                }
                break;
            case R.id.volume_plus:
                if (volume<5){
                    volume++;
                    setSound("105",sound[volume]);
                }
                break;
        }
    }


}