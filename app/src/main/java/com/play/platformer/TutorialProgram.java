package com.play.platformer;

import java.util.ArrayList;
import java.util.List;

public class TutorialProgram {

    public interface Condition {
        boolean isDone(Stats s, Player p, LevelData level);
    }

    public static class Step {
        public final String text;
        public final Condition condition;
        public boolean done = false;

        public Step(String text, Condition condition) {
            this.text = text;
            this.condition = condition;
        }
    }

    public static class Stats {
        public float maxX = 0;
        public int  jumpsUsed = 0;
        public int  doubleJumpsUsed = 0;
        public int  timesStopped = 0;
        public boolean wasInAir = false;
    }

    private final List<Step> steps = new ArrayList<>();
    private int currentIndex = 0;

    public TutorialProgram(String levelId) {
        build(levelId);
    }

    public Step getCurrent() {
        if (currentIndex >= steps.size()) return null;
        return steps.get(currentIndex);
    }

    public boolean isFinished() {
        return currentIndex >= steps.size();
    }

    public boolean checkAndAdvance(Stats s, Player p, LevelData level) {
        Step cur = getCurrent();
        if (cur == null) return false;
        if (cur.condition.isDone(s, p, level)) {
            cur.done = true;
            currentIndex++;
            return true;
        }
        return false;
    }

    public int getCurrentNumber() { return currentIndex + 1; }
    public int getTotal() { return steps.size(); }

    private void build(String levelId) {
        if (levelId == null) return;
        switch (levelId) {
            case "tutorial_1": buildTutorial1(); break;
            case "tutorial_2": buildTutorial2(); break;
            case "tutorial_3": buildTutorial3(); break;
            case "tutorial_4": buildTutorial4(); break;
            case "tutorial_5": buildTutorial5(); break;
        }
    }

    private void buildTutorial1() {
        steps.add(new Step("Держи ПРАВУЮ половину экрана — беги вправо",
                (s, p, l) -> s.maxX > 200));

        steps.add(new Step("Отпусти палец — остановишься",
                (s, p, l) -> s.maxX > 200 && p.vx == 0));

        steps.add(new Step("Тапни ЛЮБОЙ половиной — прыжок",
                (s, p, l) -> s.jumpsUsed >= 1));

        steps.add(new Step("В воздухе тапни ещё — ДВОЙНОЙ прыжок!",
                (s, p, l) -> s.doubleJumpsUsed >= 1));

        steps.add(new Step("Дойди до флага →",
                (s, p, l) -> p.x > l.finishX - 100));
    }

    private void buildTutorial2() {
        steps.add(new Step("Держи правую половину и беги",
                (s, p, l) -> s.maxX > 400));

        steps.add(new Step("Впереди яма! Держи палец + тапни другой половиной — прыжок",
                (s, p, l) -> s.maxX > 500 && s.jumpsUsed >= 1));

        steps.add(new Step("В воздухе ещё тап — ДВОЙНОЙ прыжок",
                (s, p, l) -> s.doubleJumpsUsed >= 1));

        steps.add(new Step("Перепрыгни все ямы",
                (s, p, l) -> p.x > 2300));

        steps.add(new Step("Дойди до флага →",
                (s, p, l) -> p.x > l.finishX - 100));
    }

    private void buildTutorial3() {
        steps.add(new Step("Земля ровная — беги вправо",
                (s, p, l) -> s.maxX > 300));

        steps.add(new Step("Висячие платформы! Тапни противоположной половиной — прыжок",
                (s, p, l) -> s.jumpsUsed >= 1));

        steps.add(new Step("Прыгай с платформы на платформу",
                (s, p, l) -> p.x > 1000));

        steps.add(new Step("Дойди до флага →",
                (s, p, l) -> p.x > l.finishX - 100));
    }

    private void buildTutorial4() {
        steps.add(new Step("Тут и ямы, и платформы",
                (s, p, l) -> s.maxX > 500));

        steps.add(new Step("Прыгай через ямы и по платформам",
                (s, p, l) -> p.x > 2000));

        steps.add(new Step("Дойди до флага →",
                (s, p, l) -> p.x > l.finishX - 100));
    }

    private void buildTutorial5() {
        steps.add(new Step("Финальное испытание — как в кампании!",
                (s, p, l) -> s.maxX > 500));

        steps.add(new Step("Используй всё, чему научился",
                (s, p, l) -> p.x > 3000));

        steps.add(new Step("Дойди до флага →",
                (s, p, l) -> p.x > l.finishX - 100));
    }
}