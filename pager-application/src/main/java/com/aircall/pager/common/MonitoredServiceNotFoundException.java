package com.aircall.pager.common;

public class MonitoredServiceNotFoundException extends RuntimeException {

    public MonitoredServiceNotFoundException(String serviceId) {
        super(String.format("Not found monitored service with id: %s",serviceId));
    }
}
