package com.example.medicam.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom View for displaying BMI gauge with needle indicator
 */
public class BMIGaugeView extends View {
    private Paint arcPaint;
    private Paint needlePaint;
    private Paint textPaint;
    private Paint circlePaint;
    private float bmiValue = 22.5f;
    private float centerX;
    private float centerY;
    private float radius;

    // BMI ranges
    private static final float MIN_BMI = 10f;
    private static final float MAX_BMI = 40f;
    private static final float UNDERWEIGHT_MIN = 10f;
    private static final float UNDERWEIGHT_MAX = 18.5f;
    private static final float NORMAL_MIN = 18.5f;
    private static final float NORMAL_MAX = 25f;
    private static final float OVERWEIGHT_MIN = 25f;
    private static final float OVERWEIGHT_MAX = 30f;
    private static final float OBESE_MIN = 30f;
    private static final float EXTREME_OBESE_MIN = 35f;

    public BMIGaugeView(Context context) {
        super(context);
        init();
    }

    public BMIGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BMIGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);

        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setColor(Color.BLACK);
        needlePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40f);
        textPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (centerX == 0 || centerY == 0) {
            centerX = getWidth() / 2f;
            centerY = getHeight() / 1.5f;
            radius = Math.min(getWidth(), getHeight()) / 2.8f;
        }

        // Draw arcs for different BMI categories
        drawBMIArcs(canvas);

        // Draw needle
        drawNeedle(canvas);

        // Draw center circle
        circlePaint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY, radius * 0.15f, circlePaint);

        // Draw outer border
        Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.parseColor("#1976D2"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4f);
        canvas.drawCircle(centerX, centerY, radius, borderPaint);
    }

    private void drawBMIArcs(Canvas canvas) {
        float arcWidth = 20f;
        float startRadius = radius - arcWidth;
        RectF rect = new RectF(
                centerX - startRadius,
                centerY - startRadius,
                centerX + startRadius,
                centerY + startRadius
        );

        // Underweight (Blue) - 10-18.5
        arcPaint.setColor(Color.parseColor("#2196F3")); // Blue
        arcPaint.setStrokeWidth(arcWidth);
        canvas.drawArc(rect, 180, 36, false, arcPaint);
        drawLabel(canvas, "UNDERWEIGHT", 198, Color.parseColor("#2196F3"));

        // Normal (Green) - 18.5-25
        arcPaint.setColor(Color.parseColor("#4CAF50")); // Green
        canvas.drawArc(rect, 216, 36, false, arcPaint);
        drawLabel(canvas, "NORMAL", 234, Color.parseColor("#4CAF50"));

        // Overweight (Yellow) - 25-30
        arcPaint.setColor(Color.parseColor("#FFC107")); // Yellow
        canvas.drawArc(rect, 252, 36, false, arcPaint);
        drawLabel(canvas, "OVERWEIGHT", 270, Color.parseColor("#FFC107"));

        // Obese (Orange) - 30-35
        arcPaint.setColor(Color.parseColor("#FF9800")); // Orange
        canvas.drawArc(rect, 288, 27, false, arcPaint);
        drawLabel(canvas, "OBESE", 301.5f, Color.parseColor("#FF9800"));

        // Extreme Obese (Red) - 35+
        arcPaint.setColor(Color.parseColor("#F44336")); // Red
        canvas.drawArc(rect, 315, 45, false, arcPaint);
        drawLabel(canvas, "EXTREME OBESE", 337.5f, Color.parseColor("#F44336"));
    }

    private void drawLabel(Canvas canvas, String label, float angle, int color) {
        Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setColor(color);
        labelPaint.setTextSize(28f);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        labelPaint.setTypeface(android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD));

        float labelRadius = radius + 40f;
        double radians = Math.toRadians(angle);
        float x = centerX + (float) (labelRadius * Math.cos(radians));
        float y = centerY + (float) (labelRadius * Math.sin(radians));

        canvas.drawText(label, x, y, labelPaint);
    }

    private void drawNeedle(Canvas canvas) {
        // Calculate angle based on BMI value
        // 180 degrees = 10 BMI (minimum)
        // 360 degrees = 40 BMI (maximum)
        float angle = 180 + ((bmiValue - MIN_BMI) / (MAX_BMI - MIN_BMI)) * 180;

        double radians = Math.toRadians(angle);
        float needleLength = radius * 0.75f;
        float endX = centerX + (float) (needleLength * Math.cos(radians));
        float endY = centerY + (float) (needleLength * Math.sin(radians));

        // Draw needle line
        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(6f);
        canvas.drawLine(centerX, centerY, endX, endY, linePaint);

        // Draw needle tip triangle
        Path path = new Path();
        float tipRadius = 8f;
        float tip1X = centerX + (float) (tipRadius * Math.cos(radians - 0.3));
        float tip1Y = centerY + (float) (tipRadius * Math.sin(radians - 0.3));
        float tip2X = centerX + (float) (tipRadius * Math.cos(radians + 0.3));
        float tip2Y = centerY + (float) (tipRadius * Math.sin(radians + 0.3));

        path.moveTo(endX, endY);
        path.lineTo(tip1X, tip1Y);
        path.lineTo(tip2X, tip2Y);
        path.close();

        canvas.drawPath(path, needlePaint);
    }

    public void setBMI(float bmi) {
        this.bmiValue = bmi;
        invalidate();
    }

    public float getBMI() {
        return bmiValue;
    }
}
