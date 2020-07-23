package com.aircall.pager.application.port.out;

public interface SMSPort {

    void sendSMS(String phoneNumber, String message);
}
