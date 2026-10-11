package com.play.platformer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class FreePlayModeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.is_tablet)) {
            setRequestedOrientation(
                    android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_free_play_mode);

        Button btnPeaceful = findViewById(R.id.btnPeaceful);
        Button btnHostile  = findViewById(R.id.btnHostile);
        Button btnBack     = findViewById(R.id.btnBack);

        btnPeaceful.setOnClickListener(v -> openMode(GameMode.PEACEFUL));
        btnHostile.setOnClickListener(v  -> openMode(GameMode.HOSTILE));

        btnBack.setOnClickListener(v -> finish());
    }

    private void openMode(GameMode mode) {
        Intent i = new Intent(this, LevelSelectActivity.class);
        i.putExtra(GameActivity.EXTRA_MODE, mode.name());
        startActivity(i);
    }
}