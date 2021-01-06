package com.pichs.app.xwidget;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pichs.common.base.BaseActivity;
import com.pichs.common.widget.cardview.XCardButton;
import com.pichs.common.widget.utils.XTypefaceHelper;
import com.pichs.common.widget.view.XButton;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState) {

    }

    @Override
    protected void onCreate() {
        TextView tv = findViewById(R.id.tv1);
        XCardButton btn = findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XTypefaceHelper.setGlobalTypefaceFromAssets(getApplicationContext(), "leihong.ttf");
                XTypefaceHelper.setGlobalTypefaceStyle(getApplicationContext(), XTypefaceHelper.NONE);
            }
        });
        XButton normalBtn = findViewById(R.id.normalBtn);

        normalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                XTypefaceHelper.setGlobalTypefaceFromAssets(getApplicationContext(), "leihong.ttf");
//                XTypefaceHelper.clearObserver();

//                XTypefaceHelper.setGlobalTypeface(getApplicationContext(), XTypefaceHelper.TYPEFACE_BOLD);
                XTypefaceHelper.resetTypeface(MainActivity.this);
//                XTypefaceHelper.setGlobalTypefaceStyle(getApplicationContext(), XTypefaceHelper.NONE);
            }
        });
    }
}