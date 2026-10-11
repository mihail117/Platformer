package com.play.platformer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import java.util.List;

public class LevelSelectActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.is_tablet)) {
            setRequestedOrientation(
                    android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_level_select);

        ListView list = findViewById(R.id.listLevels);
        Button btnBack = findViewById(R.id.btnBack);

        // Режим из Intent (по умолчанию — CAMPAIGN)
        String modeName = getIntent().getStringExtra(GameActivity.EXTRA_MODE);
        GameMode mode;
        try {
            mode = GameMode.valueOf(modeName);
        } catch (Exception e) {
            mode = GameMode.CAMPAIGN;
        }

        LevelStorage.ensureDefaultExists(this);
        List<LevelData> levels = LevelStorage.loadAll(this);

        // В кампании не показываем default — он ушёл в "Обучение"
        if (mode == GameMode.CAMPAIGN) {
            levels.removeIf(l -> "default".equals(l.id));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1);
        for (LevelData l : levels) adapter.add(l.name);
        list.setAdapter(adapter);

        final GameMode finalMode = mode;
        list.setOnItemClickListener((parent, view, pos, id) -> {
            Intent i = new Intent(this, GameActivity.class);
            i.putExtra(GameActivity.EXTRA_LEVEL_ID, levels.get(pos).id);
            i.putExtra(GameActivity.EXTRA_MODE, finalMode.name());
            startActivity(i);
        });

        btnBack.setOnClickListener(v -> finish());
    }
}