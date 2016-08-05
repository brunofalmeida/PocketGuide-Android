package com.example.cossettenavigation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.example.cossettenavigation.map.AnchorBeacon;
import com.example.cossettenavigation.map.Map;

/**
 * Created by Bruno on 2016-08-04.
 */
public class FloorMapView extends View {

    private static final String TAG = "FloorMapView";

    private Paint rectanglePaint;
    private Paint anchorBeaconPaint;


    public FloorMapView(Context context) {
        super(context);

        rectanglePaint = new Paint();
        rectanglePaint.setColor(Color.WHITE);
        rectanglePaint.setStyle(Paint.Style.STROKE);
        rectanglePaint.setStrokeWidth(25);

        anchorBeaconPaint = new Paint();
        anchorBeaconPaint.setColor(Color.WHITE);
        anchorBeaconPaint.setStyle(Paint.Style.FILL);
        anchorBeaconPaint.setStrokeWidth(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.v(TAG, "onDraw()");

/*        Log.v(TAG, String.format(
                "onDraw(): width = %d, height = %d",
                getWidth(), getHeight()));*/

        super.onDraw(canvas);

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int rectangleMargin = 50;

        int availableCanvasWidth = canvasWidth - (2 * rectangleMargin);
        int availableCanvasHeight = canvasHeight - (2 * rectangleMargin);

        double mapWidth = Map.gridWidth;
        double mapHeight = Map.gridHeight;

        double availableCanvasWidthToHeight = (double) availableCanvasWidth / availableCanvasHeight;
        double mapWidthToHeight = mapWidth / mapHeight;

        int rectangleWidth;
        int rectangleHeight;
        double pixelsPerMapUnit;

        // Map is wider than available canvas
        if (mapWidthToHeight > availableCanvasWidthToHeight) {
            rectangleWidth = availableCanvasWidth;
            rectangleHeight = (int)(rectangleWidth / mapWidthToHeight);
            pixelsPerMapUnit = rectangleWidth / mapWidth;
        }

        // Map is taller than available canvas
        else if (mapWidthToHeight < availableCanvasWidthToHeight) {
            rectangleHeight = availableCanvasHeight;
            rectangleWidth = (int)(rectangleHeight * mapWidthToHeight);
            pixelsPerMapUnit = rectangleHeight / mapHeight;
        }

        // Map and canvas have same dimension ratio
        else {
            rectangleWidth = availableCanvasWidth;
            rectangleHeight = availableCanvasHeight;
            pixelsPerMapUnit = rectangleWidth / mapWidth;
        }

        int xMargin = (canvasWidth - rectangleWidth) / 2;
        int yMargin = (canvasHeight - rectangleHeight) / 2;

        canvas.drawRect(
                xMargin,
                yMargin,
                canvasWidth - xMargin,
                canvasHeight - yMargin,
                rectanglePaint);




        canvas.drawPoint(canvasWidth / 2, canvasHeight / 2, anchorBeaconPaint);

        for (AnchorBeacon anchorBeacon : Map.getAnchorBeacons()) {
            float x = (float)(anchorBeacon.getXPosition() * pixelsPerMapUnit) + xMargin;
            float y = (float)(anchorBeacon.getYPosition() * pixelsPerMapUnit) + yMargin;

            Log.v(TAG, String.format(
                    "onDraw(): x = %f, y = %f, canvasWidth = %d, canvasHeight = %d",
                    x, y, canvasWidth, canvasHeight));

            canvas.drawPoint(x, y, anchorBeaconPaint);
        }
    }

}
