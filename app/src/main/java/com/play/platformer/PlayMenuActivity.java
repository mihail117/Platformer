package com.play.platformer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class PlayMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.is_tablet)) {
            setRequestedOrientation(
                    android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_play_menu);

        Button btnLevels   = findViewById(R.id.btnLevels);   // «Кампания»
        Button btnTutorial = findViewById(R.id.btnTutorial); // «Обучение»
        Button btnFreePlay = findViewById(R.id.btnFreePlay); // «Свободная игра»
        Button btnBack     = findViewById(R.id.btnBack);

        // --- Кампания: список уровней в режиме CAMPAIGN ---
        btnLevels.setOnClickListener(v -> {
            Intent i = new Intent(this, LevelSelectActivity.class);
            i.putExtra(GameActivity.EXTRA_MODE, GameMode.CAMPAIGN.name());
            startActivity(i);
        });

        // --- Обучение: сразу уровень "default" в мирном режиме ---
        btnTutorial.setOnClickListener(v -> {
            Intent i = new Intent(this, GameActivity.class);
            i.putExtra(GameActivity.EXTRA_LEVEL_ID, "default");
            i.putExtra(GameActivity.EXTRA_MODE, GameMode.PEACEFUL.name());
            startActivity(i);
        });

        // --- Свободная игра: выбор режима (мирный / враждебный) ---
        btnFreePlay.setOnClickListener(v ->
                startActivity(new Intent(this, FreePlayModeActivity.class)));

        // --- Назад ---
        btnBack.setOnClickListener(v -> finish());
    }
}