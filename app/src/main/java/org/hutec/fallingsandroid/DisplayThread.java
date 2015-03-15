package org.hutec.fallingsandroid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.widget.Toast;

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
                        if (!GameFactory.getBT().write(GameFactory.getGameLogic().world)) {
                            //bluetooth problems
                            GameFactory.getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(GameFactory.getActivity(), "Es gibt Probleme mit der Bluetooth-Verbindung. Bitte prüfe diese und verbinde dich erneut", Toast.LENGTH_LONG).show();
                                }
                            });
                            GameFactory.getBT().closeConnection();
                            GameFactory.setBT(null);
                        }
                    }
                }
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
            try {
                Thread.sleep(GameFactory.getDelay());
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