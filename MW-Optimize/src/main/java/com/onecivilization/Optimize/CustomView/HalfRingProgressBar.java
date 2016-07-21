package com.onecivilization.Optimize.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.onecivilization.Optimize.R;

/**
 * Created by CGZ on 2016/7/20.
 */
public class HalfRingProgressBar extends View {

    public static final int MAX_ANGLE = 266;
    Rect bounds;
    private String percentageText;
    private int percentageTextColor;
    private int percentageTextSize;
    private String progressText;
    private int progressTextColor;
    private int progressTextSize;
    private String titleText;
    private int titleTextColor;
    private int titleTextSize;
    private int unreachedColor;
    private int reachedColor;
    private int minusColor;
    private int exceededColor;
    private int falseColor;
    private int ringWidth;
    private int radius;
    private Paint paint;
    private RectF oval;
    private int progress;
    private int max;

    public HalfRingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HalfRingProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HalfRingProgressBar, defStyleAttr, 0);
        try {
            radius = typedArray.getDimensionPixelSize(R.styleable.HalfRingProgressBar_radius,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics()));
            percentageTextColor = typedArray.getColor(R.styleable.HalfRingProgressBar_percentageTextColor, Color.BLACK);
            percentageTextSize = typedArray.getDimensionPixelSize(R.styleable.HalfRingProgressBar_percentageTextSize, radius / 3);
            progressTextColor = typedArray.getColor(R.styleable.HalfRingProgressBar_progressTextColor, Color.GRAY);
            progressTextSize = typedArray.getDimensionPixelSize(R.styleable.HalfRingProgressBar_progressTextSize, radius / 7);
            titleText = typedArray.getString(R.styleable.HalfRingProgressBar_title_text);
            if (titleText == null) titleText = "PROGRESS";
            titleTextColor = typedArray.getColor(R.styleable.HalfRingProgressBar_title_text_color, Color.BLACK);
            titleTextSize = typedArray.getDimensionPixelSize(R.styleable.HalfRingProgressBar_title_text_size, radius / 4);
            unreachedColor = typedArray.getColor(R.styleable.HalfRingProgressBar_unreachedColor, Color.GRAY);
            reachedColor = typedArray.getColor(R.styleable.HalfRingProgressBar_reachedColor, 0xff25d713);
            minusColor = typedArray.getColor(R.styleable.HalfRingProgressBar_minusColor, Color.RED);
            exceededColor = typedArray.getColor(R.styleable.HalfRingProgressBar_exceededColor, 0xFFD9A729);
            falseColor = typedArray.getColor(R.styleable.HalfRingProgressBar_falseColor, 0xFF000000);
            ringWidth = typedArray.getDimensionPixelSize(R.styleable.HalfRingProgressBar_ringWidth, radius / 8);
            progress = typedArray.getInt(R.styleable.HalfRingProgressBar_progress, 0);
            max = typedArray.getInt(R.styleable.HalfRingProgressBar_max, 1);
        } finally {
            typedArray.recycle();
        }
        paint = new Paint();
        bounds = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width, height;
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        } else {
            width = getPaddingLeft() + 2 * radius + ringWidth + getPaddingRight();
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            height = getPaddingTop() + 2 * radius + ringWidth + getPaddingBottom();
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStrokeWidth(ringWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        //draw the unreached ring
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(unreachedColor);
        canvas.drawArc(oval, 137, MAX_ANGLE, false, paint);

        float percentage = (float) progress / max;
        percentageText = String.format("%.2f%%", percentage * 100);
        float reachedAngle = percentage * MAX_ANGLE;

        //draw the reached ring
        if (reachedAngle != 0) {
            if (reachedAngle > 0) {
                if (reachedAngle > MAX_ANGLE) {
                    reachedAngle = MAX_ANGLE;
                    paint.setColor(exceededColor);
                } else {
                    paint.setColor(reachedColor);
                }
            } else {
                reachedAngle = -reachedAngle;
                if (reachedAngle >= MAX_ANGLE) {
                    reachedAngle = MAX_ANGLE;
                    paint.setColor(falseColor);
                } else {
                    paint.setColor(minusColor);
                }
            }
            canvas.drawArc(oval, 137, reachedAngle, false, paint);
        }

        //draw the percentage text
        if (percentage >= 0) {
            if (percentage > 1) {
                paint.setColor(exceededColor);
            } else {
                paint.setColor(percentageTextColor);
            }
        } else {
            if (percentage <= -1) {
                paint.setColor(falseColor);
            } else {
                paint.setColor(minusColor);
            }
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(percentageTextSize);
        paint.getTextBounds(percentageText, 0, percentageText.length(), bounds);
        canvas.drawText(percentageText, centerX - bounds.width() / 2, centerY + bounds.height() / 6, paint);

        //draw the progress text
        if (percentage >= 0) {
            if (percentage > 1) {
                paint.setColor(exceededColor);
            } else {
                paint.setColor(progressTextColor);
            }
        } else {
            if (percentage <= -1) {
                paint.setColor(falseColor);
            } else {
                paint.setColor(minusColor);
            }
        }
        progressText = progress + "/" + max;
        paint.setTextSize(progressTextSize);
        paint.getTextBounds(progressText, 0, progressText.length(), bounds);
        canvas.drawText(progressText, centerX - bounds.width() / 2, centerY + radius * 0.65f, paint);

        //draw the title text
        if (percentage >= 0) {
            if (percentage > 1) {
                paint.setColor(exceededColor);
            } else {
                paint.setColor(titleTextColor);
            }
        } else {
            if (percentage <= -1) {
                paint.setColor(falseColor);
            } else {
                paint.setColor(minusColor);
            }
        }
        paint.setTextSize(titleTextSize);
        paint.getTextBounds(titleText, 0, titleText.length(), bounds);
        canvas.drawText(titleText, centerX - bounds.width() / 2, centerY + radius * 0.95f, paint);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public float getPercentage() {
        return progress / max * 100;
    }

    public void setProgressAndMax(int progress, int max) {
        this.progress = progress;
        this.max = max;
        invalidate();
    }

}
