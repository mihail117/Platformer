package com.play.platformer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.view.*;

public class GameView extends SurfaceView {
    private Thread gameThread;
    private volatile boolean running;
    private final SurfaceHolder holder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Runnable gameLoop;

    private static final int TARGET_FPS = 60;
    private static final long FRAME_TIME = 1000L / TARGET_FPS;

    private static final int STATE_PLAYING = 0;
    private static final int STATE_DEAD    = 1;
    private static final int STATE_WIN     = 2;
    private int state = STATE_PLAYING;
    private int deaths = 0;

    private final LevelData level;
    private final GameMode mode;
    private Player player;
    private float cameraX;
    private float scale = 1f;
    private RectF finishZone;

    // ----- УПРАВЛЕНИЕ -----
    private boolean leftPressed, rightPressed, jumpQueued;
    private int movePointerId = -1;   // id пальца, который управляет движением (-1 = нет)
    private int moveDir = 0;          // -1 влево, +1 вправо, 0 = нет

    public GameView(Context context, LevelData level, GameMode mode) {
        super(context);
        this.level = level;
        this.mode = mode;
        holder = getHolder();

        gameLoop = new Runnable() {
            @SuppressLint("WrongThread")
            @Override
            public void run() {
                while (running) {
                    long t0 = System.currentTimeMillis();
                    update();
                    draw();
                    long sleep = FRAME_TIME - (System.currentTimeMillis() - t0);
                    if (sleep > 0) try { Thread.sleep(sleep); } catch (InterruptedException ignored) { }
                }
            }
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        if (player == null) {
            scale = (float) h / LevelData.WORLD_HEIGHT;
            initFromLevel();
        }
    }

    private void initFromLevel() {
        player = new Player(100, LevelData.WORLD_HEIGHT - 400);
        finishZone = new RectF(
                level.finishX,
                LevelData.WORLD_HEIGHT - 650,
                level.finishX + 100,
                LevelData.WORLD_HEIGHT - 450
        );
        state = STATE_PLAYING;
        cameraX = 0;
        resetInput();
    }

    private void resetInput() {
        movePointerId = -1;
        moveDir = 0;
        leftPressed = false;
        rightPressed = false;
        jumpQueued = false;
    }

    private void respawn() {
        player = new Player(100, LevelData.WORLD_HEIGHT - 400);
        cameraX = 0;
        state = STATE_PLAYING;
        resetInput();
    }

    private void update() {
        if (player == null || state != STATE_PLAYING) return;

        player.update(leftPressed, rightPressed, jumpQueued, level.platforms);
        jumpQueued = false;

        if (player.y > LevelData.WORLD_HEIGHT + 200) {
            deaths++; state = STATE_DEAD; return;
        }
        if (RectF.intersects(player.getBounds(), finishZone)) {
            state = STATE_WIN; return;
        }

        float visibleWorldW = getWidth() / scale;
        float target = player.x - visibleWorldW / 3f;
        float maxCam = Math.max(0, level.worldWidth - visibleWorldW);
        cameraX = Math.max(0, Math.min(target, maxCam));
    }

    private void draw() {
        if (!holder.getSurface().isValid()) return;
        Canvas c = holder.lockCanvas();
        if (c == null) return;

        c.drawColor(Color.rgb(135, 206, 235));

        c.save();
        c.scale(scale, scale);
        c.translate(-cameraX, 0);

        paint.setColor(Color.rgb(60, 40, 20));
        for (RectF p : level.platforms) c.drawRect(p, paint);

        paint.setColor(Color.rgb(50, 200, 50));
        c.drawRect(finishZone, paint);

        int col;
        switch (player.getJumpsLeft()) {
            case 0:  col = Color.rgb(140, 140, 140); break;
            case 1:  col = Color.rgb(255, 150, 0);   break;
            default: col = Color.rgb(220, 50, 50);
        }
        paint.setColor(col);
        c.drawRect(player.getBounds(), paint);

        c.restore();

        drawControls(c);
        drawHud(c);
        drawOverlays(c);

        holder.unlockCanvasAndPost(c);
    }

    // ================= ПОДСКАЗКИ НА ЭКРАНЕ =================
    private void drawControls(Canvas c) {
        float w = getWidth(), h = getHeight();
        float midX = w / 2f;

        if (state == STATE_PLAYING) {
            if (moveDir == 0) {
                // Ничего не держим — показываем обе половины как «зоны»
                paint.setColor(Color.argb(30, 255, 255, 255));
                c.drawRect(0, 0, midX, h, paint);
                c.drawRect(midX, 0, w, h, paint);

                // разделитель
                paint.setColor(Color.argb(60, 255, 255, 255));
                paint.setStrokeWidth(2);
                c.drawLine(midX, 0, midX, h, paint);

                paint.setColor(Color.argb(150, 255, 255, 255));
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(70);
                c.drawText("←", midX / 2f, h / 2f - 20, paint);
                c.drawText("→", midX + midX / 2f, h / 2f - 20, paint);

                paint.setTextSize(24);
                paint.setColor(Color.argb(120, 255, 255, 255));
                c.drawText("удерживай", midX / 2f, h / 2f + 30, paint);
                c.drawText("удерживай", midX + midX / 2f, h / 2f + 30, paint);
                paint.setTextAlign(Paint.Align.LEFT);
            } else if (moveDir == 1) {
                // Держим ПРАВУЮ половину → двигаемся вправо.
                // На левой половине показываем «ПРЫЖОК»
                paint.setColor(Color.argb(50, 100, 255, 100));   // лёгкая подсветка левой половины
                c.drawRect(0, 0, midX, h, paint);

                paint.setColor(Color.argb(180, 100, 255, 100));
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(80);
                c.drawText("↑", midX / 2f, h / 2f - 10, paint);
                paint.setTextSize(32);
                c.drawText("ПРЫЖОК", midX / 2f, h / 2f + 50, paint);
                paint.setTextAlign(Paint.Align.LEFT);

                // правая половина — лёгкое подтверждение движения
                paint.setColor(Color.argb(80, 255, 255, 255));
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(60);
                c.drawText("→→→", midX + midX / 2f, h / 2f, paint);
                paint.setTextAlign(Paint.Align.LEFT);
            } else if (moveDir == -1) {
                // Держим ЛЕВУЮ половину → двигаемся влево.
                // На правой половине — «ПРЫЖОК»
                paint.setColor(Color.argb(50, 100, 255, 100));
                c.drawRect(midX, 0, w, h, paint);

                paint.setColor(Color.argb(180, 100, 255, 100));
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(80);
                c.drawText("↑", midX + midX / 2f, h / 2f - 10, paint);
                paint.setTextSize(32);
                c.drawText("ПРЫЖОК", midX + midX / 2f, h / 2f + 50, paint);
                paint.setTextAlign(Paint.Align.LEFT);

                // левая половина — подтверждение движения
                paint.setColor(Color.argb(80, 255, 255, 255));
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(60);
                c.drawText("←←←", midX / 2f, h / 2f, paint);
                paint.setTextAlign(Paint.Align.LEFT);
            }
        }
    }

    // ================= HUD =================
    private void drawHud(Canvas c) {
        float w = getWidth();

        float barW = Math.min(w * 0.22f, 400f);
        float barH = 40;
        float barX = 300;
        float barY = 45;

        float panelW = barX + barW + 20;
        paint.setColor(Color.argb(90, 0, 0, 0));
        c.drawRect(0, 0, panelW, 130, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(38);
        c.drawText("Смертей: " + deaths, 20, 45, paint);

        paint.setColor(Color.rgb(255, 220, 0));
        paint.setTextSize(38);
        c.drawText("🪙 0 / 0", 20, 95, paint);

        if (state == STATE_PLAYING) {
            float progress = Math.min(1f, player.x / level.finishX);

            paint.setColor(Color.argb(150, 0, 0, 0));
            c.drawRect(barX, barY, barX + barW, barY + barH, paint);

            paint.setColor(Color.rgb(50, 200, 50));
            c.drawRect(barX, barY, barX + barW * progress, barY + barH, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            paint.setColor(Color.WHITE);
            c.drawRect(barX, barY, barX + barW, barY + barH, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    private void drawOverlays(Canvas c) {
        if (state == STATE_PLAYING) return;
        int w = getWidth(), h = getHeight();
        paint.setColor(Color.argb(180, 0, 0, 0));
        c.drawRect(0, 0, w, h, paint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);

        if (state == STATE_DEAD) {
            paint.setTextSize(110);
            c.drawText("УПАЛ", w / 2f, h / 2f - 60, paint);
            paint.setTextSize(48);
            c.drawText("Смертей: " + deaths, w / 2f, h / 2f + 20, paint);
            paint.setTextSize(44);
            c.drawText("Тапни чтобы возродиться", w / 2f, h / 2f + 110, paint);
        } else {
            paint.setTextSize(120);
            c.drawText("ПОБЕДА!", w / 2f, h / 2f - 100, paint);
            paint.setTextSize(48);
            c.drawText("Смертей: " + deaths, w / 2f, h / 2f + 30, paint);
            paint.setTextSize(40);
            c.drawText("Тапни чтобы играть снова", w / 2f, h / 2f + 120, paint);
        }
        paint.setTextAlign(Paint.Align.LEFT);
    }

    // ================= УПРАВЛЕНИЕ =================
    // Логика:
    //   1) Первый тап определяет направление по половине экрана:
    //        левая половина  → движение влево
    //        правая половина → движение вправо
    //   2) Пока этот палец держится, ЛЮБОЙ другой тап = ПРЫЖОК
    //      (то есть противоположная половина автоматически становится «кнопкой прыжка»)
    //   3) Отпустил палец движения — можно снова тапнуть по любой половине,
    //      чтобы начать новое движение
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getActionMasked();
        int idx    = e.getActionIndex();
        int id     = e.getPointerId(idx);
        float midX = getWidth() / 2f;

        // --- экраны смерти/победы: любой тап = рестарт ---
        if (state != STATE_PLAYING) {
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                if (state == STATE_WIN) deaths = 0;
                respawn();
            }
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                float x = e.getX(idx);

                if (movePointerId == -1) {
                    // это палец движения — определяем направление по половине
                    movePointerId = id;
                    moveDir = (x < midX) ? -1 : +1;
                } else {
                    // уже есть палец движения → этот тап = прыжок
                    jumpQueued = true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                if (id == movePointerId) {
                    // палец движения отпущен — сбрасываем движение
                    movePointerId = -1;
                    moveDir = 0;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                movePointerId = -1;
                moveDir = 0;
                break;
            }
        }

        leftPressed  = (moveDir == -1);
        rightPressed = (moveDir == +1);
        return true;
    }

    public void resume() {
        if (running) return;
        running = true;
        gameThread = new Thread(gameLoop);
        gameThread.start();
    }

    public void pause() {
        running = false;
        if (gameThread != null) {
            try { gameThread.join(); } catch (InterruptedException ignored) {}
            gameThread = null;
        }
    }
}