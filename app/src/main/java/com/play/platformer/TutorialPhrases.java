package com.play.platformer;

import android.graphics.*;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Всё, что связано с фразами обучения.
 */
public class TutorialPhrases {

    private final String[] phrases;
    private int phraseIndex = 0;
    private volatile boolean frozen = false;
    private boolean tapArmed = false;

    private float autoPhraseX = -1f;
    private int   autoPhraseIndex = -1;
    private boolean autoPhraseFired = false;

    private float autoPhrase2X = -1f;
    private int   autoPhrase2Index = -1;
    private boolean autoPhrase2Fired = false;
    private boolean autoPhrase2Forward = false;
    private boolean phase1Done = false;

    private int unfreezeAfterPhrase = -1;
    private boolean unfreezeUsed = false;

    private boolean jumpEnabled = true;
    private boolean doubleJumpEnabled = true;

    public TutorialPhrases(String levelId, float startX, float finishX) {
        phrases = getPhrasesFor(levelId);
        frozen = (phrases != null && phrases.length > 0);
        configureTutorial(levelId, startX, finishX);
    }

    private void configureTutorial(String levelId, float startX, float finishX) {
        switch (levelId) {
            case "tutorial_1":
                unfreezeAfterPhrase = 2;
                autoPhraseX = (startX + finishX) / 2f;
                autoPhraseIndex = 3;
                autoPhrase2X = startX + 30f;
                autoPhrase2Index = 4;
                autoPhrase2Forward = false;
                jumpEnabled = false;
                doubleJumpEnabled = false;
                break;

            case "tutorial_2":
                unfreezeAfterPhrase = 3;
                autoPhraseX = 1400f;
                autoPhraseIndex = 4;
                jumpEnabled = true;
                doubleJumpEnabled = false;
                break;

            case "tutorial_3":
                unfreezeAfterPhrase = 0;
                jumpEnabled = true;
                doubleJumpEnabled = false;
                break;

            case "tutorial_4":
                unfreezeAfterPhrase = 2;
                autoPhraseX = 750f;
                autoPhraseIndex = 3;
                autoPhrase2X = 1450f;
                autoPhrase2Index = 4;
                autoPhrase2Forward = true;
                jumpEnabled = true;
                doubleJumpEnabled = true;
                break;

            case "tutorial_5":
                unfreezeAfterPhrase = 1;    // разморозка после 2-й фразы
                jumpEnabled = true;
                doubleJumpEnabled = true;
                // победа с начала — фраз больше нет
                break;
        }
    }

    private String[] getPhrasesFor(String levelId) {
        switch (levelId) {
            case "tutorial_1":
                return new String[] {
                        "Привет ты в обучении",
                        "Для того чтобы двигаться зажми ту половину экрана, которая по направлению твоего движения (например чтобы идти вправо зажми правую сторону, для движения влево левую)",
                        "Начнем! Твоя задача немного пройти вправо (зажми правую половину экрана)",
                        "У тебя отлично получается, теперь вернись в начало (зажми левую сторону)",
                        "Молодец! Теперь дойди до конца и перейди ко 2-му этапу! :)"
                };
            case "tutorial_2":
                return new String[] {
                        "Это 2-й этап обучения, в 1-м этапе ты научился \"ходить\"",
                        "Во 2-м этапе ты научишься прыгать",
                        "Для того чтобы прыгать во время движения нажми на ту сторону экрана, которая противоположна стороне движения",
                        "Начнем! Двигайся вправо и перепрыгивай ямы!",
                        "Хорошо идешь! Но будь осторожен — ямы постепенно увеличиваются!"
                };
            case "tutorial_3":
                return new String[] {
                        "Это 3-й этап обучения, здесь вместо ям летучие платформы"
                };
            case "tutorial_4":
                return new String[] {
                        "Это 4-й этап обучения, в нем мы научимся двойным прыжкам",
                        "Чтобы использовать 2-й прыжок просто во время движения нажми прыжок 2 раза",
                        "Приступим!",
                        "Начинай движение и используй двойной прыжок!",
                        "Молодец! Используй двойной прыжок для завершения этапа!"
                };
            case "tutorial_5":
                return new String[] {
                        "Это последний этап, закрепление",
                        "Просто пройди уровень"
                };
        }
        return null;
    }

