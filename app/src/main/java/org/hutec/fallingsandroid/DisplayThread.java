package org.hutec.fallingsandroid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by robin on 04.03.15.
 */
class DisplayThread extends Thread {
    private SurfaceHolder mSurfaceHolder;
    private boolean mRun = false;
    private DrawingView view;
    private Paint mPaint;

    private final long mDelay = 10;

    public DisplayThread(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        mPaint = new Paint();
        mRun = true;
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

    public void run() {
        while (mRun) {
            Canvas canvas = mSurfaceHolder.lockCanvas(null);
            if (canvas != null) {
                synchronized (mSurfaceHolder) {
                    GameFactory.getGameLogic().simulate();
                    GameFactory.getGameLogic().draw(canvas);
                    if (GameFactory.getBT() != null) {
                        GameFactory.getBT().write(GameFactory.getGameLogic().world);
                    }
                    //canvas.drawRect(0, 0, canvas.getWidth(),canvas.getHeight(), mPaint);
                }
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
            try {
                Thread.sleep(mDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean run) {
        mRun = run;
    }

    public boolean isRunning() {
        return mRun;
    }

}
