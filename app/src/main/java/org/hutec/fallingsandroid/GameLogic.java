package org.hutec.fallingsandroid;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Main game routines are defined and implemented here.
 *
 * Created by robin on 28.02.15.
 */
public class GameLogic extends Activity {
    /**
     * rock and sand physic engine
     * add pressure model (propabilistic)
     */

    //Delare elements here, directly use the intensity values for the CM
    public final byte ERASE = (byte) 0;
    public final byte SAND =  (byte) 50;
    public final byte ROCK = (byte) 255;

    public byte currentItem = SAND;
    public byte[] world;

    public final int SIZE = 24;

    private int mBlockSize;

    /**
     * Constructor.
     */
    public  GameLogic() {
        world = new byte[SIZE * SIZE];
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0,0, canvas.getHeight(), canvas.getWidth(), paint);

        for (int i = 0; i < SIZE * SIZE; i++) {
            byte cell = world[i];
            if (cell != 0) {
                int posX = i % SIZE;
                int posY = i / SIZE;
                switch (cell) {
                    case ERASE:
                        paint.setColor(Color.WHITE);
                        break;
                    case SAND:
                        paint.setColor(Color.YELLOW);
                        break;
                    case ROCK:
                        paint.setColor(Color.BLACK);
                        break;
                }
                canvas.drawRect(posX * mBlockSize, posY * mBlockSize, (posX + 1) * mBlockSize, (posY + 1) * mBlockSize, paint);
            }
        }
    }

    /**
     * This method does the physics simulation and calculates next world configuration.
     */
    public void simulate() {
        /**
         * Sand mit freiem Weg => fallen in Gravitationsrichtung
         * Sand auf anderem Sand auf Stein => aufeinanderfallen bis zum Gleichgewicht
         * Sonderfall z.B U-Rohr => Druckkomponenten (abhängig von Haftreibung als Parameter)
         *
         * TODO Zeichengröße einstellbar
         */
        byte[] newWorld = new byte[SIZE * SIZE];
        for (int i = 0; i < SIZE * SIZE; i++) {
            if (world[i] == SAND) {
                ArrayList<Integer> possibleCells = findPossibleNeighbourCells(i);
                if (possibleCells.size() > 0) {
                    int r = new Random().nextInt(possibleCells.size());
                    //Log.d(Integer.toString(i), Integer.toString(r));
                    newWorld[possibleCells.get(r)] = SAND;
                } else {
                    newWorld[i] = SAND;
                }
            }
            if (world[i] == ROCK) {
                newWorld[i] = ROCK;
            }
        }
        System.arraycopy(newWorld, 0, world, 0, newWorld.length);
    }

    private ArrayList<Integer> findPossibleNeighbourCells(int position) {
        ArrayList<Integer> possibleCells = new ArrayList<Integer>();
        /*if (position - SIZE - 1 > 0 && world[position - SIZE - 1] == 0) possibleCells.add(position - SIZE -1);
        if (position - SIZE > 0 && world[position - SIZE] == 0) possibleCells.add(position - SIZE);
        if (position - SIZE + 1> 0 && world[position - SIZE + 1] == 0) possibleCells.add(position - SIZE + 1);
        if (position - 1 > 0 && world[position - 1] == 0) possibleCells.add(position - 1);
        if (position + 1 > 0 && world[position + 1] == 0) possibleCells.add(position + 1);*/
        if (position + SIZE - 1 < SIZE * SIZE && world[position + SIZE - 1] == 0) possibleCells.add(position + SIZE - 1);
        if (position + SIZE < SIZE * SIZE && world[position + SIZE] == 0) possibleCells.add(position + SIZE);
        if (position + SIZE + 1 < SIZE * SIZE && world[position + SIZE + 1] == 0) possibleCells.add(position + SIZE + 1);

        return possibleCells;
    }

    /**
     * Adds currentItem to world array at position x
     * @param x Position to add item
     */
    public void addParticle(int x) {
        if (x > 0 && x < SIZE * SIZE) {
            world[x] = currentItem;
        }
    }

    public void setCurrentItem(byte newItem) {
        currentItem = newItem;
    }

    public void setBlockSize(int size) {
        mBlockSize = size;
    }

    public int getSize() {
        return SIZE;
    }

}
