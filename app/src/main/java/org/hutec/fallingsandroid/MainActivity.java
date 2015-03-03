package org.hutec.fallingsandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements SensorEventListener{
    private LEDMatrixBTConn BT;
    protected static final String REMOTE_BT_DEVICE_NAME = "ledpi-teco";

    protected static final String APP_NAME ="fallingsandroid";

    //display size
    protected static final int X_SIZE = 24;
    protected  static final int Y_SIZE = 24;

    //Remote display color mode. 0 = red, 1 = green, 2 = blue, 3 = RGB
    protected static final int COLOR_MODE = 0;


    //The name this app uses to identify with the server
    protected static final String APP_Name="FallingSandroid";


    //UI elements
    private Button mSandButton;
    private Button mRockButton;
    private Button mEraseButton;
    private Button mStartButton;
    private DrawingView mDrawingView;
    private TextView mTextView;
    //private int sendDelay; not used yet

    //Sensor manager that handles all the sensor devices, e.g. accelerometer
    private SensorManager mSensorManager;

    public GameLogic game;

    private int sendDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawingView = (DrawingView) findViewById(R.id.drawingView);
        mRockButton = (Button) findViewById(R.id.btnRock);
        mRockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.currentItem = game.ROCK;
                mDrawingView.setPaintColor(Color.BLACK);
            }
        });

        mSandButton = (Button) findViewById(R.id.btnSand);
        mSandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.currentItem = game.SAND;
                mDrawingView.setPaintColor(Color.YELLOW);
            }
        });

        mEraseButton = (Button) findViewById(R.id.btnErase);
        mEraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.currentItem  = game.ERASE;
                mDrawingView.setPaintColor(Color.WHITE);
            }
        });


        mStartButton = (Button) findViewById(R.id.btnStart);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(); //Start BT
                game.simulate();
            }
        });

        game = new GameLogic();
        mDrawingView.addGame(game);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }


    protected void onResume() {
        super.onResume();
        //mSensorManager.registerListener()
    }

    /**
     * Opening BT conenction and sending data in a seperate thread.
     */
    public void start() {
        BT = new LEDMatrixBTConn(this, REMOTE_BT_DEVICE_NAME, X_SIZE, Y_SIZE, COLOR_MODE, APP_NAME);

        if (!BT.prepare() || !BT.checkIfDeviceIsPaired()) {
            mStartButton.setEnabled(true);
            return;
        }

        // Start BT sending thread.
        Thread sender = new Thread() {

            boolean loop = true;
            public void run() {

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

                // Try to connect.
                if (!BT.connect()) {
                    loop = false;
                }

                // Connected. Calculate and set send delay from maximum FPS.
                // Negative maxFPS should not happen.
                int maxFPS = BT.getMaxFPS();
                if (maxFPS > 0) {
                    sendDelay = (int) (1000.0 / maxFPS);
                } else {
                    loop = false;
                }

                // Prepare variables for making the pattern.
                int counter = 0;
                int a = 255;
                int b = 0;

                // Main sending loop.
                while (loop) {
                    counter++;

                    // Change pattern every 10 frames.
                    if (counter >= 10) {
                        if (a == 255) {
                            a = 0;
                            b = 255;
                        } else {
                            a = 255;
                            b = 0;
                        }
                        counter = 0;
                    }

                    // Fill message buffer.
                    byte[] msgBuffer = new byte[24 * 24];
                    for (int i = 0; i < (24 * 24); i++) {
                        if (i % 2 == 1) {
                            msgBuffer[i] = (byte) a;
                        } else {
                            msgBuffer[i] = (byte) b;
                        }
                    }

                    // If write fails, the connection was probably closed by the server.
                    if (!BT.write(msgBuffer)) {
                        loop = false;
                    }

                    try {
                        // Delay for a moment.
                        // Note: Delaying the same amount of time every frame will not give you constant FPS.
                        Thread.sleep(sendDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Connection terminated or lost.
                BT.closeConnection();
            }
        };

        // Start sending thread.
        sender.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch  (item.getItemId()) {
            case R.id.action_bluetooth_connect:
                connectBluetooth();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }



    /**
     * Opens bluetooth connection.
     */
    public void connectBluetooth() {
        BT = new LEDMatrixBTConn(this, REMOTE_BT_DEVICE_NAME, X_SIZE, Y_SIZE, COLOR_MODE, APP_Name);

        if(!BT.prepare() || !BT.checkIfDeviceIsPaired()) {
            //mStartButton.setEnabled(true);
            return;
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        /*if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //mTextView.setText("" + event.values[0]);
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }
}
