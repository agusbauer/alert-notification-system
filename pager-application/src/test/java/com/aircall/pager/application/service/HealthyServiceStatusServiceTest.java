package com.aircall.pager.application.service;

import com.aircall.pager.application.port.in.HealthyServiceEventUseCase.HealthyServiceEventCommand;
import com.aircall.pager.application.port.out.LockPort;
import com.aircall.pager.application.port.out.MonitoredServiceRepository;
import com.aircall.pager.common.LockedResourceException;
import com.aircall.pager.common.MonitoredServiceNotFoundException;
import com.aircall.pager.domain.MonitoredService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class HealthyServiceStatusServiceTest {

    private MonitoredServiceRepository monitoredServiceRepository;
    private HealthyServiceStatusService service;
    private LockPort lockPort;

    @BeforeEach
    void prepareTest(){
        monitoredServiceRepository = mock(MonitoredServiceRepository.class);
        lockPort = mock(LockPort.class);
        service = new HealthyServiceStatusService(monitoredServiceRepository, lockPort);
    }

    @Test
    void setServiceAsHealthy_Should_SetServiceAsHealthy_When_ServiceIsPresent(){

        String serviceId = "service1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));
        when(monitoredServiceRepository.save(monitoredService)).thenReturn(true);

        HealthyServiceEventCommand command = new HealthyServiceEventCommand(serviceId);

        assertDoesNotThrow(() -> service.setServiceAsHealthy(command));
        assertTrue(monitoredService.isHealthy());
        verify(monitoredServiceRepository, times(1)).getById(serviceId);
        verify(monitoredServiceRepository, times(1)).save(monitoredService);
    }

    @Test
    void setServiceAsHealthy_Should_ThrowServiceNotFoundException_When_ServiceIsNotFound(){

        String serviceId = "service1";

        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.empty());

        HealthyServiceEventCommand command = new HealthyServiceEventCommand(serviceId);

        assertThrows(MonitoredServiceNotFoundException.class,() -> service.setServiceAsHealthy(command));
        verify(monitoredServiceRepository, times(1)).getById(serviceId);
        verify(monitoredServiceRepository, times(0)).save(any());
    }

    @Test
    void setServiceAsHealthy_Should_ThrowLockedResourceException_When_RepositoryHasAnOpLockingFailure(){

        String serviceId = "service1";

        MonitoredService monitoredService = new MonitoredService(serviceId);
        when(monitoredServiceRepository.getById(serviceId)).thenReturn(Optional.of(monitoredService));

        HealthyServiceEventCommand command = new HealthyServiceEventCommand(serviceId);

        assertThrows(LockedResourceException.class,() -> service.setServiceAsHealthy(command));
        verify(monitoredServiceRepository, times(1)).getById(serviceId);
    }

    @Test
    void setServiceAsHealthy_Should_UnlockResource_When_AnExceptionOccurs(){

        String serviceId = "service1";

        when(monitoredServiceRepository.getById(serviceId)).thenThrow(new RuntimeException());
        HealthyServiceEventCommand command = new HealthyServiceEventCommand(serviceId);

        assertThrows(RuntimeException.class,() -> service.setServiceAsHealthy(command));
        verify(lockPort, times(1)).lock(serviceId);
        verify(lockPort, times(1)).unlock(serviceId);
    }
}
