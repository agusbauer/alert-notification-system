package com.aircall.pager.application.service;

import com.aircall.pager.common.EscalationNotFoundException;
import com.aircall.pager.common.MonitoredServiceNotFoundException;
import com.aircall.pager.application.port.in.RegisterAlertUseCase.RegisterAlertCommand;
import com.aircall.pager.application.port.out.AlertRepository;
import com.aircall.pager.application.port.out.EscalationPolicyRepository;
import com.aircall.pager.application.port.out.MonitoredServiceRepository;
import com.aircall.pager.application.port.out.TimerPort;
import com.aircall.pager.domain.Escalation;
import com.aircall.pager.domain.Level;
import com.aircall.pager.domain.MonitoredService;
import com.aircall.pager.domain.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class RegisterAlertServiceTest {

    private MonitoredServiceRepository monitoredServiceRepository;
    private NotificationsHelper notificationsHelper;
    private AlertRepository alertRepository;
    private TimerPort timer;
    private RegisterAlertService service;
    private EscalationPolicyRepository escalationPolicyRepository;

    @BeforeEach
    void prepareTest(){

        monitoredServiceRepository =  mock(MonitoredServiceRepository.class);
        notificationsHelper =  mock(NotificationsHelper.class);
        alertRepository = mock(AlertRepository.class);
        timer = mock(TimerPort.class);
        escalationPolicyRepository = mock(EscalationPolicyRepository.class);
        service = new RegisterAlertService(monitoredServiceRepository, notificationsHelper, alertRepository, timer, escalationPolicyRepository);
    }

    @Test
    void registerAlert_Should_SetServiceAsUnhealthyAndProcessAlert_When_ServiceIsHealthy(){

        String serviceId = "service1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));
        when(monitoredServiceRepository.save(monitoredService)).thenReturn(true);

        Set<Target> targets = new HashSet<>();
        List<Level> levels = new LinkedList<>();
        levels.add(new Level("level1", targets));
        Escalation escalation = new Escalation("1111",serviceId,levels);

        when(escalationPolicyRepository.getEscalationByServiceId(serviceId)).thenReturn(Optional.of(escalation));

        RegisterAlertCommand command = new RegisterAlertCommand(serviceId, "An error has ocurred");

        assertDoesNotThrow(() -> service.registerAlert(command));
        assertFalse(monitoredService.isHealthy());
        verify(monitoredServiceRepository, times(1)).save(monitoredService);
        verify(notificationsHelper, times(1)).sendNotifications(any(),eq(targets));
        verify(timer, times(1)).scheduleTimer(any(),any(),any());
    }

    @Test
    void registerAlert_Should_DoNothing_When_ServiceIsUnhealthy(){

        String serviceId = "service1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        monitoredService.setAsUnhealthy();
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));

        RegisterAlertCommand command = new RegisterAlertCommand(serviceId, "An error has ocurred");

        assertDoesNotThrow(() -> service.registerAlert(command));
        assertFalse(monitoredService.isHealthy());
        verify(monitoredServiceRepository, times(0)).save(monitoredService);
        verify(notificationsHelper, times(0)).sendNotifications(any(),any());
    }

    @Test
    void registerAlert_Should_DoNothing_When_RepositoryHasAnOpLockingFailure(){

        String serviceId = "service1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));
        when(monitoredServiceRepository.save(monitoredService)).thenReturn(false);

        RegisterAlertCommand command = new RegisterAlertCommand(serviceId, "An error has ocurred");
        assertDoesNotThrow(() -> service.registerAlert(command));
        assertFalse(monitoredService.isHealthy());
        verify(monitoredServiceRepository, times(1)).save(monitoredService);
        verify(notificationsHelper, times(0)).sendNotifications(any(),any());
    }

    @Test
    void registerAlert_Should_throwServiceNotFoundException_When_TheCorrespondingServiceIsNotFound(){

        String serviceId = "service1";

        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.empty());

        RegisterAlertCommand command = new RegisterAlertCommand(serviceId, "An error has ocurred");
        assertThrows(MonitoredServiceNotFoundException.class,() -> service.registerAlert(command));
    }

    @Test
    void registerAlert_Should_throwEscalationNotFoundException_When_TheCorrespondingEscalationIsNotFound(){

        String serviceId = "service1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));
        when(monitoredServiceRepository.save(monitoredService)).thenReturn(true);

        when(escalationPolicyRepository.getEscalationByServiceId(serviceId)).thenReturn(Optional.empty());

        RegisterAlertCommand command = new RegisterAlertCommand(serviceId, "An error has ocurred");
        assertThrows(EscalationNotFoundException.class,() -> service.registerAlert(command));
    }

}
