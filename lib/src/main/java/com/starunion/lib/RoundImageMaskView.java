package com.starunion.lib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Discription:
 * Created by sz on 18/1/9.
 */

public class RoundImageMaskView extends View {
    private int cornerSize = 30;
    private Paint paint;

    public RoundImageMaskView(Context context) {
        this(context, null);

    }

    public RoundImageMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        cornerSize = DisplayUtil.dip2px(context, 3);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);//消除锯齿
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawLeftTop(canvas);
        drawRightTop(canvas);
        drawLeftBottom(canvas);
        drawRightBottom(canvas);
    }

    private void drawLeftTop(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, cornerSize);
        path.lineTo(0, 0);
        path.lineTo(cornerSize, 0);
        path.arcTo(new RectF(0, 0, cornerSize * 2, cornerSize * 2), -90, -90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLeftBottom(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, getHeight() - cornerSize);
        path.lineTo(0, getHeight());
        path.lineTo(cornerSize, getHeight());
        path.arcTo(new RectF(0, // x
                getHeight() - cornerSize * 2,// y
                cornerSize * 2,// x
                getHeight()// getWidth()// y
        ), 90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightBottom(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth() - cornerSize, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), getHeight() - cornerSize);
        RectF oval = new RectF(getWidth() - cornerSize * 2, getHeight()
                - cornerSize * 2, getWidth(), getHeight());
        path.arcTo(oval, 0, 90);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRightTop(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getWidth(), cornerSize);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth() - cornerSize, 0);
        path.arcTo(new RectF(getWidth() - cornerSize * 2, 0, getWidth(),
                0 + cornerSize * 2), -90, 90);
        path.close();
        canvas.drawPath(path, paint);
    }
}
