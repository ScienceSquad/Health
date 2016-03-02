package com.sciencesquad.health.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.sciencesquad.health.R;

import org.threeten.bp.Duration;

/** TODO
 * FIX POSITIONING OF THE TIME IN THE STOPWATCH
 */

public class ClockView extends View {
	private static final String TAG = ClockView.class.getSimpleName();

	private Paint mCirclePaint;
    private Paint mDotPaint;
    private Paint mTimePaint;
    private Paint mMsPaint;

    private Paint mHourPaint;
    private Paint mMinutePaint;
    private Paint mSecondPaint;
    private Paint mMilliPaint;

    private final int DOT_RADIUS = 20;
    private final int PADDING = this.DOT_RADIUS;

    private int mBackgroundColor;
    private int mTextColor;

    private float mTextHeight;
    private double mMsTextHeight = 0.75;

	/**
	 * Lengths expressed as fractions of the watch radius:
     *  1 = full length
     *  0.5 = half length
     */
    private double mHourHandLen = 0.75;
    private double mMinuteHandLen = 1;
    private double mSecondHandLen = 1;
    private double mMilliHandLen = 0.5;

	/**
	 * Widths in pixels of the hands of the watch
     */
    private float mHourHandWidth = 5;
    private float mMinuteHandWidth = 5;
    private float mSecondHandWidth = 3;
    private float mMilliHandWidth = 2;

	/**
	 * Colors of the hands of the watch
     */
    private int mHourColor;
    private int mMinuteColor;
    private int mSecondColor;
    private int mMilliColor;

    private Stopwatch stopwatch;

    private GestureDetector mDetector;

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    class mListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private void makePaints() {
        mTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void setPaints() {
		if (mTimePaint != null) {
			mTimePaint.setColor(mTextColor);
			mTimePaint.setTextSize(mTextHeight);
		}

		if (mMsPaint != null) {
			mMsPaint.setColor(mTextColor);
			mMsPaint.setTextSize((float) (mTextHeight * mMsTextHeight));
		}

		if (mDotPaint != null) {
			mDotPaint.setStyle(Paint.Style.FILL);
			mDotPaint.setColor(mTextColor);
		}

		if (mCirclePaint != null) {
			mCirclePaint.setStyle(Paint.Style.STROKE);
			mCirclePaint.setColor(mTextColor);
		}
    }

    private void init(Context context, AttributeSet attrs) {
        mDetector = new GestureDetector(this.getContext(), new mListener());
		TypedArray a = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.ClockView,
				0, 0);

		try {
			mTextColor = a.getInteger(R.styleable.ClockView_textColor, 0);
			mBackgroundColor = a.getInteger(R.styleable.ClockView_backgroundColor, 0);
			mTextHeight = a.getDimension(R.styleable.ClockView_textHeight, 0);
		} finally {
			a.recycle();
		}
        this.makePaints();
        this.setPaints();

        this.startStopwatch();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
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

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        this.setPaints();
    }

    public void setBackgroundColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        this.setPaints();
    }

    public void setTextHeight(float textHeight) {
		Log.d(TAG, "Text height: " + String.valueOf(textHeight));
        this.mTextHeight = textHeight;
        this.setPaints();
    }

    private Runnable createRunnable(ClockView view) {
        return () -> view.postInvalidate();
    }

    private void startStopwatch() {
        if (this.stopwatch != null) return;
        this.stopwatch = new Stopwatch();
        this.stopwatch.setOnTimeChange(createRunnable(this));
        this.stopwatch.setMode(Stopwatch.WatchMode.UP);
        // this.stopwatch.plusMinutes(5);
        this.stopwatch.start();
    }

    private void drawDot(Canvas canvas, float radius, float angle) {
        canvas.drawCircle(this.PADDING + (radius * (1 + (float) Math.cos(angle))),
                this.PADDING + (radius * (1 - (float) Math.sin(angle))),
                this.DOT_RADIUS, this.mDotPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float radius = canvas.getWidth() / 2;
        float height = canvas.getHeight() / 2;
        if (height < radius) {
            radius = height;
        }
        radius -= this.PADDING;

        Duration duration = this.stopwatch.getDurationForMode();
        String timeText = this.stopwatch.getPrettyTime(duration, false);
        String milliText = this.stopwatch.getMilliString(duration);

        Rect bounds = new Rect();
        mTimePaint.getTextBounds(timeText, 0, timeText.length(), bounds);
        int textWidth = bounds.width();

        bounds = new Rect();
        mMsPaint.getTextBounds(milliText, 0, milliText.length(), bounds);
        int msTextWidth = bounds.width();
        int totalTextWidth = textWidth + msTextWidth;

        canvas.drawText(timeText, this.PADDING + radius - (totalTextWidth / 2),
                this.PADDING + radius + (mTextHeight / 2), mTimePaint);
        canvas.drawText(milliText, this.PADDING + radius - (totalTextWidth / 2) + textWidth,
                this.PADDING + radius + (mTextHeight / 2), mMsPaint);

        this.drawDot(canvas, radius, this.stopwatch.getSecondHandAngle());
        canvas.drawCircle(this.PADDING + radius, this.PADDING + radius, radius, this.mCirclePaint);
    }

}