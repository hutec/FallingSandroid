package org.hutec.fallingsandroid;

import android.content.Context;

/**
 * Class used for project wide access to the same game logic instance.
 *
 * Created by robin on 04.03.15.
 */
public final class GameFactory {
    private static GameLogic mGame;

    private static LEDMatrixBTConn BT;

    private static DisplayThread displayThread;

    protected static final String REMOTE_BT_DEVICE_NAME = "ledpi-teco";

    protected static final String APP_NAME ="fallingsandroid";

    //display size
    protected static final int X_SIZE = 24;
    protected  static final int Y_SIZE = 24;

    //Remote display color mode. 0 = red, 1 = green, 2 = blue, 3 = RGB
    protected static final int COLOR_MODE = 0;


    //The name this app uses to identify with the server
    protected static final String APP_Name="FallingSandroid";


    public GameFactory() {}

    public static GameLogic getGameLogic() {
        if (mGame == null) {
            mGame = new GameLogic();
        }
        return mGame;
    }

    public static void setBT(LEDMatrixBTConn bt) {
        BT = bt;
    }

    public static LEDMatrixBTConn getBT() {
        return (BT != null) ? BT : null;
    }

    public static void setDisplayThread(DisplayThread thread) {
        displayThread = thread;
    }

    public static DisplayThread getDisplayThread() {
        return displayThread;
    }

}
