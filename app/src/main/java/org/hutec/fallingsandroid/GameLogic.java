package org.hutec.fallingsandroid;

import android.app.Activity;
import android.os.AsyncTask;
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
    public final byte SAND =  (byte) 10;
    public final byte ROCK = (byte) 127;

    public byte currentItem = SAND;
    public byte[] world;

    public final int SIZE = 24;

    /**
     * Constructor.
     */
    public  GameLogic() {
        world = new byte[576];
    }

    //This method does the physics simulation and calculates next world configuration.
    public void simulate() {

        /**
         * Sand mit freiem Weg => fallen in Gravitationsrichtung
         * Sand auf anderem Sand auf Stein => aufeinanderfallen bis zum Gleichgewicht
         * Sonderfall z.B U-Rohr => Druckkomponenten (abhängig von Haftreibung als Parameter)
         *
         * TODO Zeichengröße einstellbar
         *
         */
        findViewById(R.id.drawingView).setEnabled(false); //disable touch events during calculation

        Thread simulation = new Thread() {

            public void run() {
                for (int i = 0; i < 576; i++) {
                    if (world[i] == SAND) {
                        ArrayList<Integer> possibleCells = findPossibleNeighbourCells(i);
                        int r = new Random().nextInt(possibleCells.size());
                        world[i] = 0;
                    }
                }
            }
        };
        simulation.run();

        findViewById(R.id.drawingView).setEnabled(true);
    }

    private ArrayList<Integer> findPossibleNeighbourCells(int position) {
        ArrayList<Integer> possibleCells = new ArrayList<Integer>();
        /*if (position - SIZE - 1 > 0 && world[position - SIZE - 1] == 0) possibleCells.add(position - SIZE -1);
        if (position - SIZE > 0 && world[position - SIZE] == 0) possibleCells.add(position - SIZE);
        if (position - SIZE + 1> 0 && world[position - SIZE + 1] == 0) possibleCells.add(position - SIZE + 1);
        if (position - 1 > 0 && world[position - 1] == 0) possibleCells.add(position - 1);
        if (position + 1 > 0 && world[position + 1] == 0) possibleCells.add(position + 1);*/
        if (position + SIZE - 1 < 576 && world[position + SIZE - 1] == 0) possibleCells.add(position + SIZE - 1);
        if (position + SIZE < 576 && world[position + SIZE] == 0) possibleCells.add(position + SIZE);
        if (position + SIZE + 1 < 576 && world[position + SIZE + 1] == 0) possibleCells.add(position + SIZE +1);

        return possibleCells;
    }
}
