package com.fyang21117.smelldata;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DebugActivity extends AppCompatActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DebugActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        setTitle("气体传感");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * 2020年1月3日10:23:56
         * 插入使用Mtest.jar文件，进行计算，返回气味识别结果。
         *
         * */


    }
}
