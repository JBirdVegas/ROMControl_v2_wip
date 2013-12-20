package com.aokp.romcontrol.widgets;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

public class SwipeLinearLayout extends LinearLayout implements View.OnTouchListener {
    private GestureDetector mGestureDetector;
    private OnSwipeListener mSwipeListener;
    private int mLastMotionX;
    private int mLastMotionY;
    private PointF StartPT;
    private PointF DownPT;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int eid = event.getAction();
        switch (eid) {
            case MotionEvent.ACTION_MOVE:
                if (DownPT == null) {
//                    DownPT = new PointF(event.getX(), event.getY());
                    DownPT = new PointF(getX(), getY());
                }
                PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                StartPT = new PointF(getX(), getY());
                setX((int) (StartPT.x + mv.x));
//                setY((int) (StartPT.y + mv.y));
                break;
            case MotionEvent.ACTION_DOWN:
                DownPT.x = event.getX();
//                DownPT.y = event.getY();
                StartPT = new PointF(getX(), getY());
                break;
            case MotionEvent.ACTION_UP:
                // Nothing have to do
                break;
            default:
                break;
        }
        return true;
    }

    public interface OnSwipeListener {
        void onSwipeLeft();
    }

    public SwipeLinearLayout(Context context) {
        this(context, null);
    }

    public SwipeLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLinearLayout(Context context, AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), new LeftSwipeListener());
        setOnTouchListener(this);
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.mSwipeListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mLastMotionX = (int) event.getX();
            mLastMotionY = (int) event.getY();
        }

        final int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int diffX = Math.abs(x - mLastMotionX);
        final int diffY = Math.abs(y - mLastMotionY);
        boolean isSwipingSideways = diffX > scaledTouchSlop && diffX > diffY;

        // Start sending all events to our onTouchEvent from this point
        if (action == MotionEvent.ACTION_MOVE && isSwipingSideways) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private class LeftSwipeListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 1200; // Toy with this value to adjust how hard you have to swipe

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // right to left swipe
            if (mLastMotionX - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (mSwipeListener != null) {
                    mSwipeListener.onSwipeLeft();
                    return true;
                }
            }

            return false;
        }
    }
}