    public boolean isFrozen() { return frozen; }
    public boolean isJumpEnabled() { return jumpEnabled; }
    public boolean isDoubleJumpEnabled() { return doubleJumpEnabled; }

    public boolean isWinEnabled() {
        if (phrases == null) return true;
        return !frozen && phraseIndex >= phrases.length;
    }

    public void reset() {
        phraseIndex = 0;
        tapArmed = false;
        autoPhraseFired = false;
        autoPhrase2Fired = false;
        phase1Done = false;
        unfreezeUsed = false;
        frozen = (phrases != null && phrases.length > 0);
    }

    public void checkAutoTrigger(float playerX) {
        if (frozen) return;
        if (autoPhraseFired) return;
        if (autoPhraseX <= 0) return;
        if (autoPhraseIndex < 0) return;
        if (phrases == null || autoPhraseIndex >= phrases.length) return;

        if (playerX >= autoPhraseX) {
            autoPhraseFired = true;
            phraseIndex = autoPhraseIndex;
            frozen = true;
        }
    }

    public void checkAutoTrigger2(float playerX) {
        if (frozen) return;
        if (!phase1Done) return;
        if (autoPhrase2Fired) return;
        if (autoPhrase2X <= 0) return;
        if (autoPhrase2Index < 0) return;
        if (phrases == null || autoPhrase2Index >= phrases.length) return;

        boolean triggered = autoPhrase2Forward
                ? (playerX >= autoPhrase2X)
                : (playerX <= autoPhrase2X);

        if (triggered) {
            autoPhrase2Fired = true;
            phraseIndex = autoPhrase2Index;
            frozen = true;
        }
    }

    public boolean handleTouch(MotionEvent e) {
        if (!frozen) return false;

        int action = e.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            tapArmed = true;
        } else if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_POINTER_UP) {
            if (tapArmed) {
                tapArmed = false;

                if (autoPhraseFired && !phase1Done && phraseIndex == autoPhraseIndex) {
                    phase1Done = true;
                    if (autoPhrase2X > 0) {
                        frozen = false;
                        phraseIndex = autoPhraseIndex + 1;
                    } else {
                        frozen = false;
                        phraseIndex = phrases.length;
                    }
                    return true;
                }

                int next = phraseIndex + 1;
                if (next >= phrases.length) {
                    frozen = false;
                    phraseIndex = phrases.length;
                } else {
                    phraseIndex = next;
                }
                if (!unfreezeUsed && unfreezeAfterPhrase >= 0 && phraseIndex > unfreezeAfterPhrase) {
                    frozen = false;
                    unfreezeUsed = true;
                }
            }
        } else if (action == MotionEvent.ACTION_CANCEL) {
            tapArmed = false;
        }
        return true;
    }

    public void draw(Canvas c, Paint paint) {
        if (!frozen) return;
        if (phrases == null) return;
        int idx = phraseIndex;
        if (idx >= phrases.length) return;

        int w = c.getWidth(), h = c.getHeight();

        paint.setColor(Color.argb(150, 0, 0, 0));
        c.drawRect(0, 0, w, h, paint);

        float textSize = 52;
        float maxWidth = w * 0.85f;
        float lineHeight = textSize * 1.25f;

        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);

        List<String> lines = wrapText(phrases[idx], maxWidth, paint);

        float totalHeight = lines.size() * lineHeight;
        float startY = h / 2f - totalHeight / 2f + textSize;

        for (int i = 0; i < lines.size(); i++) {
            c.drawText(lines.get(i), w / 2f, startY + i * lineHeight, paint);
        }

        paint.setTextSize(32);
        paint.setColor(Color.argb(180, 255, 255, 255));
        c.drawText("тапни чтобы продолжить", w / 2f,
                startY + lines.size() * lineHeight + 40, paint);

        paint.setTextAlign(Paint.Align.LEFT);
    }

    private List<String> wrapText(String text, float maxWidth, Paint paint) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String test = current.length() == 0 ? word : current + " " + word;
            float testWidth = paint.measureText(test);

            if (testWidth <= maxWidth) {
                if (current.length() > 0) current.append(" ");
                current.append(word);
            } else {
                if (current.length() > 0) {
                    lines.add(current.toString());
                    current = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            }
        }
        if (current.length() > 0) lines.add(current.toString());

        return lines;
    }
}