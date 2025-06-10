package com.yunsong.bujen.device;

import static com.yunsong.bujen.Homepage.homeId;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.R;
import com.yunsong.bujen.init.Connect;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.sdk.bean.DeviceBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Devices extends AppCompatActivity implements View.OnClickListener{
    ImageView img_back;
    ListView listView;
    ImageView img_add;
    List<String> idlist;
    List<String> namelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_devices);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    void init(){
        img_back=findViewById(R.id.img_back);
        listView=findViewById(R.id.list_sblb);
        img_add=findViewById(R.id.img_add);

        img_back.setOnClickListener(this);
        img_add.setOnClickListener(this);
        getDeviceMassage();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent it=new Intent(Devices.this,Device.class);
                it.putExtra("id",idlist.get(i));
                it.putExtra("name",namelist.get(i));
                        startActivity(it);
            }
        });
    };
    private void getDeviceMassage() {
        ThingHomeSdk.newHomeInstance(homeId).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                if(homeBean.getDeviceList().size()>0){
                    Toast.makeText(Devices.this, "设备获取成功", Toast.LENGTH_SHORT).show();
                    setList(homeBean.getDeviceList());
//                    mDevice = ThingHomeSdk.newDeviceInstance(homeBean.getDeviceList().get(0).getDevId());
                }else {
                    Toast.makeText(Devices.this, "没有绑定设备", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                Toast.makeText(Devices.this, "获取设备失败"+errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setList(List<DeviceBean> deviceList) {
        idlist=new ArrayList<>();
        namelist=new ArrayList<>();
        List<Map<String, String>> data = new ArrayList<>();
        for (int i=0;i<deviceList.size();i++){
            Map map=new HashMap();
            idlist.add(deviceList.get(i).getDevId());
            namelist.add(deviceList.get(i).getName());
            map.put("id",deviceList.get(i).getDevId());
            map.put("name",deviceList.get(i).getName());
            data.add(map);
        }
        String[] from = {"name"}; // 数据源的键
        int[] to = {R.id.txt_mname}; // 布局文件中的视图 ID
        SimpleAdapter adapter=new SimpleAdapter(this,data,R.layout.item_devices,from,to);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_add:
                startActivity(new Intent(Devices.this, Connect.class));
                break;
        }
    }
}