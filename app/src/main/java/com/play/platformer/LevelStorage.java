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
}