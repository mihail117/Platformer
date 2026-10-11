package com.play.platformer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.is_tablet)) {
            setRequestedOrientation(
                    android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_main);

        LevelStorage.ensureDefaultExists(this);

        Button btnPlay   = findViewById(R.id.btnPlay);
        Button btnCreate = findViewById(R.id.btnCreate);

        btnPlay.setOnClickListener(v ->
                startActivity(new Intent(this, PlayMenuActivity.class)));

        btnCreate.setAlpha(0.5f);
    }
}