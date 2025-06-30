package com.yunsong.bujen.device;

import static com.yunsong.bujen.Homepage.homeId;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thingclips.smart.sdk.api.IDevListener;
import com.thingclips.smart.sdk.api.IThingDevice;
import com.yunsong.bujen.R;
import com.yunsong.bujen.adapter.DeviceAdapter;
import com.yunsong.bujen.init.Connect;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.sdk.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static IThingDevice mDevice=null;
    private DeviceAdapter adapter;
    private List<DeviceBean> deviceList = new ArrayList<>();


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

        adapter = new DeviceAdapter(this, deviceList);
        listView.setAdapter(adapter);
        getDeviceMassage();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceBean device = deviceList.get(i);
                Intent it = new Intent(Devices.this, Device.class);
                it.putExtra("id", device.getDevId());
                it.putExtra("name", device.getName());
                startActivity(it);
            }
        });
    };
    private void getDeviceMassage() {
        ThingHomeSdk.newHomeInstance(homeId).getHomeDetail(new IThingHomeResultCallback() {
            @Override
            public void onSuccess(HomeBean homeBean) {
                if(homeBean.getDeviceList().size()>0){
                    deviceList.clear();
                    deviceList.addAll(homeBean.getDeviceList());
                    runOnUiThread(() -> adapter.notifyDataSetChanged());

                    // 注册监听第一个设备的DP更新（如果要监听所有设备，需要注册所有）
                    for (DeviceBean device : homeBean.getDeviceList()) {
                        IThingDevice devInstance = ThingHomeSdk.newDeviceInstance(device.getDevId());
                        devInstance.registerDevListener(new IDevListener() {
                            @Override
                            public void onDpUpdate(String devId, String dpStr) {
                                try {
                                    JSONObject jsonObject = new JSONObject(dpStr);
                                    if (jsonObject.has("102")) {
                                        int batteryLevel = jsonObject.getInt("102");
                                        Log.d("电量更新", "设备：" + devId + "，电量：" + batteryLevel);
                                        runOnUiThread(() -> adapter.updateBattery(devId, batteryLevel));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override public void onRemoved(String devId) {}
                            @Override public void onStatusChanged(String devId, boolean online) {}
                            @Override public void onNetworkStatusChanged(String devId, boolean status) {}
                            @Override public void onDevInfoUpdate(String devId) {}
                        });
                    }
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