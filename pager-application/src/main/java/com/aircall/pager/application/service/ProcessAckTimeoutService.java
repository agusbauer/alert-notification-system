package com.aircall.pager.application.service;

import com.aircall.pager.application.port.out.*;
import com.aircall.pager.common.AlertNotFoundException;
import com.aircall.pager.common.EscalationNotFoundException;
import com.aircall.pager.common.MonitoredServiceNotFoundException;
import com.aircall.pager.application.port.helpers.NotificationsHelperPort;
import com.aircall.pager.application.port.in.ProcessAckTimeoutUseCase;
import com.aircall.pager.domain.Alert;
import com.aircall.pager.domain.Level;
import com.aircall.pager.domain.MonitoredService;

import static com.aircall.pager.application.service.Properties.ACK_TIMEOUT;
import static com.aircall.pager.application.service.Properties.ALERT_RESOURCE_TYPE;

public class ProcessAckTimeoutService implements ProcessAckTimeoutUseCase {

    private final AlertRepository alertRepository;
    private final MonitoredServiceRepository monitoredServiceRepository;
    private final NotificationsHelperPort notificationsHelper;
    private final TimerPort timerPort;
    private final EscalationPolicyRepository escalationPolicyRepository;
    private final LockPort lockPort;

    public ProcessAckTimeoutService(AlertRepository alertRepository, MonitoredServiceRepository monitoredServiceRepository, NotificationsHelperPort notificationsHelper, TimerPort timerPort, EscalationPolicyRepository escalationPolicyRepository, LockPort lockPort) {
        this.alertRepository = alertRepository;
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.notificationsHelper = notificationsHelper;
        this.timerPort = timerPort;
        this.escalationPolicyRepository = escalationPolicyRepository;
        this.lockPort = lockPort;
    }

    @Override
    public void processAckTimeout(AckTimeoutCommand command) {

        String alertId = command.getAlertId();
        Alert alert;

        lockPort.lock(alertId);
        try{
            alert = alertRepository.getById(alertId)
                    .orElseThrow(() -> new AlertNotFoundException(alertId));

            if(alert.isAcknowledge())
                return;
        }
        finally {
            lockPort.unlock(alertId);
        }

        String serviceId = alert.getServiceID();

        lockPort.lock(serviceId);
        try{
            MonitoredService service =  monitoredServiceRepository
                    .getById(serviceId)
                    .orElseThrow(() -> new MonitoredServiceNotFoundException(serviceId));

            if(service.isHealthy())
                return;
        }
        finally {
            lockPort.unlock(serviceId);
        }

        Level levelToNotify = escalationPolicyRepository
                .getEscalationByServiceId(serviceId)
                .orElseThrow(() -> new EscalationNotFoundException(serviceId))
                .getNextLevel(alert.getLastNotifiedLevelId());

        alert.setLastNotifiedLevelId(levelToNotify.getLevelId());

        //false in case of Optimistic Locking. To avoid duplicated alert timeout notifications
        if(!alertRepository.save(alert))
            return;

        notificationsHelper.sendNotifications(alert.getMessage(),levelToNotify.getTargets());

        timerPort.scheduleTimer(alert.getId(),ALERT_RESOURCE_TYPE, ACK_TIMEOUT);
    }

}
