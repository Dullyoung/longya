package com.longya.player;

import android.app.Application;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

/*
 * Created by　Dullyoung on 2020/12/21
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        UMConfigure.init(this,"5fe0079e0b4a4938464bab59"
                ,"",UMConfigure.DEVICE_TYPE_PHONE,"");
        PlatformConfig.setWeixin("wx246689996532261a", "9f63f51f9a6dfebcd83708875b55934f");
        PlatformConfig.setWXFileProvider("com.longya.player.WXFileProvider");
    }
}
