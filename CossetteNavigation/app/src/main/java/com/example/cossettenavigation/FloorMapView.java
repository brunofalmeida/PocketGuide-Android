package com.example.cossettenavigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * Created by Bruno on 2016-08-04.
 */
public class FloorMapView extends View {

    private static final String TAG = "FloorMapView";

    private Paint rectanglePaint;


    public FloorMapView(Context context) {
        super(context);

        rectanglePaint = new Paint();
        rectanglePaint.setColor(Color.WHITE);
        rectanglePaint.setStyle(Paint.Style.STROKE);
        rectanglePaint.setStrokeWidth(50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.v(TAG, "onDraw()");

/*        Log.v(TAG, String.format(
                "onDraw(): width = %d, height = %d",
                getWidth(), getHeight()));*/

        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int inset = 10;

        canvas.drawRect(inset, inset, width - inset, height - inset, rectanglePaint);
    }

}
