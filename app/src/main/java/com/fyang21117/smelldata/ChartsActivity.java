package com.fyang21117.smelldata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.fyang21117.smelldata.view.DemoView;
import com.fyang21117.smelldata.view.LineChart01View;
import com.fyang21117.smelldata.view.SplineChart03View;
import com.fyang21117.smelldata.view.event.mZoomControls;

import static android.content.ContentValues.TAG;

/**
 * 关于整个图表缩放的说明 :
 * 	Demo中提供了两种缩放模式:
 * 		1. 通过 ZoomControls控件调用DemoView中的Zoom相关函数来缩放图表.
 * 		  通过这种方式缩放后的点击事件。
 * 		禁用方式:
 * 			可通过直接继承GraphicalView或通过覆盖onTouchEvent事件来禁用
 * 		2. 通过chart.enableScale()来激活通过双指手势对图表进行缩放。
 * 		 
 *      禁用方式:
 *      	chart.disableScale();
 * 
 * 图表区的平移  :  
 *  激活图表区的平移     
 * 		chart.enablePanMode()
 * 
 *      如果数据量比较大，感觉平移不够 顺畅，可以禁用图库的高精度计算，函数: 
 *			chart.disableHighPrecision();
 *      即，忽略掉Java的Float/Double类型的计算误差，能显著提高性能。但饼图类慎用。
 *			
 *  禁用图表区的平移     
 *  	chart.disablePanMode()
 *  
 *   平移默认是打开的。如果出现图或标识在绘图区外显示不全，可以禁掉此模式即可。
 *  
 *  
 *  如果要展示的图表数据比较长或多，可以通过调整绘图区宽度chart.getPlotArea().extWidth(增加宽度);
 *  或整个图的大小，即chart.setChartRange()的值。
 *  然后用户可以通过平移图表区的方式来展示未显示出来的数据. 
 *  注意，此方式性能会有些损失，超大量的就不用尝试这种方式了， 
 *  对于这种超大量的可以通过ScrollView控件方式来处理,具体可参考"左右滑动折线图"的例子。
 *
 *  
 * @author XCL
 *
 */
public class ChartsActivity extends Activity {
    public static void actionStart(Context context) {
        Intent intent = new Intent(context,ChartsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

	private mZoomControls myZoomControls;
	private int mSelected = 0;
    private DemoView[] mCharts ;

    public static int c1[] = new int[30];
    public static int c2[] = new int[30];
    public static int c3[] = new int[30];
    public static int c4[] = new int[30];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Log.d(TAG, "*************ChartActivity:  start*************");
        //设置铺满屏幕
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置没标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//设置横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		mCharts = new DemoView[]{
		        new LineChart01View(this),  //折线图(封闭式)
                new SplineChart03View(this)//平滑曲线图
        };

        String title = null;
        try {
            Bundle bundle = this.getIntent().getExtras();
            mSelected = bundle.getInt("selected");
            title = bundle.getString("title");
            c1 = bundle.getIntArray("c1");
            c2 = bundle.getIntArray("c2");
            c3 = bundle.getIntArray("c3");
            c4 = bundle.getIntArray("c4");
    } catch (NullPointerException e) {e.printStackTrace();}

        if(mSelected > mCharts.length - 1){
			setContentView(R.layout.activity_charts);
			this.setTitle(Integer.toString(mSelected));
		}else{
	        initActivity();
			this.setTitle(title);
		}
	}
	
	private void initActivity()
	{
			//完全动态创建,无须XML文件.
	       FrameLayout framelayout = new FrameLayout(this);
	       //缩放控件放置在FrameLayout的上层，用于放大缩小图表
		   FrameLayout.LayoutParams frameParm = new FrameLayout.LayoutParams(
		   LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  //LayoutParams(width,height)
		   frameParm.gravity = Gravity.BOTTOM|Gravity.RIGHT;  

		  //缩放控件放置在FrameLayout的上层，用于放大缩小图表
	       myZoomControls = new mZoomControls(this);
	       myZoomControls.setIsZoomInEnabled(true);
	       myZoomControls.setIsZoomOutEnabled(true);
		   myZoomControls.setLayoutParams(frameParm);
		   
		   //图表显示范围在占屏幕大小的99%的区域内
		   DisplayMetrics dm = getResources().getDisplayMetrics();		   
		   int scrWidth = (int) (dm.widthPixels * 0.99);
		   int scrHeight = (int) (dm.heightPixels * 0.99);
	       RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(scrWidth, scrHeight);
	       
	       //居中显示
           layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);   
           //图表view放入布局中，也可直接将图表view放入Activity对应的xml文件中
           final RelativeLayout chartLayout = new RelativeLayout(this);  
      
           chartLayout.addView( mCharts[mSelected], layoutParams);
  
	        //增加控件
         framelayout.addView(chartLayout);
		   ((ViewGroup) framelayout).addView(myZoomControls);
		    setContentView(framelayout);
		    //放大监听
		   myZoomControls.setOnZoomInClickListener(new OnZoomInClickListenerImpl());
		    //缩小监听
		    myZoomControls.setOnZoomOutClickListener(new OnZoomOutClickListenerImpl());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, Menu.FIRST + 1, 0, "帮助");
        menu.add(Menu.NONE, Menu.FIRST + 2, 0, "返回");
		return true;
	}

	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        super.onOptionsItemSelected(item);
	        switch(item.getItemId())
	        {
	        case Menu.FIRST+1: 
	        	//String chartsHelp[] = getResources().getStringArray(R.array.chartsHelp);	        
	        	//String URL = chartsHelp[mSelected]; 	        	
/*	        	String URL =getResources().getString(R.string.hello_world);
		        Uri uri = Uri.parse(URL);  
		        Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);  
		        startActivity(intent2);  
		        finish();*/
                Toast.makeText(this,"帮助",Toast.LENGTH_SHORT).show();
	            break;
	        case Menu.FIRST+2:
		        //Intent intent = new Intent();
	    		//intent.setClass(ChartsActivity.this,testActivity.class);
	    		//startActivity(intent);
                Toast.makeText(this,"返回",Toast.LENGTH_SHORT).show();
	            break;
	        }
	        return true;
	    }

	 private class OnZoomInClickListenerImpl implements View.OnClickListener {
	        @Override
	        public void onClick(View view) {	        	
	        	//mCharts[mSelected].zoomIn();
                Toast.makeText(ChartsActivity.this,"努力缩小中",Toast.LENGTH_SHORT).show();
	        }
	    }

	    private class OnZoomOutClickListenerImpl implements View.OnClickListener {
	        @Override
	        public void onClick(View view) {
	        	//mCharts[mSelected].zoomOut();
                //Toast.makeText(ChartsActivity.this,"努力放大中",Toast.LENGTH_SHORT).show();
            testActivity.actionStart(ChartsActivity.this);
	        }
	    }
}
