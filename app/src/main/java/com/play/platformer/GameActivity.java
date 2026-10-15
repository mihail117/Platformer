package com.play.platformer;

import android.app.Activity;
import android.content.Intent;
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

        final String levelId = getIntent().getStringExtra(EXTRA_LEVEL_ID) != null
                ? getIntent().getStringExtra(EXTRA_LEVEL_ID) : "default";

        String modeName = getIntent().getStringExtra(EXTRA_MODE);
        GameMode mode;
        try {
            mode = GameMode.valueOf(modeName);
        } catch (Exception e) {
            mode = GameMode.PEACEFUL;
        }

        final String nextLevelId = getNextTutorialId(levelId);

        LevelData level = LevelStorage.load(this, levelId);
        if (level == null) level = LevelStorage.getDefaultLevel();

        gameView = new GameView(this, level, mode, nextLevelId);

        gameView.setLevelCompleteListener(() -> {
            if (nextLevelId != null) {
                Intent i = new Intent(this, GameActivity.class);
                i.putExtra(EXTRA_LEVEL_ID, nextLevelId);
                i.putExtra(EXTRA_MODE, GameMode.PEACEFUL.name());
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
                finish();
            } else {
                goToMainMenu();
            }
        });

        gameView.setOnExitListener(this::goToMainMenu);

        setContentView(gameView);
    }

    private void goToMainMenu() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    private String getNextTutorialId(String levelId) {
        if (levelId == null || !levelId.startsWith("tutorial_")) return null;
        try {
            int n = Integer.parseInt(levelId.substring("tutorial_".length()));
            if (n < 5) return "tutorial_" + (n + 1);
        } catch (Exception ignored) { }
        return null;
    }

    @Override protected void onResume()  { super.onResume();  gameView.resume(); }
    @Override protected void onPause()   { super.onPause();   gameView.pause();  }
}