package com.aircall.pager.application.service;

import com.aircall.pager.application.port.helpers.NotificationsHelperPort;
import com.aircall.pager.application.port.out.MailPort;
import com.aircall.pager.application.port.out.SMSPort;
import com.aircall.pager.domain.Alert;
import com.aircall.pager.domain.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class NotificationsHelperTest {

    private MailPort mailPort;
    private SMSPort smsPort;
    private NotificationsHelperPort service;

    @BeforeEach
    void prepareTest(){
        mailPort = mock(MailPort.class);
        smsPort = mock(SMSPort.class);
        service = new NotificationsHelper(mailPort,smsPort);
    }

    @Test
    void sendNotifications_Should_SendEmailToEachTarget_When_CalledWithMailTargets(){

        Set<Target> targets = new HashSet<>();
        targets.add(Target.newMailTarget("1","aaa@bbb.com"));
        targets.add(Target.newMailTarget("2","ccc@bbb.com"));

        Alert alert = new Alert("1","service1","something happened");

        assertDoesNotThrow(() -> service.sendNotifications(alert.getMessage(), targets));

        verify(mailPort, times(1)).sendEmail("aaa@bbb.com","something happened");
        verify(mailPort, times(1)).sendEmail("ccc@bbb.com","something happened");
        verify(smsPort, times(0)).sendSMS(any(),any());
    }

    @Test
    void sendNotifications_Should_SendSMSToEachTarget_When_CalledWithSMSTargets(){

        Set<Target> targets = new HashSet<>();

        targets.add(Target.newSMSTarget("1","123456677"));
        targets.add(Target.newSMSTarget("2","123456679"));

        Alert alert = new Alert("1","service1","something happened");

        assertDoesNotThrow(() -> service.sendNotifications(alert.getMessage(), targets));

        verify(smsPort, times(1)).sendSMS("123456677","something happened");
        verify(smsPort, times(1)).sendSMS("123456679","something happened");
        verify(mailPort, times(0)).sendEmail(any(),any());
    }


}
