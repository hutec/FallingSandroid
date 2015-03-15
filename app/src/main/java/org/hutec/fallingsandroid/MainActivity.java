package org.hutec.fallingsandroid;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;


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
    private ImageButton mStartButton;
    private ImageButton mClearButton;
    private DrawingView mDrawingView;
    private ToggleButton mGravitySwitch;

    //Sensor manager that handles all the sensor devices, e.g. accelerometer
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    public GameLogic game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        game = GameFactory.getGameLogic(); //creates new GameLogic/Engine
        GameFactory.setActivity(this);

        setContentView(R.layout.activity_main);

        mDrawingView = (DrawingView) findViewById(R.id.drawingView);

        mRockButton = (Button) findViewById(R.id.btnRock);
        mRockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.setCurrentItem(game.ROCK);
                mDrawingView.setPaintColor(Color.BLACK);
            }
        });

        mSandButton = (Button) findViewById(R.id.btnSand);
        mSandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.setCurrentItem(game.SAND);
                mDrawingView.setPaintColor(Color.YELLOW);
            }
        });

        mEraseButton = (Button) findViewById(R.id.btnErase);
        mEraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.setCurrentItem(game.ERASE);
                mDrawingView.setPaintColor(Color.WHITE);
            }
        });


        mStartButton = (ImageButton) findViewById(R.id.btnStart);
        //This is done for startup
        if (GameFactory.getGameLogic().isPaused()) {
            mStartButton.setImageResource(android.R.drawable.ic_media_pause);
            //mStartButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            //mStartButton.setText("Start");
        } else {
            mStartButton.setImageResource(android.R.drawable.ic_media_play);
            //mStartButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            //mStartButton.setText("Stop");
        }
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameFactory.getGameLogic().togglePaused();
                if (GameFactory.getGameLogic().isPaused()) {
                    mStartButton.setImageResource(android.R.drawable.ic_media_pause);
                    //mStartButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                } else {
                    mStartButton.setImageResource(android.R.drawable.ic_media_play);
                    //mStartButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                }
            }
        });

        mClearButton = (ImageButton) findViewById(R.id.btnClear);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameFactory.getGameLogic().clear();
            }
        });

        mGravitySwitch = (ToggleButton) findViewById(R.id.gravitationSwitch);
        mGravitySwitch.setChecked(GameFactory.getGameLogic().getGravity());
        mGravitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mGravitySwitch.setChecked(!GameFactory.getGameLogic().getGravity());
                GameFactory.getGameLogic().toggleGravity();
            }
        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkGravity = menu.findItem(R.id.action_gravity_check);
        checkGravity.setChecked(GameFactory.getGameLogic().getGravity());
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
                return true;
            case R.id.action_bluetooth_disconnect:
                disconnectBluetooth();
                return true;
            case R.id.action_gravity_check:
                item.setChecked(!GameFactory.getGameLogic().getGravity());
                GameFactory.getGameLogic().toggleGravity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onPause() {
        super.onPause();
        //GameFactory.getBT().onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mDrawingView.resume();
    }

    /**
     * Opens bluetooth connection and adds it to GameFactory.
     */
    private void connectBluetooth() {
        if (GameFactory.getBT() != null) {
            Toast.makeText(getApplicationContext(), "Bluetooth bereits verbunden", Toast.LENGTH_SHORT).show();
            return;
        }
        Thread btOpener = new Thread() {
            public void run() {
                BT = new LEDMatrixBTConn(getApplicationContext(), REMOTE_BT_DEVICE_NAME, X_SIZE, Y_SIZE, COLOR_MODE, APP_NAME);
                if (!BT.prepare()) return;
                if (BT.checkIfDeviceIsPaired()) {
                    BT.connect();
                    GameFactory.setBT(BT);
                }
            }
        };
        btOpener.run();
    }

    /**
     * Disconnects bluetooth connection.
     */
    private void disconnectBluetooth() {
        if (GameFactory.getBT() != null) {
            //First set null, so that DisplayThread has no problem and shows toast
            LEDMatrixBTConn bt = GameFactory.getBT();
            GameFactory.setBT(null);
            bt.closeConnection();
            Toast.makeText(this.getApplicationContext(), "Bluetooth getrennt.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this.getApplicationContext(), "Es existiert keine Bluetooth-Verbindung.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Runnable sensorThread = new SensorThread(event, getResources().getConfiguration().orientation);
        new Thread(sensorThread).start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }
}
