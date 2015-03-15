package org.hutec.fallingsandroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This class handles drawing and mirroring the display of the connection machine.
 *
 * Created by robin on 26.02.15.
 */
public class DrawingView extends SurfaceView implements SurfaceHolder.Callback{

    //used paint, e.g. thickness and color intensity of path
    private Paint mPaint;

    //Side length in pixels of one block
    private int mBlockSize;


    private DisplayThread mDisplayThread;

    public DrawingView(Context context, AttributeSet attr) {
        super(context, attr);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mDisplayThread = new DisplayThread(holder);
        mDisplayThread.start();
        GameFactory.setDisplayThread(mDisplayThread);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Start the display thread
        if (!mDisplayThread.isRunning()) {
            mDisplayThread.setRunning(true);
        }
        GameFactory.getGameLogic().setBlockSize(this.getWidth()/GameFactory.getGameLogic().getSize());
        mBlockSize = this.getWidth()/GameFactory.getGameLogic().getSize();
    }

    public void resume() {
        mDisplayThread = new DisplayThread(getHolder());
        mDisplayThread.start();
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

        //Stops drags outside view
        if (eventX > this.getWidth() || eventY > this.getWidth()
            || eventX < 0 || eventY <0 ) return false;

        //TODO check if rounding problems due to int casting
        int worldPosX = (int) Math.floor(eventX/mBlockSize);
        int worldPosY = (int) Math.floor(eventY/mBlockSize);

        //Log.d(Integer.toString(worldPosX), Integer.toString(worldPosY));

        GameFactory.getGameLogic().addParticle(worldPosY * GameFactory.getGameLogic().getSize() + worldPosX);

        //TODO use action_move to smooth lines
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

