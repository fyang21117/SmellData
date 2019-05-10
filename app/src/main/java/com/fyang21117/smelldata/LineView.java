package com.fyang21117.smelldata;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import java.util.LinkedList;
import static com.fyang21117.smelldata.MainActivity.c1;
import static com.fyang21117.smelldata.MainActivity.c2;
import static com.fyang21117.smelldata.MainActivity.c3;
import static com.fyang21117.smelldata.MainActivity.c4;
import static com.fyang21117.smelldata.MainActivity.max;

public class LineView extends View {
    private String TAG = "lineview";
    // X轴的间距
    private float  xInstance;

    // 起始和结束的坐标点
    private int mStartX, mStartY, mEndX, mEndY;

    // 画笔
    private Paint mL0Paint;//专门画直线
    private Paint mL1Paint;
    private Paint mL2Paint;
    private Paint mL3Paint;

    private Paint mTextPaint;//文字的画笔
    private Paint mXYPaint;//xy坐标轴的画笔
    private Paint mNumPaint;//格子里面的画笔

    // View 宽高
     int mViewWidth;
     int mViewHeight;

    //左右的间距
     int marginLeft, marginRight;

    //坐标轴的X,Y总长度
    private int XLong, YLong;

    //坐标轴两个点之间的间距
    private int mDistance;

    //直线path的起始点
     int mLineStartX;

    LinkedList<Double> LineData0 = new LinkedList<>();
    LinkedList<Double> LineData1 = new LinkedList<>();
    LinkedList<Double> LineData2 = new LinkedList<>();
    LinkedList<Double> LineData3 = new LinkedList<>();

    //直线的path
    Path linePath0;
    Path linePath1;
    Path linePath2;
    Path linePath3;

    //直线的动画
    private ValueAnimator lineAnimator;

    // 动画数值(用于控制动画状态)
    private float mAnimatorValue = 0;

    // 动效过程监听器
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private Animator.AnimatorListener            mAnimatorListener;


    // 测量Path 并截取部分的工具
    PathMeasure mMeasure0;
    PathMeasure mMeasure1;
    PathMeasure mMeasure2;
    PathMeasure mMeasure3;

    // 默认的动效周期 10s
     final int DEFAULTDURATION = 10000;

    //下标日期的列表
    private LinkedList<Integer> mDates = new LinkedList<>();

    private boolean doAnimation = false;

    private Bitmap mBitmap;

    private Matrix mMatrix;//图片的矩阵

    private float[] pos;                // 当前点的实际位置
    private float[] tan;                // 当前点的tangent值,用于计算图片所需旋转的角度

    private static int MAX=0;//最后的数字
    private ProgressListener mListener;   //获取变化率的接口

    LinkedList<Double> arrInt0 = new LinkedList<>();
    LinkedList<Double> arrInt1 = new LinkedList<>();
    LinkedList<Double> arrInt2 = new LinkedList<>();
    LinkedList<Double> arrInt3 = new LinkedList<>();

    public void setProgressListener(ProgressListener listener) {
        mListener = listener;
    }

