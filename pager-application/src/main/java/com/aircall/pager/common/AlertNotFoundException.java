package com.aircall.pager.common;

public class AlertNotFoundException extends RuntimeException {

    public AlertNotFoundException(String alertId) {
        super(String.format("Not found alert with id: %s", alertId));
    }

}
