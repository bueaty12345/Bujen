package com.yunsong.bujen.device;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.yunsong.bujen.R;
import com.thingclips.smart.android.device.bean.UpgradeInfoBean;
//import com.thingclips.smart.api.MicroContext;
//import com.thingclips.smart.api.router.UrlBuilder;
//import com.thingclips.smart.api.service.RouteEventListener;
//import com.thingclips.smart.api.service.ServiceEventListener;
//import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
//import com.thingclips.smart.panel.ota.service.AbsOtaCallerService;
import com.thingclips.smart.sdk.api.IGetOtaInfoCallback;
import com.thingclips.smart.sdk.api.IThingOTAService;

import java.util.List;

public class Upgrade extends AppCompatActivity {
    TextView txt_new,txt_mz,txt_mcu;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upgrade);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
    private void init(){
        txt_new=findViewById(R.id.txt_new);
        txt_mz=findViewById(R.id.txt_mz);
        txt_mcu=findViewById(R.id.txt_mcu);
        Intent it=getIntent();
        id=it.getStringExtra("id");
        IThingOTAService iThingOTAService = ThingHomeSdk.newOTAServiceInstance(id);
        iThingOTAService.getFirmwareUpgradeInfo(new IGetOtaInfoCallback() {
            @Override
            public void onSuccess(List<UpgradeInfoBean> upgradeInfoBeans) {
                switch (upgradeInfoBeans.get(0).getUpgradeStatus()){
                    case 0:txt_new.setText("无新版本");break;
                    case 1:txt_new.setText("有新版本");break;
                    case 2:txt_new.setText("在升级中");break;
                    case 5:txt_new.setText("等待设备唤醒");break;
                }
                txt_mz.setText(upgradeInfoBeans.get(0).getCurrentVersion());

            }

            @Override
            public void onFailure(String code, String error) {

            }
        });
//        // 业务包初始化
//        BizBundleInitializer.init(getApplication(), new RouteEventListener() {
//            @Override
//            public void onFaild(int errorCode, UrlBuilder urlBuilder) {
//                // 路由未实现回调
//                // 点击无反应表示路由未现实，需要在此实现，urlBuilder.target 目标路由，urlBuilder.params 路由参数
//                Log.e("router not implement", urlBuilder.target + urlBuilder.params.toString());
//            }
//        }, new ServiceEventListener() {
//            @Override
//            public void onFaild(String serviceName) {
//                // 服务未实现回调
//                Log.e("service not implement", serviceName);
//            }
//        });
//        //判断是否可升级
//        AbsOtaCallerService absOtaCallerService = MicroContext.getServiceManager().findServiceByInterface(AbsOtaCallerService.class.getName());
//        if (absOtaCallerService != null) {
//            if (absOtaCallerService.isSupportUpgrade(id)) {
//                // 进入可升级页面
//                absOtaCallerService.goFirmwareUpgrade(this, id);
//            } else {
//                // can not support ota ability
//            }
//        }


    }
}