package com.yunsong.bujen.pary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.util.AttributeSet;
import android.view.View;

import android.graphics.Path;


import java.util.LinkedList;
import java.util.Queue;

public class VisualizerView extends View {
    private Paint paint;
    private Path wavePath;
    private Queue<Integer> amplitudeQueue = new LinkedList<>();
    private static final int MAX_POINTS = 50; // 采样点数量

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.parseColor("#BAAE9C")); // 设置颜色
        paint.setStrokeWidth(6f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true); // 抗锯齿
        wavePath = new Path();
    }

    public void updateVolume(int amplitude) {
        if (amplitudeQueue.size() >= MAX_POINTS) {
            amplitudeQueue.poll(); // 移除旧数据
        }
        amplitudeQueue.add(amplitude);
        postInvalidateOnAnimation(); // 平滑刷新 UI
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int centerY = height / 2;

        wavePath.reset();
        float spacing = (float) width / MAX_POINTS;
        int i = 0;

        for (int amp : amplitudeQueue) {
            float x = i * spacing;
            float y = centerY - ((float) amp / 32768) * centerY; // 音量映射到高度
            if (i == 0) {
                wavePath.moveTo(x, y);
            } else {
                wavePath.lineTo(x, y);
            }
            i++;
        }

        canvas.drawPath(wavePath, paint);
    }
}
