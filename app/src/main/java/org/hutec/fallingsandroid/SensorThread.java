package org.hutec.fallingsandroid;

import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * Sensor event handling done here.
 * Created by robin on 13.03.2015.
 */
public class SensorThread implements Runnable {

    private SensorEvent event;
    private int orientation;


    public SensorThread(SensorEvent event, int orientation) {
        this.event = event;
        this.orientation = orientation;
    }

    @Override
    public void run() {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            float gravityX, gravityY;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                gravityX = event.values[0];
                gravityY = event.values[1];
            } else {
                //Handle two possible landscape modes
                if (event.values[0] > 0) {
                    gravityX = -event.values[1];
                    gravityY = event.values[0];
                } else {
                    gravityX = event.values[1];
                    gravityY = event.values[0];
                }
            }
            GameFactory.getGameLogic().setGravity(gravityX, gravityY);
        }
    }
}
