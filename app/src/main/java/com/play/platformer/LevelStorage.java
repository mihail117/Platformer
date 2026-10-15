package com.play.platformer;

import android.content.Context;
import android.graphics.RectF;
import org.json.JSONObject;

import java.io.*;
        import java.util.ArrayList;
import java.util.List;

public class LevelStorage {
    private static final String DIR = "levels";

    public static File getLevelsDir(Context ctx) {
        File d = new File(ctx.getFilesDir(), DIR);
        if (!d.exists()) d.mkdirs();
        return d;
    }

    public static void save(Context ctx, LevelData level) throws Exception {
        File f = new File(getLevelsDir(ctx), level.id + ".json");
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(level.toJson().toString());
        }
    }

    public static LevelData load(Context ctx, String id) {
        File f = new File(getLevelsDir(ctx), id + ".json");
        if (!f.exists()) return null;
        try {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
            return LevelData.fromJson(new JSONObject(sb.toString()));
        } catch (Exception e) {
            return null;
        }
    }

    public static List<LevelData> loadAll(Context ctx) {
        List<LevelData> result = new ArrayList<>();
        File[] files = getLevelsDir(ctx).listFiles((d, n) -> n.endsWith(".json"));
        if (files == null) return result;
        for (File f : files) {
            try {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                }
                result.add(LevelData.fromJson(new JSONObject(sb.toString())));
            } catch (Exception ignored) { }
        }
        return result;
    }

    public static void ensureDefaultExists(Context ctx) {
        File f = new File(getLevelsDir(ctx), "default.json");
        if (!f.exists()) {
            try { save(ctx, getDefaultLevel()); } catch (Exception ignored) { }
        }
    }

    public static LevelData getDefaultLevel() {
        LevelData d = new LevelData();
        d.id = "default";
        d.name = "Стандартный";
        d.worldWidth = 4500;
        d.finishX = 4200;

        float H   = LevelData.WORLD_HEIGHT;
        float gY  = H - 100;
        float m1  = H - 300;
        float m2  = H - 450;
        float m3  = H - 600;

        d.platforms.add(new RectF(0,    gY, 800,  H));
        d.platforms.add(new RectF(950,  gY, 1500, H));
        d.platforms.add(new RectF(1700, gY, 2400, H));
        d.platforms.add(new RectF(2600, gY, 3500, H));
        d.platforms.add(new RectF(3700, gY, 4400, H));

        d.platforms.add(new RectF(600,  m1, 900,  m1 + 40));
        d.platforms.add(new RectF(1100, m2, 1400, m2 + 40));
        d.platforms.add(new RectF(1800, m2, 2100, m2 + 40));
        d.platforms.add(new RectF(2200, m3, 2500, m3 + 40));
        d.platforms.add(new RectF(2700, m1, 2900, m1 + 40));
        d.platforms.add(new RectF(3000, m2, 3200, m2 + 40));
        d.platforms.add(new RectF(3300, m3, 3500, m3 + 40));
        d.platforms.add(new RectF(3800, m2, 4300, m2 + 40));

        return d;
    }
    // ============ УРОВНИ ОБУЧЕНИЯ ============
    public static void ensureTutorialLevels(Context ctx) {
        for (int i = 1; i <= 5; i++) {
            String id = "tutorial_" + i;
            File f = new File(getLevelsDir(ctx), id + ".json");
            if (!f.exists()) {
                try { save(ctx, createTutorialLevel(i)); }
                catch (Exception ignored) { }
            }
        }
    }

    private static LevelData createTutorialLevel(int n) {
        LevelData d = new LevelData();
        d.id   = "tutorial_" + n;
        d.name = "Обучение " + n;

        float H  = LevelData.WORLD_HEIGHT;
        float gY = H - 100;
        float m1 = H - 300, m2 = H - 450, m3 = H - 600;

        switch (n) {
            case 1: // Ровная земля — только движение и прыжок
                d.worldWidth = 1600;
                d.finishX    = 1400;
                d.platforms.add(new RectF(0, gY, 1600, H));
                break;

            case 2: // Земля с ямами (5 ям, всё шире)
                d.worldWidth = 2900;
                d.finishX    = 2700;
                d.platforms.add(new RectF(0,    gY, 400,  H));   // старт
                d.platforms.add(new RectF(480,  gY, 850,  H));   // яма 80
                d.platforms.add(new RectF(1000, gY, 1450, H));   // яма 150
                d.platforms.add(new RectF(1650, gY, 2150, H));   // яма 200
                d.platforms.add(new RectF(2400, gY, 2900, H));   // яма 250
                break;

            case 3: // Только платформы, без ям
                d.worldWidth = 2000;
                d.finishX    = 1800;
                d.platforms.add(new RectF(0, gY, 2000, H));       // земля
                d.platforms.add(new RectF(400,  m1, 700,  m1 + 40));
                d.platforms.add(new RectF(800,  m2, 1100, m2 + 40));
                d.platforms.add(new RectF(1200, m3, 1500, m3 + 40));
                break;

            case 4: // Комбинация: ямы + висячие платформы
                d.worldWidth = 3200;
                d.finishX    = 3000;
                d.platforms.add(new RectF(0,    gY, 500,  H));
                d.platforms.add(new RectF(600,  gY, 1100, H));   // яма 100
                d.platforms.add(new RectF(1300, gY, 1800, H));   // яма 200
                d.platforms.add(new RectF(2050, gY, 2600, H));   // яма 250
                d.platforms.add(new RectF(2800, gY, 3200, H));   // яма 200
                // висячие
                d.platforms.add(new RectF(650,  m1, 900,  m1 + 40));
                d.platforms.add(new RectF(1350, m2, 1600, m2 + 40));
                d.platforms.add(new RectF(2100, m2, 2400, m2 + 40));
                break;

            case 5: // Копия стандартного (default) без монеток
                d.worldWidth = 4500;
                d.finishX    = 4200;
                d.platforms.add(new RectF(0,    gY, 800,  H));
                d.platforms.add(new RectF(950,  gY, 1500, H));
                d.platforms.add(new RectF(1700, gY, 2400, H));
                d.platforms.add(new RectF(2600, gY, 3500, H));
                d.platforms.add(new RectF(3700, gY, 4400, H));

                d.platforms.add(new RectF(600,  m1, 900,  m1 + 40));
                d.platforms.add(new RectF(1100, m2, 1400, m2 + 40));
                d.platforms.add(new RectF(1800, m2, 2100, m2 + 40));
                d.platforms.add(new RectF(2200, m3, 2500, m3 + 40));
                d.platforms.add(new RectF(2700, m1, 2900, m1 + 40));
                d.platforms.add(new RectF(3000, m2, 3200, m2 + 40));
                d.platforms.add(new RectF(3300, m3, 3500, m3 + 40));
                d.platforms.add(new RectF(3800, m2, 4300, m2 + 40));
                break;
        }
        return d;
    }
}