package com.fyang21117.smelldata;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class testActivity extends AppCompatActivity implements OnItemClickListener,
        View.OnClickListener {
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, testActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    private static String TAG = testActivity.class.getSimpleName();

    public static int kind = 0;
    public static int c1[] = new int[30];
    public static int c2[] = new int[30];
    public static int c3[] = new int[30];
    public static int c4[] = new int[30];

    public static int min1, min2, min3, min4;
    public static int max;

    public static       EditText       Hexdata;
    public static       EditText       Decdata;

    public static final int            UPDATE    = 1;
    public static final int            UPDATE2   = 2;
    private             BufferedReader bfReader;
    private             InputStream    is;
    private             Reader         reader;
    private             String         temp="";
    int line = 0, max1 = 0, max2 = 0, max3 = 0, max4 = 0;

    public  int    rawId[]   = new int[]{R.raw.smoke, R.raw.perfume0327,
            R.raw.smelldata2018, R.raw.banana0329, R.raw.oilpaint190327, R.raw.orange0327,
            R.raw.orange0329, R.raw.orangepi};
    public  String dataUrl[] = {"http://www.minija.cn/smelldata/perfume0327.txt",
            "http://www.minija.cn/smelldata/smelldata2018.txt",
            "http://www.minija.cn/smelldata/smoke.txt",
            "http://www.minija.cn/smelldata/orangepi.txt",
            "http://www.minija.cn/smelldata/orange0329.txt",
            "http://www.minija.cn/smelldata/orange0327.txt",
            "http://www.minija.cn/smelldata/banana0329.txt",
            "http://www.minija.cn/smelldata/oilpaint190327.txt"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setTitle("气味数据显示");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(false);

        findViewById(R.id.b1).setOnClickListener(testActivity.this);
        findViewById(R.id.b2).setOnClickListener(testActivity.this);
        Hexdata = findViewById(R.id.Hexdata);
        Decdata = findViewById(R.id.Decdata);
        txtRead();
    }

    @Override
    public void onClick(View v) {
        String chartsTitleCurr[] = getResources().getStringArray(R.array.chartsTitle);
        Bundle bundleSimple = new Bundle();
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.b1: {
                Log.e(TAG, "*************折线图LineChart01View start*************");
                bundleSimple.putString("title", chartsTitleCurr[0]);
                intent.setClass(testActivity.this, ChartsActivity.class);
                bundleSimple.putInt("selected", 0);
            }
            break;
            case R.id.b2: {
                Log.e(TAG, "*************曲线图SplineChart03View start*************");
                bundleSimple.putString("title", chartsTitleCurr[1]);
                intent.setClass(testActivity.this, ChartsActivity.class);
                bundleSimple.putInt("selected", 1);
            }
            break;
            default:
                break;
        }
        bundleSimple.putIntArray("c1", c1);//保存int类型数组，在txtRead已经转换类型
        bundleSimple.putIntArray("c2", c2);
        bundleSimple.putIntArray("c3", c3);
        bundleSimple.putIntArray("c4", c4);
        bundleSimple.putInt("max", max);
        intent.putExtras(bundleSimple);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, Menu.FIRST + 1, 0, "刷新");
        menu.add(Menu.NONE, Menu.FIRST + 2, 0, "关于");
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case Menu.FIRST + 1: {
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                if (kind < 7) kind++;
                else kind = 0;
                editor.putInt("kind", kind);
                editor.apply();
                Toast.makeText(this, "当前数据path：" + dataUrl[kind], Toast.LENGTH_SHORT).show();
                txtRead();
            }
            break;
            case Menu.FIRST + 2:
                Toast.makeText(this, "about", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    /*读取数据流部分**/
    public void txtRead() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuffer   strBuf    = new StringBuffer();
                    final StringBuffer   hexBuf    = new StringBuffer();
                    final StringBuffer   decBuf    = new StringBuffer();
                    String         hex_str[] = new String[120];
                    int            dec_num[] = new int[120];
                    String         smellstr;
                    String[]       smelldata;
                    SharedPreferences sPref = getSharedPreferences("data", MODE_PRIVATE);
                    int num = sPref.getInt("kind", 0);
                    String path = dataUrl[num];
                    Log.i(TAG, "dataUrl[" + num + "]:" + path);

                    HttpURLConnection conn;
                    URL url = new URL(path);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(false);
                    conn.setDoInput(true);
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    conn.setRequestProperty("Content-type", "application/txt");
                    conn.setInstanceFollowRedirects(false);
                    //必须设置false，否则会自动redirect到重定向后的地址
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        //InputStream input = getResources().openRawResource(rawId[num]);
                        InputStream is = conn.getInputStream();
                        Reader reader = new InputStreamReader(is);
                        BufferedReader bfReader = new BufferedReader(reader);
                        String temp = "";
                        while ((temp = bfReader.readLine()) != null) {
                            temp = temp.substring(6);
                            String[] str = temp.split(" ");//str[4]
                            for (int i = 0; i < str.length; i++) {
                                strBuf.append(str[i]);
                            }
                            line++;
                            if (line % 30 == 0) { // 30行* 8字节，共240byte数据时添加换行
                                strBuf.append("\n");
                                break;
                            }
                        }
                        /*数据截取，进制转换，按列集合**/
                        smellstr = strBuf.toString();
                        smelldata = smellstr.split("\n");
                        Log.i(TAG, "smelldata[0]:" + smelldata[0]);

                        for (int k = 0; k < (smelldata[0].length() / 2); k++) {//120
                            hex_str[k] = smelldata[0].substring(2 * k, 2 * k + 2);
                            dec_num[k] = Integer.parseInt(hex_str[k], 16);//将十六进制字符串转化成十进制int基本类型
                            hexBuf.append(hex_str[k]);

                            if (dec_num[k] < 10) decBuf.append("\t");
                            decBuf.append(dec_num[k]);
                            decBuf.append("\t");

                            if ((k + 1) % 4 == 0) {
                                decBuf.append("\n");
                                hexBuf.append("\n");
                            }
                        }

                        //Log.e(TAG, "hex_str[" + 119+ "]=" + hex_str[119]);//按个输出,right!
                        //Log.e(TAG, "dec_num[" + 119 + "]=" + dec_num[119]);//按个输出,right!
                        for (int k = 0; k < 30; k++) {//k<dec_num.length
                            c1[k] = dec_num[4 * k];
                            c2[k] = dec_num[4 * k + 1];
                            c3[k] = dec_num[4 * k + 2];
                            c4[k] = dec_num[4 * k + 3];
                            max1 = getMax(max1, c1[k]);
                            max2 = getMax(max2, c2[k]);
                            max3 = getMax(max3, c3[k]);
                            max4 = getMax(max4, c4[k]);
                        }
                        max = getMax(getMax(getMax(max1, max2), max3), max4);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Hexdata.setText(hexBuf.toString());//120个十六进制数据
                                Decdata.setText(decBuf.toString());//120个十进制数据
                            }
                        });
                        if(bfReader != null) bfReader.close();
                        if(reader != null) reader.close();
                        if(is != null)is.close();
                        conn.disconnect();}

                    new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //使用handler更新主线程UI
                        Log.i(TAG, "sendMessage**********");
                        Message message = new Message();
                        message.what = UPDATE;
                        message.obj = hexBuf.toString();
                        mHandler.sendMessage(message);

                        Log.i(TAG, "sendMessage2**********");
                        Message message2 = new Message();
                        message2.what = UPDATE2;
                        message2.obj = decBuf.toString();
                        mHandler2.sendMessage(message2);
                    }
                }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int getMax(int a, int b) {
        if (a < b) return b;
        else return a;
    }

    //将 Handler 声明为静态内部类。并持有外部类的弱引用
    private static class MyHandler extends Handler {
        private final WeakReference<testActivity> mActivity;

        public MyHandler(testActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            testActivity activity = mActivity.get();
            if (activity != null) switch (msg.what) {
                case UPDATE:
                    Log.i(TAG, "case UPDATE:**********");
                    Hexdata.setText(msg.obj.toString());//120个十六进制数据
                    break;
                case UPDATE2:
                    Log.i(TAG, "case UPDATE2:**********");
                    Decdata.setText(msg.obj.toString());//120个十进制数据
                    break;
                default:
                    break;
            }
        }
    }

    private final MyHandler mHandler  = new MyHandler(this);
    private final MyHandler mHandler2 = new MyHandler(this);
}