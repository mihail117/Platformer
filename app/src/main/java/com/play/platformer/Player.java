package com.play.platformer;

import android.graphics.RectF;
import java.util.List;

public class Player {
    public float x, y;
    public float vx, vy;
    public float width = 60, height = 100;

    private static final float GRAVITY     = 2000f;
    private static final float MOVE_SPEED  = 500f;
    private static final float JUMP_VEL    = -900f;
    private static final float MAX_FALL    = 1500f;
    private static final float DT          = 1f / 60f;

    private int maxJumps = 2;
    private int jumpsLeft = 2;

    private boolean onGround = false;
    private final RectF bounds = new RectF();

    public Player(float x, float y) { this.x = x; this.y = y; }

    public RectF getBounds() {
        bounds.set(x, y, x + width, y + height);
        return bounds;
    }

    public int getJumpsLeft() { return jumpsLeft; }

    public boolean isOnGroundSafe() { return onGround; }

    /** Сколько прыжков разрешено: 1 — без двойного, 2 — с двойным. */
    public void setMaxJumps(int n) {
        this.maxJumps = Math.max(1, n);
        this.jumpsLeft = Math.min(this.jumpsLeft, this.maxJumps);
    }

    public void update(boolean left, boolean right, boolean jumpQueued, List<RectF> platforms) {
        vx = 0;
        if (left)  vx = -MOVE_SPEED;
        if (right) vx =  MOVE_SPEED;

        vy += GRAVITY * DT;
        if (vy > MAX_FALL) vy = MAX_FALL;

        // Прыжок — тратим одну попытку (если есть)
        if (jumpQueued && jumpsLeft > 0) {
            vy = JUMP_VEL;
            jumpsLeft--;
        }

        // ограничение высоты
        if (y < 10) { y = 10; if (vy < 0) vy = 0; }

        // восстановление прыжков при касании земли


        x += vx * DT;  resolveX(platforms);
        y += vy * DT;  onGround = false;  resolveY(platforms);

        // Коснулись земли — восстанавливаем прыжки
        if (onGround) jumpsLeft = maxJumps;
    }

    private void resolveX(List<RectF> plats) {
        RectF me = getBounds();
        for (RectF p : plats) {
            if (RectF.intersects(me, p)) {
                if (vx > 0)      x = p.left - width;
                else if (vx < 0) x = p.right;
                vx = 0;
                me.set(x, y, x + width, y + height);
            }
        }
    }

    private void resolveY(List<RectF> plats) {
        RectF me = getBounds();
        for (RectF p : plats) {
            if (RectF.intersects(me, p)) {
                if (vy > 0) {
                    y = p.top - height;
                    onGround = true;
                } else if (vy < 0) {
                    y = p.bottom;
                }
                vy = 0;
                me.set(x, y, x + width, y + height);
            }
        }
    }
}