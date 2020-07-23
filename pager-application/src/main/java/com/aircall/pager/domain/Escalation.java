package com.aircall.pager.domain;

import java.util.List;

public class Escalation {

    private final String id;
    private final String serviceId;
    private final List<Level> levels;

    public Escalation(String id, String serviceId, List<Level> levels) {
        this.id = id;
        this.serviceId = serviceId;
        this.levels = levels;
    }

    public Level getFirstLevel(){
        return levels.get(0);
    }

    public Level getNextLevel(String levelId){

        int i = 0;
        while(i < levels.size() - 1 && !levelId.equals(levels.get(i).getLevelId())){
            i++;
        }

        //if there is no next level, it returns the last level
        return i < levels.size() - 1 ? levels.get(i+1)  : levels.get(levels.size()-1);

    }

    @Override
    public String toString() {
        return "Escalation{" +
                "id='" + id + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", levels=" + levels +
                '}';
    }
}
