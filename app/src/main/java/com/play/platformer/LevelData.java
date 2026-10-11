package com.play.platformer;

import android.graphics.RectF;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LevelData {
    // Все уровни живут в виртуальной системе координат высотой WORLD_HEIGHT.
    // При отрисовке масштабируется под экран — уровни переносимы между устройствами.
    public static final int WORLD_HEIGHT = 1000;

    public String id;
    public String name;
    public int    worldWidth;
    public float  finishX;
    public List<RectF> platforms = new ArrayList<>();

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("name", name);
        o.put("worldWidth", worldWidth);
        o.put("finishX", finishX);
        o.put("worldHeight", WORLD_HEIGHT);

        JSONArray arr = new JSONArray();
        for (RectF r : platforms) {
            JSONObject p = new JSONObject();
            p.put("l", r.left);
            p.put("t", r.top);
            p.put("r", r.right);
            p.put("b", r.bottom);
            arr.put(p);
        }
        o.put("platforms", arr);
        return o;
    }

    public static LevelData fromJson(JSONObject o) throws JSONException {
        LevelData d = new LevelData();
        d.id         = o.optString("id", "unknown");
        d.name       = o.optString("name", "Level");
        d.worldWidth = o.optInt("worldWidth", 4500);
        d.finishX    = (float) o.optDouble("finishX", 4200);

        JSONArray arr = o.optJSONArray("platforms");
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject p = arr.getJSONObject(i);
                d.platforms.add(new RectF(
                        (float) p.getDouble("l"),
                        (float) p.getDouble("t"),
                        (float) p.getDouble("r"),
                        (float) p.getDouble("b")
                ));
            }
        }
        return d;
    }
}