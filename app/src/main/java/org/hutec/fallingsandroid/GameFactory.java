package org.hutec.fallingsandroid;

/**
 * Class used for project wide access to the same game logic instance.
 *
 * Created by robin on 04.03.15.
 */
public final class GameFactory {
    private static GameLogic mGame;


    public GameFactory() {}

    public static GameLogic getGameLogic() {
        if (mGame == null) {
            mGame = new GameLogic();
        }
        return mGame;
    }
}
