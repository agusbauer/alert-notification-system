package com.aircall.pager.application.service;

import com.aircall.pager.application.port.helpers.NotificationsHelperPort;
import com.aircall.pager.application.port.in.ProcessAckTimeoutUseCase.AckTimeoutCommand;
import com.aircall.pager.application.port.out.*;
import com.aircall.pager.common.AlertNotFoundException;
import com.aircall.pager.common.EscalationNotFoundException;
import com.aircall.pager.common.MonitoredServiceNotFoundException;
import com.aircall.pager.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ProcessAckTimeoutServiceTest {

    private AlertRepository alertRepository;
    private MonitoredServiceRepository monitoredServiceRepository;
    private NotificationsHelperPort notificationsHelper;
    private ProcessAckTimeoutService service;
    private TimerPort timer;
    private LockPort lockPort;
    private EscalationPolicyRepository escalationPolicyRepository;

    @BeforeEach
    void prepareTest(){

        alertRepository = mock(AlertRepository.class);
        monitoredServiceRepository =  mock(MonitoredServiceRepository.class);
        notificationsHelper =  mock(NotificationsHelperPort.class);
        timer = mock(TimerPort.class);
        lockPort = mock(LockPort.class);
        escalationPolicyRepository = mock(EscalationPolicyRepository.class);
        service = new ProcessAckTimeoutService(alertRepository,monitoredServiceRepository, notificationsHelper, timer, escalationPolicyRepository, lockPort);
    }

    @Test
    void processAckTimeout_Should_NotifyAndSetAckTimeout_When_AlertIsNotAckAndServiceIsUnhealthy(){

        String serviceId = "service1";
        String alertId = "1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        monitoredService.setAsUnhealthy();
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));

        Set<Target> targets = new HashSet<>();
        List<Level> levels = new LinkedList<>();
        levels.add(new Level("level1", targets));
        Escalation escalation = new Escalation("1111",serviceId,levels);
        when(escalationPolicyRepository.getEscalationByServiceId(serviceId)).thenReturn(Optional.of(escalation));

        Alert alert = new Alert(alertId,serviceId, "new Alert");
        when(alertRepository.getById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(alert)).thenReturn(true);

        AckTimeoutCommand command = new AckTimeoutCommand(alertId);

        assertDoesNotThrow(() -> service.processAckTimeout(command));
        verify(notificationsHelper, times(1)).sendNotifications(any(), any());
        verify(timer, times(1)).scheduleTimer(any(),any(),any());
    }

    @Test
    void processAckTimeout_Should_DoNothing_When_ServiceIsHealthy(){

        String serviceId = "service1";
        String alertId = "1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));

        Alert alert = new Alert(alertId,serviceId, "new Alert");
        when(alertRepository.getById(alertId)).thenReturn(Optional.of(alert));

        AckTimeoutCommand command = new AckTimeoutCommand(alertId);

        assertDoesNotThrow(() -> service.processAckTimeout(command));
        verify(monitoredServiceRepository, times(1)).getById(serviceId);
        verify(notificationsHelper, times(0)).sendNotifications(any(), any());
        verify(timer, times(0)).scheduleTimer(any(),any(),any());
    }

    @Test
    void processAckTimeout_Should_DoNothing_When_ServiceIsUnhealthyAndAlertIsAck(){

        String serviceId = "service1";
        String alertId = "1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        monitoredService.setAsUnhealthy();
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));

        Alert alert = new Alert(alertId,serviceId, "new Alert");
        alert.acknowledge();
        when(alertRepository.getById(alertId)).thenReturn(Optional.of(alert));

        AckTimeoutCommand command = new AckTimeoutCommand(alertId);

        assertDoesNotThrow(() -> service.processAckTimeout(command));
        verify(notificationsHelper, times(0)).sendNotifications(any(), any());
        verify(timer, times(0)).scheduleTimer(any(),any(),any());
    }

    @Test
    void processAckTimeout_Should_ThrowAlertNotFoundException_When_AlertIsNotFound(){

        String serviceId = "service1";
        String alertId = "1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        monitoredService.setAsUnhealthy();
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));

        when(alertRepository.getById(alertId)).thenReturn(Optional.empty());

        AckTimeoutCommand command = new AckTimeoutCommand(alertId);

        assertThrows(AlertNotFoundException.class,() -> service.processAckTimeout(command));
        verify(notificationsHelper, times(0)).sendNotifications(any(), any());
    }

    @Test
    void processAckTimeout_Should_ThrowServiceNotFoundException_When_ServiceIsNotFound(){

        String serviceId = "service1";
        String alertId = "1";

        Alert alert = new Alert(alertId,serviceId, "new Alert");
        when(alertRepository.getById(alertId)).thenReturn(Optional.of(alert));
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.empty());

        AckTimeoutCommand command = new AckTimeoutCommand(alertId);

        assertThrows(MonitoredServiceNotFoundException.class,() -> service.processAckTimeout(command));
        verify(notificationsHelper, times(0)).sendNotifications(any(), any());
    }

    @Test
    void processAckTimeout_Should_ThrowEscalationNotFoundException_When_EscalationIsNotFound(){

        String serviceId = "service1";
        String alertId = "1";

        Alert alert = new Alert(alertId,serviceId, "new Alert");
        when(alertRepository.getById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(alert)).thenReturn(true);

        when(escalationPolicyRepository.getEscalationByServiceId(serviceId)).thenReturn(Optional.empty());

        MonitoredService monitoredService = new MonitoredService(serviceId);
        monitoredService.setAsUnhealthy();
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));

        AckTimeoutCommand command = new AckTimeoutCommand(alertId);

        assertThrows(EscalationNotFoundException.class,() -> service.processAckTimeout(command));
        verify(notificationsHelper, times(0)).sendNotifications(any(), any());
    }

    @Test
    void processAckTimeout_Should_DoNothing_When_RepositoryHasAnOpLockingFailure(){

        String serviceId = "service1";
        String alertId = "1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        monitoredService.setAsUnhealthy();
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));

        Set<Target> targets = new HashSet<>();
        List<Level> levels = new LinkedList<>();
        levels.add(new Level("level1", targets));
        Escalation escalation = new Escalation("1111",serviceId,levels);
        when(escalationPolicyRepository.getEscalationByServiceId(serviceId)).thenReturn(Optional.of(escalation));

        Alert alert = new Alert(alertId,serviceId, "new Alert");
        when(alertRepository.getById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(alert)).thenReturn(false);

        AckTimeoutCommand command = new AckTimeoutCommand(alertId);

        assertDoesNotThrow(() -> service.processAckTimeout(command));
        verify(notificationsHelper, times(0)).sendNotifications(any(), any());
        verify(timer, times(0)).scheduleTimer(any(),any(),any());
    }
}
