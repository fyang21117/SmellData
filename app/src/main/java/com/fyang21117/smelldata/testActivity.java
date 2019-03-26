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

public class testActivity extends AppCompatActivity implements OnItemClickListener {
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, testActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }
    private static String TAG = testActivity.class.getSimpleName();
    private ListView listView;
    private ListAdapter listAdapter;
    private EditText textView;

    public static int c1[] = new int[30];
    public static int c2[] = new int[30];
    public static int c3[] = new int[30];
    public static int c4[] = new int[30];
    String hex_str[]=new String[120];
    int dec_num[]=new int[120];
//    public static String[][] smelldata= new String[4][240];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setTitle("气味数据显示");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(false);

        dataread();
        listView.setOnItemClickListener(testActivity.this);
        //设置页面横屏
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        readRawTxt();
        OnItemClickListener listener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    android.view.View view, int position, long id) {
                String chartsTitleCurr[] = getResources().getStringArray(R.array.chartsTitle);
                if(position > chartsTitleCurr.length - 1) return;

                Bundle bundleSimple = new Bundle();
                Intent intent = new Intent();
                bundleSimple.putString("title", chartsTitleCurr[position]);
                intent.setClass(testActivity.this,ChartsActivity.class);
                bundleSimple.putInt("selected", position);
                bundleSimple.putIntArray("c1",c1);//保存int类型数组，在readRawTxt已经转换类型
                bundleSimple.putIntArray("c2",c2);
                bundleSimple.putIntArray("c3",c3);
                bundleSimple.putIntArray("c4",c4);

                intent.putExtras(bundleSimple);
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(listener);
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

    private void dataread() {
        listView = findViewById(R.id.list_view);
        listView.setDivider(new ColorDrawable(Color.BLACK));
        listView.setDividerHeight(2);
        new Thread() {
            @Override
            public void run() {
                super.run();
                List<Item> itemList = new ArrayList<>();
                String path = "http://192.168.11.4/smelldata/smelldata.xml";//http://192.168.10.216/yy_voice/items.xml"
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
                                    }
                                    break;
                                    case XmlPullParser.END_TAG: {
                                        if ("item".equals(nodeName)) {
                                            Item item = new Item();
                                            item.setName(name);
                                            item.setInfo(info);
                                            itemList.add(item);
                                        }
                                    }
                                    break;
                                    default:
                                        break;
                                }
                                eventType = xmlPullParser.next();//获取下一个元素
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    listAdapter = new MyAdapter(itemList, testActivity.this);
                    //listAdapter.notifyDataSetChanged();
                    listView.setAdapter(listAdapter);
                } catch (IOException e) {e.printStackTrace();}
                catch (XmlPullParserException e) {e.printStackTrace();}
                catch (Exception e) {e.printStackTrace();}
            }
        }.start();
    }

    public void readRawTxt() {
        textView = findViewById(R.id.smeelldata);
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer sb= new StringBuffer();
        BufferedReader bfReader=null;
        String temp;
        int line=0 ;

        try{
            InputStream input = getResources().openRawResource(R.raw.smelldata2018);
            Reader reader = new InputStreamReader(input);
            bfReader = new BufferedReader(reader);

                while((temp=bfReader.readLine())!=null){
                    temp = temp.replaceAll("12 34 ","");
//                    stringBuffer.append("r"+line+":"+temp);
                    String[] str = temp.split(" ");//str[8]
                    //for (int i = 0; i < 2; i++) {
                         for (int i = 0; i < str.length; i++){
                        /*c1[i+2*line]=str[i];
                        c2[i+2+2*line]=str[i+2];
                        c3[i+4+2*line]=str[i+4];
                        c4[i+6+2*line]=str[i+6];*/
                         stringBuffer.append(str[i]);
                        //stringBuffer.append(c1);
                    }
                    line++;
                    if(line%30==0) {//一行8个，30组共240byte数据时添加换行
                        stringBuffer.append("\n");
                        //c1[60]= c2[60]= c3[60]= c4[60]="\0";
                        //break;
                    }
                }
            }
        catch(Exception e){e.printStackTrace();}
        finally{
            if(bfReader != null){
                try{bfReader.close();}
                catch(Exception e){e.printStackTrace();}
            }
        }
        String string = stringBuffer.toString();
        String []data1 = string.split("\n");

        //System.arraycopy(data1,1,smelldata[0],1,10);
        // 44组数据，str1[0].length()每组长度240byte
        //Toast.makeText(this,String.valueOf(str1.length),Toast.LENGTH_SHORT).show();
        //textView.setText(str1[0]);
/*        for(int j=0;j<10;j++){
            sb.append(data1[8*j]);
            sb.append(data1[1+8*j]);
        }
        String s1 = sb.toString();*/
        ///smelldata[0][1]=data1[0];
        textView.setText(data1[0]);//前120个数据显示

        /**** k < 240/2=120*****/
        for(int k=0;k<data1[0].length()/2;k++){
            hex_str[k]=data1[0].substring(2*k,2*k+1);
            //将十六进制字符串转化成十进制int基本类型
            dec_num[k] = Integer.parseInt(hex_str[k],16);
            if(k<40) {
                c1[k] = dec_num[4 * k];
                c2[k] = dec_num[4 * k + 1];
                c3[k] = dec_num[4 * k + 2];
                c4[k] = dec_num[4 * k + 3];
            }
            Log.d(TAG, "dec_num:"+dec_num[k]);
        }
        //dataSeries1.add( c1[k]);
        //dataSeries2.add( c2[k]);
        //dataSeries3.add( c3[k]);
        //dataSeries4.add( c4[k]);
    }
}

