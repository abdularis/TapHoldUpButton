package com.aar.tapholdupbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class TapHoldUpButton extends View {

    private static final int DEF_STROKE_WIDTH = 20;
    private static final int DEF_RING_COLOR = Color.WHITE;
    private static final int DEF_CIRCLE_COLOR = Color.RED;
    private static final int DEF_CIRCLE_GAP = 10;

    private static final int LONG_HOLD_DELAY = 800;
    private static final int ANIM_DURATION = 150;

    private Handler mHandler = new Handler();

    private Paint mRingPaint;
    private Paint mCirclePaint;
    private float mCircleGap;
    private int mCircleColor;
    private int mCircleColorOnHold;

    private boolean longHoldEnabled = true;
    private float mScalePercentage = 1f;
    private boolean longHold = false;
    private int touchState;
    private ValueAnimator downAnim;
    private ValueAnimator upAnim;
    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

    private OnButtonClickListener mClickListener;

    public interface OnButtonClickListener {
        void onLongHoldStart(View v);
        void onLongHoldEnd(View v);
        void onClick(View v);
    }

    public TapHoldUpButton(Context context) {
        super(context);
        init(context, null);
    }

    public TapHoldUpButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        mClickListener = listener;
    }

    public void enableLongHold(boolean enable) {
        longHoldEnabled = enable;
    }

    public void resetLongHold() {
        if (!longHold)
            return;

        touchState = -1;
        startUpAnimation();
        endLongHold();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float halfWidth = getWidth() / 2f;
        float halfHeight = getHeight() / 2f;
        canvas.drawCircle(
                halfWidth,
                halfHeight,
                halfWidth - mRingPaint.getStrokeWidth() / 2f,
                mRingPaint
        );

        canvas.drawCircle(
                halfWidth,
                halfHeight,
                (halfWidth - mRingPaint.getStrokeWidth() - mCircleGap) * mScalePercentage,
                mCirclePaint
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isInCircle(event.getX(), event.getY())) {
                touchState = MotionEvent.ACTION_DOWN;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!longHoldEnabled)
                            return;
                        if (touchState == MotionEvent.ACTION_DOWN) {
                            longHold = true;
                            startColorChangeAnimation(mCircleColor, mCircleColorOnHold);
                            if (mClickListener != null)
                                mClickListener.onLongHoldStart(TapHoldUpButton.this);
                        }
                    }
                }, LONG_HOLD_DELAY);

                startDownAnimation();
            }
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP &&
                touchState == MotionEvent.ACTION_DOWN) {
            touchState = MotionEvent.ACTION_UP;
            startUpAnimation();
            mHandler.removeCallbacksAndMessages(null);
            if (longHold) {
                endLongHold();
            } else {
                if (mClickListener != null)
                    mClickListener.onClick(TapHoldUpButton.this);
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    private void endLongHold() {
        longHold = false;
        startColorChangeAnimation(mCircleColorOnHold, mCircleColor);
        if (mClickListener != null)
            mClickListener.onLongHoldEnd(TapHoldUpButton.this);
    }

    private void startColorChangeAnimation(final int startColor, final int endColor) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(150);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int col = (int) mArgbEvaluator.evaluate((float) animation.getAnimatedValue(), startColor, endColor);
                mCirclePaint.setColor(col);
                invalidate();
            }
        });
        anim.start();
    }

    private void startDownAnimation() {
        if (downAnim != null)
            return;

        downAnim = ValueAnimator.ofFloat(1f, .8f);
        downAnim.setDuration(ANIM_DURATION);
        downAnim.setInterpolator(new DecelerateInterpolator());
        downAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScalePercentage = (float) downAnim.getAnimatedValue();
                invalidate();
            }
        });
        downAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                downAnim = null;
                if (touchState == MotionEvent.ACTION_UP) {
                    startUpAnimation();
                }
            }
        });
        downAnim.start();
    }

    private void startUpAnimation() {
        if (upAnim != null || downAnim != null)
            return;

        upAnim = ValueAnimator.ofFloat(.8f, 1f);
        upAnim.setDuration(ANIM_DURATION);
        upAnim.setInterpolator(new DecelerateInterpolator());
        upAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScalePercentage = (float) upAnim.getAnimatedValue();
                invalidate();
            }
        });
        upAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                upAnim = null;
            }
        });
        upAnim.start();
    }

    private boolean isInCircle(float x, float y) {
        // find the distance between center of the view and x,y point
        double distance = Math.sqrt(
                Math.pow((getWidth() / 2f) - x, 2) + Math.pow((getHeight() / 2f) - y, 2)
        );
        return distance <= (getWidth() / 2);
    }

    private void init(Context context, AttributeSet attrs) {
        mRingPaint = new Paint();
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.TapHoldUpButton, 0, 0);

            mRingPaint.setStrokeWidth(arr.getDimensionPixelSize(R.styleable.TapHoldUpButton_thub_ringStrokeWidth, DEF_STROKE_WIDTH));
            mRingPaint.setColor(arr.getColor(R.styleable.TapHoldUpButton_thub_ringColor, DEF_RING_COLOR));
            mCirclePaint.setColor(arr.getColor(R.styleable.TapHoldUpButton_thub_circleColor, DEF_CIRCLE_COLOR));
            mCircleGap = arr.getDimensionPixelSize(R.styleable.TapHoldUpButton_thub_circleGap, DEF_CIRCLE_GAP);
            mCircleColor = mCirclePaint.getColor();
            mCircleColorOnHold = arr.getColor(R.styleable.TapHoldUpButton_thub_circleColorOhHold, Color.RED);

            arr.recycle();
        } else {
            mRingPaint.setStrokeWidth(DEF_STROKE_WIDTH);
            mRingPaint.setColor(DEF_RING_COLOR);
            mCirclePaint.setColor(DEF_CIRCLE_COLOR);
            mCircleColor = DEF_CIRCLE_COLOR;
            mCircleGap = DEF_CIRCLE_GAP;
            mCircleColorOnHold = Color.RED;
        }
    }

}
