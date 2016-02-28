package com.sciencesquad.health;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sciencesquad.health.ui.Stopwatch;

class ClockView extends View {

    private Paint mCirclePaint;
    private Paint mTextPaint;

    private int mBackgroundColor;
    private int mTextColor;

    private float mTextHeight;

    private Stopwatch stopwatch;

    private GestureDetector mDetector;

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ClockView,
                0, 0);
        try {
            mBackgroundColor = a.getInteger(R.styleable.ClockView_backgroundColor, 0);
            mTextColor = a.getInteger(R.styleable.ClockView_textColor, 0);
            mTextHeight = a.getDimension(R.styleable.ClockView_textHeight, 0);
        } finally {
            a.recycle();
        }

        class mListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        }
        mDetector = new GestureDetector(this.getContext(), new mListener());

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextHeight);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mBackgroundColor);

        this.startStopwatch();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                System.out.println("View pressed");
                if (this.stopwatch.isRunning()) {
                    this.stopwatch.pause();
                }
                else {
                    this.stopwatch.resume();
                }
                result = true;
            }
        }
        return result;
    }

    private Runnable createRunnable(ClockView view) {
        return new Runnable() {
            public void run() {
                view.postInvalidate();
            }
        };
    }

    private void startStopwatch() {
        if (this.stopwatch != null) return;
        this.stopwatch = new Stopwatch();
        this.stopwatch.setOnTimeChange(createRunnable(this));
        this.stopwatch.setMode("DOWN");
        this.stopwatch.plusMinutes(5);
        this.stopwatch.start();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(this.stopwatch.getPrettyElapsed(), 0, mTextHeight, mTextPaint);
        canvas.drawText(this.stopwatch.getPrettyRemaining(), 0, 2 * mTextHeight, mTextPaint);
    }

}