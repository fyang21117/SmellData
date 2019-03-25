package com.fyang21117.smelldata.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.fyang21117.smelldata.view.event.touch.ChartTouch;
import com.fyang21117.smelldata.view.renderer.XChart;
import java.util.ArrayList;
import java.util.List;

/*
 * @ClassName ChartView
 * @Description  含手势操作的XCL-Charts图表View基类
 */
public abstract class ChartView extends GraphicalView {

    //private ChartTouch mChartTouch[];
    private List<ChartTouch> mTouch = new ArrayList<>();

    public ChartView(Context context) {
        super(context);
    }

    public ChartView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public void render(Canvas canvas) {    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //touchChart(event);
        touchEvent(event);
        return true;
    }

    //用于手势操作来平移或放大缩小图表
    /*
     * 用于绑定需要手势滑动的图表
     * @param view  视图
     * @param chart 图表类
     */
    public void bindTouch(View view, XChart chart){
        mTouch.add(new ChartTouch(this,chart));
    }

    /*
     * 用于绑定需要手势滑动的图表，及指定可滑动范围
     * @param view  视图
     * @param chart	图表类
     * @param panRatio 需大于0
     */
    public void bindTouch(View view, XChart chart,float panRatio){
        mTouch.add(new ChartTouch(this,chart,panRatio));
    }

    /*
     * 清空绑定类
     */
    public void restTouchBind(){
        mTouch.clear();
    }

    /*
     * 触发手势操作
     * @param event
     * @return
     */
    private boolean touchEvent(MotionEvent event)
    {
        for(ChartTouch c : mTouch){
            c.handleTouch(event);
        }
        return true;
    }
}
