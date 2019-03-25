package com.fyang21117.smelldata.view.event;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ZoomButton;
import com.fyang21117.smelldata.R;

public class mZoomControls extends LinearLayout {
    private final ZoomButton mZoomIn;
    private final ZoomButton mZoomOut;

    public mZoomControls(Context context) {
        this(context, null);
    }

    public mZoomControls(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(false);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.zoom_controls, this, // we are the parent
                true);

        mZoomIn =  findViewById(R.id.zoomIn);
        mZoomOut = (ZoomButton) findViewById(R.id.zoomOut);
    }

    public void setOnZoomInClickListener(OnClickListener listener) {
        mZoomIn.setOnClickListener(listener);
    }

    public void setOnZoomOutClickListener(OnClickListener listener) {
        mZoomOut.setOnClickListener(listener);
    }

    /*
     * Sets how fast you get zoom events when the user holds down the
     * zoom in/out buttons.
     */
    public void setZoomSpeed(long speed) {
        mZoomIn.setZoomSpeed(speed);
        mZoomOut.setZoomSpeed(speed);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        /* Consume all touch events so they don't get dispatched to the view
         * beneath this view.
         */
        return true;
    }

    public void show() {
        fade(View.VISIBLE, 0.0f, 1.0f);
    }

    public void hide() {
        fade(View.GONE, 1.0f, 0.0f);
    }

    private void fade(int visibility, float startAlpha, float endAlpha) {
        AlphaAnimation anim = new AlphaAnimation(startAlpha, endAlpha);
        anim.setDuration(500);
        startAnimation(anim);
        setVisibility(visibility);
    }

    public void setIsZoomInEnabled(boolean isEnabled) {
        mZoomIn.setEnabled(isEnabled);
    }

    public void setIsZoomOutEnabled(boolean isEnabled) {
        mZoomOut.setEnabled(isEnabled);
    }

    @Override
    public boolean hasFocus() {
        return mZoomIn.hasFocus() || mZoomOut.hasFocus();
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return android.widget.ZoomControls.class.getName();
    }
}
