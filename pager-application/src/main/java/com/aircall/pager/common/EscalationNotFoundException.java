package com.aircall.pager.common;

public class EscalationNotFoundException extends RuntimeException{

    public EscalationNotFoundException(String serviceId) {
        super(String.format("Not found escalation with service_id: %s", serviceId));
    }

}
