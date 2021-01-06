package com.pichs.app.xwidget;

import android.app.Application;

import com.pichs.common.widget.utils.XTypefaceHelper;
import com.pichs.xhttp.HttpHelper;

/**
 * 入口
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XTypefaceHelper.init(this);
        HttpHelper.obtain().init();
    }
}
