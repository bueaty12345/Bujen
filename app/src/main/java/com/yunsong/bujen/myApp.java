package com.yunsong.bujen;

import static com.thingclips.smart.sdk.ThingBaseSdk.getApplication;

import android.app.Application;
import android.util.Log;

import com.facebook.soloader.SoLoader;
import com.gzl.smart.gzlminiapp.core.theme.AppThemeUtil;
import com.gzl.smart.gzlminiapp.miniapp.GZLMiniAppSDK;
import com.gzl.smart.gzlminiapp.smart_api.MiniAppConfig;
import com.thing.smart.miniappclient.ThingMiniAppClient;
import com.thingclips.smart.api.start.LauncherApplicationAgent;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.theme.config.ThemeConfig;
import com.thingclips.smart.theme.config.bean.ThemeBean;

public class myApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LauncherApplicationAgent.getInstance().onCreate(this);

        ThingHomeSdk.init(this);

        ThemeConfig.INSTANCE.loadTheme(null, true);
        ThemeConfig.INSTANCE.getThemeConfigBean();
        AppThemeUtil.INSTANCE.initConfig();


        ThingMiniAppClient.initialClient().initialize();

        SoLoader.init(this, false);

        GZLMiniAppSDK.init(null);
    }

}

