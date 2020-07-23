package com.aircall.pager.domain;

import com.aircall.pager.application.port.in.RegisterAlertUseCase;

public class Alert {

    private String id;
    private String serviceId;
    private String message;
    private STATUS status;
    private String lastNotifiedLevelId;

    public enum STATUS {OPEN, ACKNOWLEDGE}

    public static Alert AlertFromCommand(String id, RegisterAlertUseCase.RegisterAlertCommand alertCommand){
        return new Alert(id,alertCommand.getServiceId(),alertCommand.getMessage());
    }

    public Alert(String id, String serviceID, String message) {
        this.id = id;
        this.status = STATUS.OPEN;
        this.serviceId = serviceID;
        this.message = message;
    }

    public String getId(){
        return this.id;
    }

    public String getServiceID() {
        return serviceId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAcknowledge() {
        return this.status == STATUS.ACKNOWLEDGE;
    }

    public String getLastNotifiedLevelId() {
        return lastNotifiedLevelId;
    }

    public void setLastNotifiedLevelId(String lastNotifiedLevelId) {
        this.lastNotifiedLevelId = lastNotifiedLevelId;
    }

    public void acknowledge(){
        this.status = STATUS.ACKNOWLEDGE;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "serviceID='" + serviceId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
