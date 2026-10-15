package com.play.platformer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.List;

public class FreePlayModeActivity extends Activity {

    private GameMode selectedMode = GameMode.PEACEFUL;
    private LevelData selectedLevel = null;

    private Button btnChoose;
    private TextView tvSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.is_tablet)) {
            setRequestedOrientation(
                    android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_free_play_mode);

        LevelStorage.ensureDefaultExists(this);
        LevelStorage.ensureTutorialLevels(this);

        Spinner spinner    = findViewById(R.id.spinnerMode);
        btnChoose          = findViewById(R.id.btnChoose);
        tvSelected         = findViewById(R.id.tvSelected);
        Button btnBack     = findViewById(R.id.btnBack);

        // --- Spinner выбора режима ---
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Мирный режим", "Враждебный режим"});
        modeAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(modeAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                selectedMode = (pos == 0) ? GameMode.PEACEFUL : GameMode.HOSTILE;
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        // --- Кнопка выбора/запуска ---
        btnChoose.setOnClickListener(v -> {
            if (selectedLevel == null) {
                showLevelPicker();
            } else {
                playSelected();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void showLevelPicker() {
        List<LevelData> levels = LevelStorage.loadAll(this);
        if (levels.isEmpty()) {
            Toast.makeText(this, "Нет уровней", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[levels.size()];
        for (int i = 0; i < levels.size(); i++) names[i] = levels.get(i).name;

        new AlertDialog.Builder(this)
                .setTitle("Выбери уровень")
                .setItems(names, (dialog, which) -> {
                    selectedLevel = levels.get(which);
                    tvSelected.setText("Выбран: " + selectedLevel.name);
                    btnChoose.setText("Играть");
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void playSelected() {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(GameActivity.EXTRA_LEVEL_ID, selectedLevel.id);
        i.putExtra(GameActivity.EXTRA_MODE, selectedMode.name());
        startActivity(i);
    }
}