package com.aircall.pager.application.port.out;

public interface MailPort {

    void sendEmail(String emailAddress, String message);
}
