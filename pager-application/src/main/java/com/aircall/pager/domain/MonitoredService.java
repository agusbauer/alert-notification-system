package com.aircall.pager.domain;

public class MonitoredService {

    private String id;
    private STATUS status;

    public enum STATUS {HEALTHY, UNHEALTHY}

    public MonitoredService(String id) {
        this.id = id;
        this.status = STATUS.HEALTHY;
    }

    public String getId() {
        return id;
    }

    public boolean isHealthy() {
        return this.status ==  STATUS.HEALTHY;
    }

    public void setAsHealthy(){
        this.status =  STATUS.HEALTHY;
    }

    public void setAsUnhealthy(){
        this.status =  STATUS.UNHEALTHY;
    }

    @Override
    public String toString() {
        return "MonitoredService{" +
                "id='" + id + '\'' +
                ", status=" + status +
                '}';
    }
}
