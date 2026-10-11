package com.play.platformer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {
    public static final String EXTRA_LEVEL_ID = "level_id";
    public static final String EXTRA_MODE     = "mode";

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String levelId = getIntent().getStringExtra(EXTRA_LEVEL_ID);
        if (levelId == null) levelId = "default";

        String modeName = getIntent().getStringExtra(EXTRA_MODE);
        GameMode mode;
        try {
            mode = GameMode.valueOf(modeName);
        } catch (Exception e) {
            mode = GameMode.PEACEFUL;
        }

        LevelData level = LevelStorage.load(this, levelId);
        if (level == null) level = LevelStorage.getDefaultLevel();

        gameView = new GameView(this, level, mode);
        setContentView(gameView);
    }

    @Override protected void onResume()  { super.onResume();  gameView.resume(); }
    @Override protected void onPause()   { super.onPause();   gameView.pause();  }
}