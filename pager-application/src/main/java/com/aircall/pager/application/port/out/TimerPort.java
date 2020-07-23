package com.aircall.pager.application.port.out;

public interface TimerPort {

    void scheduleTimer(String resourceId, String resourceType, Long timeInMillis);

}
