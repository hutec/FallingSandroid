package org.hutec.fallingsandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * This class handles drawing and mirroring the display of the connection machine.
 *
 * Created by robin on 26.02.15.
 */
public class DrawingView extends SurfaceView implements SurfaceHolder.Callback{
    //drawing path
    private Path mPath;

    //used paint, e.g. thickness and color intensity of path
    private Paint mPaint;

    //Side length in pixels of one block
    private int mBlockSize;

    //Holds the type of the current item.
    private byte mCurrentItem;

    //holds game field and information

    private DisplayThread mDisplayThread;

    public DrawingView(Context context, AttributeSet attr) {
        super(context, attr);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mDisplayThread = new DisplayThread(holder);
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Start the display thread
        mDisplayThread.setRunning(true);
        mDisplayThread.start();
        GameFactory.getGameLogic().setBlockSize(this.getWidth()/24);
        mBlockSize = this.getWidth()/24;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int widthMeasureSpec, int heightMeasureSpec) {
        /*int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);*/
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mDisplayThread.setRunning(false);
        while(retry) {
            try {
                mDisplayThread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     * Make View of quadratic size.
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    /**
     * Handles touching events such as drawing lines and settings pixels.
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        //TODO check if rounding problems due to int casting
        int worldPosX = (int) eventX/mBlockSize;
        int worldPosY = (int) eventY/mBlockSize;

        //Log.d(Integer.toString(worldPosX), Integer.toString(worldPosY));

        GameFactory.getGameLogic().addParticle(worldPosY * 24 + worldPosX);

        /*switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(eventX, eventY);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(eventX, eventY);
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(mPath, mPaint);

                //mPath.reset();
            default:
                return false;
        }*/
        return true;
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }
}