    public LineView(Context context) {
        super(context);
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.e(TAG, "LineView start*******");
        marginLeft = UiUtils.dipToPx(context, 10);
        marginRight = UiUtils.dipToPx(context, 7);

        init();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;       // 缩放图片
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_roundadd,
                options);
        mMatrix = new Matrix();
    }

    private void init() {
        initPaint();
        initListener();
        initAnimator();
        initPath();

        //下标
        int temp = 0;
        for (int i = 0; i < c1.length; i++) {
            mDates.add(temp);
            temp += 1;
        }

        pos = new float[2];
        tan = new float[2];
          //lineAnimator.start();

    }

    //初始化画笔
    private void initPaint() {
        mL0Paint = new Paint();
        mL0Paint.setColor(Color.parseColor("#f3630a"));
        mL0Paint.setAntiAlias(true);
        mL0Paint.setStyle(Paint.Style.STROKE);
        mL0Paint.setStrokeWidth(4f);

        mL1Paint = new Paint();
        mL1Paint.setColor(Color.BLACK);
        mL1Paint.setAntiAlias(true);
        mL1Paint.setStyle(Paint.Style.STROKE);
        mL1Paint.setStrokeWidth(4f);

        mL2Paint = new Paint();
        mL2Paint.setColor(Color.GREEN);
        mL2Paint.setAntiAlias(true);
        mL2Paint.setStyle(Paint.Style.STROKE);
        mL2Paint.setStrokeWidth(4f);

        mL3Paint = new Paint();
        mL3Paint.setColor(Color.BLUE);
        mL3Paint.setAntiAlias(true);
        mL3Paint.setStyle(Paint.Style.STROKE);
        mL3Paint.setStrokeWidth(4f);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(25);

        mXYPaint = new Paint();
        mXYPaint.setColor(Color.GRAY);
        mXYPaint.setStyle(Paint.Style.FILL);
        mXYPaint.setStrokeWidth(2f);

        mNumPaint = new Paint();
        mNumPaint.setColor(Color.BLACK);
        mNumPaint.setStyle(Paint.Style.STROKE);
        mNumPaint.setTextAlign(Paint.Align.CENTER);
        mNumPaint.setAntiAlias(true);
    }

    private void initListener() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();//动画中每一帧更新的时候调用
                if (mListener != null) {
                    mListener.onProgressValueChange(mAnimatorValue);
                }
                mMatrix.reset();
                invalidate();
            }
        };

        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                //doAnimation = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        };
    }

    private void initAnimator() {
        lineAnimator = ValueAnimator.ofFloat(0, 1).setDuration(DEFAULTDURATION);//6000
        lineAnimator.setInterpolator(new LinearInterpolator());//速率匀速
        lineAnimator.addUpdateListener(mUpdateListener);
        lineAnimator.addListener(mAnimatorListener);
    }

    private void initPath() {
        linePath0 = new Path();
        linePath1 = new Path();
        linePath2 = new Path();
        linePath3 = new Path();

        mMeasure0 = new PathMeasure();
        mMeasure1 = new PathMeasure();
        mMeasure2 = new PathMeasure();
        mMeasure3 = new PathMeasure();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);//当屏幕大小改变时绘制.
        Log.e(TAG, "onSizeChanged() start*******");

        mViewHeight = h;
        mViewWidth = w;
        marginLeft=mViewWidth/20;

        //XLong = mViewWidth;
        YLong = mViewHeight / 10 * 9;//Y大概占总长度的三分之一
        XLong = mViewWidth-marginLeft-marginRight;
        //YLong = mViewHeight/3*2;//Y大概占总长度的三分之一
        mDistance = XLong / 30;

        //linePath0.moveTo(marginLeft,mViewHeight-YLong/6);

        for (int i = 0; i < 30; i++) {
            arrInt0.add((double) c1[i]);
            arrInt1.add((double) c2[i]);
            arrInt2.add((double) c3[i]);
            arrInt3.add((double) c4[i]);
        }
        //动画起始入口
        addAndFormat(arrInt0, arrInt1, arrInt2, arrInt3);
    }

    //把输入进来的值标准化为Y坐标
    public void addAndFormat(LinkedList<Double> data0, LinkedList<Double> data1,
                             LinkedList<Double> data2, LinkedList<Double> data3) {
         MAX=max;
        //max0 = Collections.max(data0);
        //lastNum = data0.get(data0.size() - 1);
        for (int i = 0; i < data0.size(); i++) {
            if (data0.get(i) == 0) {
                LineData0.add((double) (mViewHeight - YLong / 6));
            } else {
                LineData0.add(mViewHeight - data0.get(i) * YLong / MAX / 6 * 5 - (double)(YLong / 6));
            }

            if (data1.get(i) == 0) {
                LineData1.add((double)( mViewHeight - YLong / 6));
            } else {
                LineData1.add(mViewHeight - data1.get(i) * YLong / MAX / 6 * 5 - (double)(YLong / 6));
            }

            if (data2.get(i) == 0) {
                LineData2.add((double) mViewHeight - YLong / 6);
            } else {
                LineData2.add(mViewHeight - data2.get(i) * YLong / MAX/ 6 * 5 - (double)(YLong / 6));
            }

            if (data3.get(i) == 0) {
                LineData3.add((double) mViewHeight - YLong / 6);
            } else {
                LineData3.add(mViewHeight - data3.get(i) * YLong / MAX/ 6 * 5 - (double)(YLong / 6));
            }
        }
        defLine();
        doAnimation = true;
        lineAnimator.start();
    }

    //这里确定path的路径
    private void defLine() {
        int startX = marginLeft;
        int tempDistance = 0;
        for (int i = 0; i < LineData0.size(); i++) {
            if (i == 0) {
                linePath0.moveTo( marginLeft, LineData0.get(0).floatValue());
                linePath1.moveTo(marginLeft, LineData1.get(0).floatValue());
                linePath2.moveTo(marginLeft, LineData2.get(0).floatValue());
                linePath3.moveTo(marginLeft, LineData3.get(0).floatValue());
            } else {
                linePath0.lineTo(startX + tempDistance, LineData0.get(i).floatValue());
                linePath1.lineTo(startX + tempDistance, LineData1.get(i).floatValue());
                linePath2.lineTo(startX + tempDistance, LineData2.get(i).floatValue());
                linePath3.lineTo(startX + tempDistance, LineData3.get(i).floatValue());
            }
            tempDistance += mDistance;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawNet(canvas);
        if (doAnimation) {
            drawLinePath0(canvas);
            drawLinePath1(canvas);
            drawLinePath2(canvas);
            drawLinePath3(canvas);
        }
        //canvas.drawPath(linePath0,mL0Paint);
    }

    private void drawNet(Canvas canvas) {
        //横坐标X
        int startX = marginLeft;
        canvas.drawLine(startX, (float)(mViewHeight - YLong / 30), startX + XLong,
                (float)(mViewHeight - YLong / 30), mXYPaint);
        int distanceTemp = 0;
        int textDistanceTemp = startX;
        int coltxtDistance = 0;

        for (int i = 0; i < MAX/10; i++) {
            canvas.drawText( i+ "", 0, coltxtDistance, mTextPaint);
            coltxtDistance += 10;
        }

        //纵坐标Y 坐标轴横坐标的起始点为marginLeft，纵坐标为marginLeft，暂时选取这两个值，横坐标的终值为marginLeft+XLong
        for (int i = 0; i < 30; i++) {
            canvas.drawLine(startX + distanceTemp, (float)(mViewHeight - YLong / 30),
                    startX + distanceTemp, mViewHeight - YLong, mXYPaint);
            distanceTemp += mDistance;
        }
        //把直线起始点定位为坐标轴的原点
        mLineStartX = marginLeft;

        for (int i = 0; i < mDates.size(); i++) {
            canvas.drawText(mDates.get(i) + "", textDistanceTemp, mViewHeight, mTextPaint);
            textDistanceTemp += mDistance;
        }


    }

    private void drawLinePath0(Canvas canvas) {
        mMeasure0.setPath(linePath0, false);
        mMeasure0.getPosTan(mMeasure0.getLength() * mAnimatorValue, pos, tan);
        mMatrix.postTranslate(pos[0] - (float) mBitmap.getWidth() / 2,
                pos[1] - mBitmap.getHeight());
        mNumPaint.setTextSize(25);

        Path dst = new Path();
        Path dst2 = new Path();
        //从0画到1
        mMeasure0.getSegment(0, mMeasure0.getLength() * mAnimatorValue, dst, true);
        mMeasure0.getSegment(mMeasure0.getLength() * mAnimatorValue - (float) mBitmap.getWidth() / 2, mMeasure0.getLength() * mAnimatorValue + (float)(mBitmap.getWidth() / 2), dst2, true);

        canvas.drawPath(dst, mL0Paint);
        //canvas.drawBitmap(mBitmap, mMatrix, mL0Paint);
        canvas.drawText("甲醛", pos[0], pos[1] - (float) mBitmap.getHeight() / 2, mNumPaint);
    }

    private void drawLinePath1(Canvas canvas) {
        mMeasure1.setPath(linePath1, false);
        mMeasure1.getPosTan(mMeasure1.getLength() * mAnimatorValue, pos, tan);
        mMatrix.postTranslate(pos[0] - (float) mBitmap.getWidth() / 2,
                pos[1] - mBitmap.getHeight());
        mNumPaint.setTextSize(25);
        //mNumPaint.setTextSize((float)mBitmap.getWidth() / 3);
        Path dst = new Path();
        Path dst2 = new Path();
        mMeasure1.getSegment(0, mMeasure1.getLength() * mAnimatorValue, dst, true);
        mMeasure1.getSegment(mMeasure1.getLength() * mAnimatorValue - (float) mBitmap.getWidth() / 2, mMeasure1.getLength() * mAnimatorValue +(float)( mBitmap.getWidth() / 2), dst2, true);
        canvas.drawPath(dst, mL1Paint);
        //canvas.drawBitmap(mBitmap, mMatrix, mL1Paint);
        canvas.drawText("CO", pos[0], pos[1] - (float) mBitmap.getHeight() / 2, mNumPaint);
    }

    private void drawLinePath2(Canvas canvas) {
        mMeasure2.setPath(linePath2, false);
        mMeasure2.getPosTan(mMeasure2.getLength() * mAnimatorValue, pos, tan);
        mMatrix.postTranslate(pos[0] - (float) mBitmap.getWidth() / 2,
                pos[1] - mBitmap.getHeight());
        mNumPaint.setTextSize(25);
        Path dst = new Path();
        Path dst2 = new Path();
        mMeasure2.getSegment(0, mMeasure2.getLength() * mAnimatorValue, dst, true);
        mMeasure2.getSegment(mMeasure2.getLength() * mAnimatorValue - (float) mBitmap.getWidth() / 2, mMeasure2.getLength() * mAnimatorValue + (float)(mBitmap.getWidth() / 2), dst2, true);
        canvas.drawPath(dst, mL2Paint);
        //canvas.drawBitmap(mBitmap, mMatrix, mL2Paint);
        canvas.drawText("2603", pos[0], pos[1] - (float) mBitmap.getHeight() / 2, mNumPaint);
    }

    private void drawLinePath3(Canvas canvas) {
        mMeasure3.setPath(linePath3, false);
        mMeasure3.getPosTan(mMeasure3.getLength() * mAnimatorValue, pos, tan);
        mMatrix.postTranslate(pos[0] - (float) mBitmap.getWidth() / 2,
                pos[1] - mBitmap.getHeight());
        mNumPaint.setTextSize(25);
        Path dst = new Path();
        Path dst2 = new Path();
        mMeasure3.getSegment(0, mMeasure3.getLength() * mAnimatorValue, dst, true);
        mMeasure3.getSegment(mMeasure3.getLength() * mAnimatorValue - (float) mBitmap.getWidth() / 2, mMeasure3.getLength() * mAnimatorValue + (float)(mBitmap.getWidth() / 2), dst2, true);
        canvas.drawPath(dst, mL3Paint);
        //canvas.drawBitmap(mBitmap, mMatrix, mL3Paint);
        canvas.drawText("MQ137", pos[0], pos[1] - (float) mBitmap.getHeight() / 2, mNumPaint);
    }

    //下面的日期
    public void addDate(LinkedList<Integer> dates) {
        mDates = dates;
        invalidate();
    }

}