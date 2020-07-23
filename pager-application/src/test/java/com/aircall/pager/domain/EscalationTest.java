package com.aircall.pager.domain;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EscalationTest {

    @Test
    void getFirstLevel_Should_GetFirstLevel_Always(){

        List<Level> levels = new LinkedList<>();
        for(int i = 1; i < 4; i++){
            levels.add(new Level(String.valueOf(i),new HashSet<>()));
        }

        Escalation escalation = new Escalation("1111","service1",levels);
        Level level = escalation.getFirstLevel();

        assertEquals("1", level.getLevelId());
    }

    @Test
    void getNextLevel_Should_GetCorrectNextLevel_When_CalledWithAValidParameter(){

        List<Level> levels = new LinkedList<>();
        for(int i = 1; i < 4; i++){
            levels.add(new Level(String.valueOf(i),new HashSet<>()));
        }

        Escalation escalation = new Escalation("1111","service1",levels);
        Level nextLevel = escalation.getNextLevel("1");

        assertEquals("2", nextLevel.getLevelId());

        nextLevel = escalation.getNextLevel("2");
        assertEquals("3", nextLevel.getLevelId());
    }

}
