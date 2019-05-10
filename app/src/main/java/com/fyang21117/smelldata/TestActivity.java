package com.fyang21117.smelldata;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import com.fyang21117.smelldata.LineView;

public class TestActivity extends AppCompatActivity {
    LineView lineView;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏

        lineView =  findViewById(R.id.line);
    }


}
