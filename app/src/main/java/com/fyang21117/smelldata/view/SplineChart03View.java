package com.fyang21117.smelldata.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.fyang21117.smelldata.view.chart.CustomLineData;
import com.fyang21117.smelldata.view.chart.PointD;
import com.fyang21117.smelldata.view.chart.SplineChart;
import com.fyang21117.smelldata.view.chart.SplineData;
import com.fyang21117.smelldata.view.common.IFormatterTextCallBack;
import com.fyang21117.smelldata.view.event.click.PointPosition;
import com.fyang21117.smelldata.view.renderer.XEnum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.fyang21117.smelldata.MainActivity.max;
import static com.fyang21117.smelldata.MainActivity.c1;
import static com.fyang21117.smelldata.MainActivity.c2;
import static com.fyang21117.smelldata.MainActivity.c3;
import static com.fyang21117.smelldata.MainActivity.c4;

public class SplineChart03View extends DemoView {

    //平滑曲线图
    private String      TAG   = "SplineChart03View";
    private SplineChart chart = new SplineChart();

    //分类轴标签集合
    private       LinkedList<String>     labels         = new LinkedList<>();
    private       LinkedList<SplineData> chartData      = new LinkedList<>();
    public static int                    SIZE           = 30;
    private       Paint                  mPaintTooltips = new Paint(Paint.ANTI_ALIAS_FLAG);

    //setCategoryAxisCustomLines
    // splinechart支持横向和竖向定制线
    private List<CustomLineData> mXCustomLineDataset = new ArrayList<>();
    private List<CustomLineData> mYCustomLineDataset = new ArrayList<>();

    public SplineChart03View(Context context) {
        super(context);
        initView();
    }

    public SplineChart03View(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SplineChart03View(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        chartLabels();
        //chartCustomeLines();
        chartDataSet();
        chartRender();
        this.bindTouch(this, chart);//綁定手势滑动事件
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        chart.setChartRange(w, h);//图所占范围大小
    }

    private void chartRender() {
        try {
            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int[] ltrb = getBarLnDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);
            chart.showRoundBorder();                            //显示边框
            chart.setCategories(labels);                        //标签源
            chart.setDataSource(chartData);                     //数据源

            //坐标系
            chart.getDataAxis().setAxisMax(max);                //数据轴最大值
            chart.getDataAxis().setAxisMin(0);
            chart.getDataAxis().setAxisSteps(max / 10);         //数据轴刻度间隔
            chart.setCustomLines(mYCustomLineDataset);          //y轴
            //chart.setCustomLines(mXCustomLineDataset);        //x轴
            chart.setCategoryAxisMax(100);                      //标签轴最大值
            //chart.getCategoryAxis().setAxisSteps(10);         //标签轴刻度间隔
            chart.setCategoryAxisMin(0);                        //标签轴最小值
            chart.setCategoryAxisCustomLines(mXCustomLineDataset); //x轴

            //设置图的背景色
            chart.setApplyBackgroundColor(true);
            chart.setBackgroundColor(Color.rgb(212, 194, 129));
            chart.getBorder().setBorderLineColor(Color.rgb(179, 147, 197));

            //调轴线与网络线风格
            chart.getCategoryAxis().hideTickMarks();
            chart.getDataAxis().hideAxisLine();
            chart.getDataAxis().hideTickMarks();
            chart.getPlotGrid().showHorizontalLines();
            //chart.hideTopAxis();
            //chart.hideRightAxis();

            chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.rgb(179, 147, 197));
            chart.getCategoryAxis().getAxisPaint().setColor(chart.getPlotGrid().getHorizontalLinePaint().getColor());
            chart.getCategoryAxis().getAxisPaint().setStrokeWidth(chart.getPlotGrid().getHorizontalLinePaint().getStrokeWidth());

            //定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为:  x值,y值
            //请自行分析定制
            chart.setDotLabelFormatter(new IFormatterTextCallBack() {
                @Override
                public String textFormatter(String value) {
                    String label = "[" + value + "]";
                    return (label);
                }
            });

            chart.setTitle("气体传感器(Gas Sensors)");               //标题
            chart.addSubtitle("(2019.04.14)");
            chart.ActiveListenItemClick();                          //激活点击监听
            chart.extPointClickRange(5);//为了让触发更灵敏，可以扩大5px的点击监听范围
            chart.showClikedFocus();
            chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEZIERCURVE);//显示平滑曲线

