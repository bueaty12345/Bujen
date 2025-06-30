package com.yunsong.bujen;

import static com.thingclips.smart.sdk.ThingBaseSdk.getApplication;

import android.app.Application;
import android.util.Log;

import com.facebook.soloader.SoLoader;
import com.gzl.smart.gzlminiapp.core.theme.AppThemeUtil;
import com.gzl.smart.gzlminiapp.miniapp.GZLMiniAppSDK;
import com.gzl.smart.gzlminiapp.smart_api.MiniAppConfig;
import com.thing.smart.miniappclient.ThingMiniAppClient;
import com.thingclips.smart.api.MicroContext;
import com.thingclips.smart.api.router.UrlBuilder;
import com.thingclips.smart.api.service.RedirectService;
import com.thingclips.smart.api.service.RouteEventListener;
import com.thingclips.smart.api.service.ServiceEventListener;
import com.thingclips.smart.api.start.LauncherApplicationAgent;
import com.thingclips.smart.bizbundle.initializer.BizBundleInitializer;
import com.thingclips.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.theme.config.ThemeConfig;
import com.thingclips.smart.theme.config.bean.ThemeBean;

public class myApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LauncherApplicationAgent.getInstance().onCreate(this);

        ThingHomeSdk.init(this);

//        ThemeConfig.INSTANCE.loadTheme(null, true);
//        ThemeConfig.INSTANCE.getThemeConfigBean();
//        AppThemeUtil.INSTANCE.initConfig();

        // 业务包初始化
        BizBundleInitializer.init(this, new RouteEventListener() {
            @Override
            public void onFaild(int errorCode, UrlBuilder urlBuilder) {
                // 路由未实现回调
                // 点击无反应表示路由未现实，需要在此实现，urlBuilder.target 目标路由，urlBuilder.params 路由参数
                Log.e("router not implement", urlBuilder.target + urlBuilder.params.toString());
            }
        }, new ServiceEventListener() {
            @Override
            public void onFaild(String serviceName) {
                // 服务未实现回调
                Log.e("service not implement", serviceName);
            }
        });

// 注册家庭服务，商城业务包可以不注册此服务
//        BizBundleInitializer.registerService(AbsBizBundleFamilyService.class, new BizBundleFamilyServiceImpl());
//        //拦截已存在的路由，通过参数跳转至自定义实现页面
//        RedirectService service = MicroContext.getServiceManager().findServiceByInterface(RedirectService.class.getName());
//        service.registerUrlInterceptor(new RedirectService.UrlInterceptor() {
//            @Override
//            public void forUrlBuilder(UrlBuilder urlBuilder, RedirectService.InterceptorCallback interceptorCallback) {
//                //Such as:
//                //Intercept the event of clicking the panel right menu and jump to the custom page with the parameters of urlBuilder
//                //例如：拦截点击面板右上角按钮事件，通过 urlBuilder 的参数跳转至自定义页面
//                /**
//                 if (urlBuilder.target.equals("panelAction") && urlBuilder.params.getString("action").equals("gotoPanelMore")) {
//                 interceptorCallback.interceptor("interceptor");
//                 Log.e("interceptor", urlBuilder.params.toString());
//                 } else {
//                 interceptorCallback.onContinue(urlBuilder);
//                 }
//                 */
//            }
//        });


    }

}

