package com.aircall.pager.domain;

import java.util.Set;

public class Level {

    private final String levelId;
    private final Set<Target> targets;

    public Level(String levelId, Set<Target> targets) {
        this.levelId = levelId;
        this.targets = targets;
    }

    public String getLevelId() {
        return levelId;
    }

    public Set<Target> getTargets() {
        return targets;
    }

    @Override
    public String toString() {
        return "Level{" +
                "levelId='" + levelId + '\'' +
                ", targets=" + targets +
                '}';
    }
}
