package com.aircall.pager.application.service;

import com.aircall.pager.application.port.helpers.NotificationsHelperPort;
import com.aircall.pager.application.port.out.MailPort;
import com.aircall.pager.application.port.out.SMSPort;
import com.aircall.pager.domain.Target;
import java.util.Set;

public class NotificationsHelper implements NotificationsHelperPort {

    private final MailPort mailPort;
    private final SMSPort smsPort;

    public NotificationsHelper(MailPort mailPort, SMSPort smsPort) {
        this.mailPort = mailPort;
        this.smsPort = smsPort;
    }

    public void sendNotifications(String message, Set<Target> targetsToNotify){

        for (Target t : targetsToNotify){

            switch (t.getType()){
                case SMS:
                    smsPort.sendSMS(t.getPhoneNumber(),message);
                    break;
                case EMAIL:
                    mailPort.sendEmail(t.getEmail(),message);
                    break;
            }

        }
    }
}
