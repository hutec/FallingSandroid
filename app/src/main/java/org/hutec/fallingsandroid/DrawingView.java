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
import android.view.View;

/**
 * This class handles drawing and mirroring the display of the connection machine.
 *
 * Created by robin on 26.02.15.
 */
public class DrawingView extends View {

    //drawing path
    private Path mPath;

    //used paint, e.g. thickness and color intensity of path
    private Paint mPaint;

    //Canvas that is used to draw stuff
    private Canvas mCanvas;

    //canvas is converted to bitmap which is then displayed on this view
    private Bitmap mBitmap;

    private byte[] mWorld;

    //Side length in pixels of one block
    private int mBlockSize;

    //Holds the type of the current item.
    private byte mCurrentItem;

    //holds game field and information
    private GameLogic game;

    public DrawingView(Context context, AttributeSet attr) {
        super(context, attr);
        setupDrawing();

        //Start GameThread
        
    }

    private void setupDrawing() {
        //setup drawing area for interaction
        mPath = new Path();
        mPaint = new Paint();
        mCanvas = new Canvas();
    }

    /**
     * Method used for drawing on the custom view.
     *
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawPath(mPath, mPaint);
        //Custom draw routine; iterate through world
        for (int i = 0; i < 576; i++) {
            byte cell = game.world[i];
            if(cell != 0) {
                int posX = i % 24;
                int posY = i / 24;
                switch (cell) {
                    case (byte) 0:
                        mPaint.setColor(Color.WHITE);
                        break;
                    case (byte) 10:
                        mPaint.setColor(Color.YELLOW);
                        break;
                    case (byte) 127:
                        mPaint.setColor(Color.BLACK);
                        break;
                }
                canvas.drawRect(posX * mBlockSize, posY * mBlockSize, (posX + 1) * mBlockSize,(posY + 1) * mBlockSize ,  mPaint);

            }
        }
        //canvas.drawPath(mPath, mPaint);
        //canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    /**
     * Is called when view is first assigned a size and if the size changes.
     * @param width
     * @param height
     * @param oldWidth
     * @param oldHeight
     */
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        mBlockSize = height/24;
        this.setBackgroundColor(Color.WHITE);
        //mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //mCanvas = new Canvas(mBitmap);
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

        Log.d(Integer.toString(worldPosX), Integer.toString(worldPosY));

        game.world[worldPosY * 24 + worldPosX] = game.currentItem;

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
        invalidate(); //invalidates the view and thus calls onDraw()
        return true;
    }

    public void addGame(GameLogic game) {
        this.game = game;
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }
}