            //图例显示在正下方
            chart.getPlotLegend().setVerticalAlign(XEnum.VerticalAlign.BOTTOM);
            chart.getPlotLegend().setHorizontalAlign(XEnum.HorizontalAlign.CENTER);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chartDataSet() {
        //甲醛的数据集
        List<PointD> linePoint1 = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            linePoint1.add(new PointD(i * 4d, (double) c1[i]));
        SplineData dataSeries1 = new SplineData("甲醛", linePoint1, Color.rgb(54, 141, 238));//key,
        // data,color
        dataSeries1.getLinePaint().setStrokeWidth(5);//线条粗细
        dataSeries1.setDotStyle(XEnum.DotStyle.RECT);//◇菱形
        //dataSeries1.setLabelVisible(true);

        //MQ137的数据集
        List<PointD> linePoint2 = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            linePoint2.add(new PointD(i * 4d, (double) c2[i]));
        SplineData dataSeries2 = new SplineData("MQ137", linePoint2, Color.rgb(255, 165, 132));
        dataSeries2.getLinePaint().setStrokeWidth(5);
        dataSeries2.setDotStyle(XEnum.DotStyle.RING);
        dataSeries2.getDotLabelPaint().setColor(Color.BLACK);

        //TGS2603的数据集
        List<PointD> linePoint3 = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            linePoint3.add(new PointD(i * 4d, (double) c3[i]));
        SplineData dataSeries3 = new SplineData("TGS2603", linePoint3, Color.rgb(84, 206, 231));
        dataSeries3.getLinePaint().setStrokeWidth(5);
        dataSeries3.setDotStyle(XEnum.DotStyle.TRIANGLE);
        dataSeries3.getDotPaint().setColor(Color.rgb(75, 166, 51));
        dataSeries3.getPlotLine().getPlotDot().setRingInnerColor(Color.rgb(123, 89, 168));

        //CO的数据集
        List<PointD> linePoint4 = new ArrayList<>();
        for (int i = 0; i < 30; i++)
            linePoint4.add(new PointD(i * 4d, (double) c4[i]));
        SplineData dataSeries4 = new SplineData("CO", linePoint4, Color.rgb(55, 165, 132));
        dataSeries4.getLinePaint().setStrokeWidth(5);
        dataSeries4.setDotStyle(XEnum.DotStyle.PRISMATIC);
        dataSeries4.getDotPaint().setColor(Color.rgb(75, 66, 51));
        dataSeries4.getPlotLine().getPlotDot().setRingInnerColor(Color.rgb(23, 89, 68));

        //设定数据源
        chartData.add(dataSeries1);
        chartData.add(dataSeries2);
        chartData.add(dataSeries3);
        chartData.add(dataSeries4);
    }

    private void chartLabels() {
        for (int i = 0; i < SIZE; i++) {
            String str = String.valueOf(i);
            labels.add(str + "s");
        }
    }

    /*** 期望线/分界线*/
    private void chartCustomeLines() {
        CustomLineData cdx1 = new CustomLineData("稍好", 30d, Color.rgb(35, 172, 57), 5);
        CustomLineData cdx2 = new CustomLineData("舒适", 40d, Color.rgb(69, 181, 248), 5);
        cdx1.setLabelVerticalAlign(XEnum.VerticalAlign.MIDDLE);
        mXCustomLineDataset.add(cdx1);
        mXCustomLineDataset.add(cdx2);

        CustomLineData cdy1 = new CustomLineData("定制线", 45d, Color.rgb(69, 181, 248), 5);
        cdy1.setLabelHorizontalPostion(Align.CENTER);
        mYCustomLineDataset.add(cdy1);
    }

    @Override
    public void render(Canvas canvas) {
        try {
            chart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            triggerClick(event.getX(), event.getY());
        }
        return true;
    }

    //触发监听
    private void triggerClick(float x, float y) {
        if (!chart.getListenItemClickStatus())
            return;

        PointPosition record = chart.getPositionRecord(x, y);
        if (null == record)
            return;

        if (record.getDataID() >= chartData.size())
            return;
        SplineData lData = chartData.get(record.getDataID());
        List<PointD> linePoint = lData.getLineDataSet();
        int pos = record.getDataChildID();
        int i = 0;
        Iterator it = linePoint.iterator();
        while (it.hasNext()) {
            PointD entry = (PointD) it.next();
            if (pos == i) {
                Double xValue = entry.x;
                Double yValue = entry.y;

                float r = record.getRadius();
                chart.showFocusPointF(record.getPosition(), r + r * 0.8f);
                chart.getFocusPaint().setStyle(Style.FILL);
                chart.getFocusPaint().setStrokeWidth(3);
                if (record.getDataID() >= 2) {
                    chart.getFocusPaint().setColor(Color.BLUE);
                } else {
                    chart.getFocusPaint().setColor(Color.RED);
                }
                //在点击处显示tooltip
                mPaintTooltips.setColor(Color.RED);
                chart.getToolTip().setCurrentXY(x, y);
                chart.getToolTip().addToolTip(" Key:" + lData.getLineKey(), mPaintTooltips);
                chart.getToolTip().addToolTip(
                        " Current Value:" + Double.toString(xValue) + "," + Double.toString(yValue), mPaintTooltips);
                chart.getToolTip().getBackgroundPaint().setAlpha(100);
                this.invalidate();

                break;
            }
            i++;
        }//end while
    }
}
