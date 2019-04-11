package com.fyang21117.smelldata;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.fyang21117.smelldata.view.Item;
import com.fyang21117.smelldata.view.MyAdapter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class testActivity extends AppCompatActivity implements OnItemClickListener ,View.OnClickListener{
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, testActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
    private static String TAG = testActivity.class.getSimpleName();
    private ListView listView;
    private ListAdapter listAdapter;
    private EditText Hexdata;
    private EditText Decdata;

    public static int c1[] = new int[30];
    public static int c2[] = new int[30];
    public static int c3[] = new int[30];
    public static int c4[] = new int[30];
    String hex_str[] = new String[120];
    int dec_num[] = new int[120];
    String string  ;
    String []data1 ;
//    public static String[][] smelldata= new String[4][240];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setTitle("气味数据显示");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(false);

        //dataRead();
        //listView.setOnItemClickListener(testActivity.this);
        findViewById(R.id.b1).setOnClickListener(testActivity.this);
        findViewById(R.id.b2).setOnClickListener(testActivity.this);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置页面横屏
        Log.e(TAG, "*************testActivity: txtRead() start*************");
        txtRead();
        Log.e(TAG, "*************testActivity: txtRead() finish*************");

/*        OnItemClickListener listener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,android.view.View view, int position, long id) {
                Log.e(TAG, "*************testActivity: listener start*************");
                 String chartsTitleCurr[] = getResources().getStringArray(R.array.chartsTitle);
                if(position > chartsTitleCurr.length - 1) return;

                Bundle bundleSimple = new Bundle();
                Intent intent = new Intent();
                bundleSimple.putString("title", chartsTitleCurr[position]);
                intent.setClass(testActivity.this,ChartsActivity.class);
                bundleSimple.putInt("selected", position);
                bundleSimple.putIntArray("c1",c1);//保存int类型数组，在txtRead已经转换类型
                bundleSimple.putIntArray("c2",c2);
                bundleSimple.putIntArray("c3",c3);
                bundleSimple.putIntArray("c4",c4);

                intent.putExtras(bundleSimple);
                startActivity(intent);
            }
        };*/
       // listView.setOnItemClickListener(listener);
        Log.e(TAG, "*************testActivity:onCreate() finish*************");
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "*************b1 b2 start*************");
        String chartsTitleCurr[] = getResources().getStringArray(R.array.chartsTitle);
        Bundle bundleSimple = new Bundle();
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.b1:{
                bundleSimple.putString("title", chartsTitleCurr[0]);
                intent.setClass(testActivity.this,ChartsActivity.class);
                bundleSimple.putInt("selected", 0);
            }break;
            case R.id.b2:{
                bundleSimple.putString("title", chartsTitleCurr[1]);
                intent.setClass(testActivity.this,ChartsActivity.class);
                bundleSimple.putInt("selected", 1);
            }break;
            default:break;
            }
        bundleSimple.putIntArray("c1",c1);//保存int类型数组，在txtRead已经转换类型
        bundleSimple.putIntArray("c2",c2);
        bundleSimple.putIntArray("c3",c3);
        bundleSimple.putIntArray("c4",c4);
        intent.putExtras(bundleSimple);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, Menu.FIRST + 1, 0, "刷新");
        menu.add(Menu.NONE, Menu.FIRST + 2, 0, "关于");
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //String text = parent.getItemAtPosition(position) + "";// 指定位置的内容
        //Toast.makeText(testActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                //dastaread();
                //listAdapter.notifyAll();
                break;
            case Menu.FIRST + 2:
                Toast.makeText(this, "about", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

 /*   private void dataRead() {
        listView = findViewById(R.id.list_view);
        listView.setDivider(new ColorDrawable(Color.BLACK));
        listView.setDividerHeight(2);
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<Item> itemList = new ArrayList<>();
                String path = "http://yf21117.com/smelldata/items.xml";//http://192.168.10.216/yy_voice/items.xml"
                HttpURLConnection conn;
                try {
                    URL url = new URL(path);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    if (conn.getResponseCode() == 200) {//客户端请求成功
                        InputStream is = conn.getInputStream();
                        XmlPullParser xmlPullParser = Xml.newPullParser();//获取解析器
                        xmlPullParser.setInput(is, "utf-8");//设置输入流和编码
                        int eventType = xmlPullParser.getEventType();//解析器的事件类型
                        String name = "";
                        String info = "";
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            String nodeName = xmlPullParser.getName();
                            try {
                                switch (eventType) {
                                    case XmlPullParser.START_TAG: {
                                        if ("name".equals(nodeName)) {
                                            name = xmlPullParser.nextText();
                                        } else if ("info".equals(nodeName)) {
                                            info = xmlPullParser.nextText();
                                        }
                                    }break;
                                    case XmlPullParser.END_TAG: {
                                        if ("item".equals(nodeName)) {
                                            Item item = new Item();
                                            item.setName(name);
                                            item.setInfo(info);
                                            itemList.add(item);
                                        }
                                    }break;
                                    default:break;
                                }
                                eventType = xmlPullParser.next();//获取下一个元素
                            } catch (IOException e) {    e.printStackTrace();    }
                        }
                    }
                    listAdapter = new MyAdapter(itemList, testActivity.this);
                    //listAdapter.notifyDataSetChanged();
                    listView.setAdapter(listAdapter);
                }
                catch (IOException e) {e.printStackTrace();}
                catch (XmlPullParserException e) {e.printStackTrace();}
                catch (Exception e) {e.printStackTrace();}
            }
        }.start();
    }*/

    public void txtRead() {
        Hexdata = findViewById(R.id.Hexdata);
        Decdata = findViewById(R.id.Decdata);
        StringBuffer strBuffer = new StringBuffer();
        StringBuffer dec = new StringBuffer();
        //StringBuffer sb= new StringBuffer();
        BufferedReader bfReader=null;
        String temp;
        int line=0 ;

        /*读取数据流部分**/
        try{
            InputStream input = getResources().openRawResource(R.raw.banana0329);
            Reader reader = new InputStreamReader(input);
            bfReader = new BufferedReader(reader);
                while((temp=bfReader.readLine()) != null){
                    //temp = temp.replaceAll("12 34 ","");
//                    strBuffer.append("r"+line+":"+temp);
                    temp = temp.substring(5);
                    String[] str = temp.split(" ");//4数据8字节
                         for (int i = 0; i < str.length; i++){
                         strBuffer.append(str[i]);
                    }
                    line++;
                    if(line%30==0) {//一行4个，8字节，30组，共240byte数据时添加换行
                        strBuffer.append("\n");
                    }
                }
            }
        catch(Exception e){e.printStackTrace();}
        finally{
            if(bfReader != null){
                try{    bfReader.close();  }
                catch(Exception e){ e.printStackTrace();    }
            }
        }

        /*数据截取，进制转换，按列集合**/
        string = strBuffer.toString();
        data1 = string.split("\n");

        //文件夹44组数据，str1[0].length()每组长度240byte
        //Toast.makeText(this,String.valueOf(str1.length),Toast.LENGTH_SHORT).show();
        Hexdata.setText(data1[0]);//前120个数据,240字节
        Log.e(TAG, "Hexdata data1[0]:"+data1[0]);
//        Log.e(TAG, "data1[1]:"+data1[1]);
//        Log.e(TAG, "data1[2]:"+data1[2]);
//        Log.e(TAG, "data1[3]:"+data1[3]);

        for(int k=0;k<data1[0].length()/2;k++){//k<120
            hex_str[k]=data1[0].substring(2*k,2*k+2);
//            Log.e(TAG, "hex_str["+k+"]="+hex_str[k]);
            dec_num[k] = Integer.parseInt(hex_str[k],16);//将十六进制字符串转化成十进制int基本类型
//            Log.e(TAG, "dec_num["+k+"]="+dec_num[k]);
            dec.append(dec_num[k]);
        }

        Decdata.setText( dec.toString());//10进制数值
        Log.e(TAG, "Decdata dec_num[k]:"+dec.toString());

        for(int k = 0;k<30;k++){//k<dec_num.length
                c1[k] = dec_num[4 * k];
                c2[k] = dec_num[4 * k + 1];
                c3[k] = dec_num[4 * k + 2];
                c4[k] = dec_num[4 * k + 3];
            Log.e(TAG, "c1["+k+"]="+c1[k]);
        }
        Log.e(TAG, "*************ChartActivity: txtRead() over*************");
        //dataSeries1.add( c1[k]);
        //dataSeries2.add( c2[k]);
        //dataSeries3.add( c3[k]);
        //dataSeries4.add( c4[k]);
    }
}

