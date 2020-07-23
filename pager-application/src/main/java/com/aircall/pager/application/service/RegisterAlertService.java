package com.aircall.pager.application.service;

import com.aircall.pager.common.EscalationNotFoundException;
import com.aircall.pager.common.MonitoredServiceNotFoundException;
import com.aircall.pager.application.port.helpers.NotificationsHelperPort;
import com.aircall.pager.application.port.in.RegisterAlertUseCase;
import com.aircall.pager.application.port.out.AlertRepository;
import com.aircall.pager.application.port.out.EscalationPolicyRepository;
import com.aircall.pager.application.port.out.MonitoredServiceRepository;
import com.aircall.pager.application.port.out.TimerPort;
import com.aircall.pager.domain.Alert;
import com.aircall.pager.domain.Level;
import com.aircall.pager.domain.MonitoredService;

import java.util.UUID;

import static com.aircall.pager.application.service.Properties.ACK_TIMEOUT;
import static com.aircall.pager.application.service.Properties.ALERT_RESOURCE_TYPE;

public class RegisterAlertService implements RegisterAlertUseCase {

    private final MonitoredServiceRepository monitoredServiceRepository;
    private final AlertRepository alertRepository;
    private final TimerPort timerPort;
    private final NotificationsHelperPort notificationsHelper;
    private final EscalationPolicyRepository escalationPolicyRepository;

    public RegisterAlertService(MonitoredServiceRepository monitoredServiceRepository, NotificationsHelperPort notificationsHelper, AlertRepository alertRepository, TimerPort timerPort, EscalationPolicyRepository escalationPolicyRepository) {
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.notificationsHelper = notificationsHelper;
        this.alertRepository = alertRepository;
        this.timerPort = timerPort;
        this.escalationPolicyRepository = escalationPolicyRepository;
    }

    public void registerAlert(RegisterAlertCommand command) {

        String serviceId = command.getServiceId();

        MonitoredService monitoredService = monitoredServiceRepository
                .getById(serviceId)
                .orElseThrow(() -> new MonitoredServiceNotFoundException(serviceId));

        if(!monitoredService.isHealthy()){
            return;
        }

        monitoredService.setAsUnhealthy();

        //Return in case of optimistic locking failure to avoid duplicated alerts
        //If the same of different alerts from the same service come up at the same time i will only register one
        if(!monitoredServiceRepository.save(monitoredService))
            return;

        Alert alert = Alert.AlertFromCommand(UUID.randomUUID().toString(),command);

        Level levelToNotify = escalationPolicyRepository
                .getEscalationByServiceId(alert.getServiceID())
                .orElseThrow(() -> new EscalationNotFoundException(serviceId))
                .getFirstLevel();

        alert.setLastNotifiedLevelId(levelToNotify.getLevelId());
        alertRepository.save(alert);

        notificationsHelper.sendNotifications(alert.getMessage(), levelToNotify.getTargets());

        timerPort.scheduleTimer(alert.getId(),ALERT_RESOURCE_TYPE, ACK_TIMEOUT);
    }
}
