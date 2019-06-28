package com.aar.tapholdupbutton;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class TapHoldUpButton extends View {

    private static final int DEF_STROKE_WIDTH = 20;
    private static final int DEF_RING_COLOR = Color.WHITE;
    private static final int DEF_CIRCLE_COLOR = Color.RED;
    private static final int DEF_CIRCLE_GAP = 10;

    private Paint mRingPaint;
    private Paint mCirclePaint;
    private float mCircleGap;

    public TapHoldUpButton(Context context) {
        super(context);
        init(context, null);
    }

    public TapHoldUpButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
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
                (halfWidth - mRingPaint.getStrokeWidth() - mCircleGap) * perc,
                mCirclePaint
        );
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

            arr.recycle();
        } else {
            mRingPaint.setStrokeWidth(DEF_STROKE_WIDTH);
            mRingPaint.setColor(DEF_RING_COLOR);
            mCirclePaint.setColor(DEF_CIRCLE_COLOR);
            mCircleGap = DEF_CIRCLE_GAP;
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final ValueAnimator anim = ValueAnimator.ofFloat(1f, .9f, 1f);
                anim.setDuration(300);
                anim.setInterpolator(new DecelerateInterpolator());
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        perc = (float) anim.getAnimatedValue();
                        invalidate();
                    }
                });
                anim.start();
            }
        });
    }

    float perc = 1f;
}
