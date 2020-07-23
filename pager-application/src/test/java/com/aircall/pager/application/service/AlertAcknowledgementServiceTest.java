package com.aircall.pager.application.service;

import com.aircall.pager.application.port.in.AlertAcknowledgementUseCase.AlertAcknowledgementCommand;
import com.aircall.pager.application.port.out.AlertRepository;
import com.aircall.pager.application.port.out.LockPort;
import com.aircall.pager.common.AlertNotFoundException;
import com.aircall.pager.common.LockedResourceException;
import com.aircall.pager.domain.Alert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlertAcknowledgementServiceTest {

    private AlertRepository alertRepository;
    private LockPort lockPort;
    private AlertAcknowledgementService service;

    @BeforeEach
    void prepareTest(){
        alertRepository = mock(AlertRepository.class);
        lockPort = mock(LockPort.class);
        service = new AlertAcknowledgementService(alertRepository, lockPort);
    }

    @Test
    void acknowledgeAlert_Should_SetAlertAsAck_When_AlertIsPresent(){

        String alertId = "1";

        Alert alert = new Alert(alertId,"xxxx","This is an alert");
        when(alertRepository.getById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(alert)).thenReturn(true);

        AlertAcknowledgementCommand command = new AlertAcknowledgementCommand(alertId);

        assertDoesNotThrow(() -> service.acknowledgeAlarm(command));
        assertTrue(alert.isAcknowledge());
        verify(alertRepository, times(1)).getById(alert.getId());
        verify(alertRepository, times(1)).save(alert);
    }

    @Test
    void acknowledgeAlert_Should_ThrowAlertNotFoundException_When_AlertIsNotPresent(){

        String alertId = "1";
        when(alertRepository.getById(alertId)).thenReturn(Optional.empty());

        AlertAcknowledgementCommand command = new AlertAcknowledgementCommand(alertId);

        assertThrows(AlertNotFoundException.class,() -> service.acknowledgeAlarm(command));
    }

    @Test
    void acknowledgeAlert_Should_ThrowAlertLockedResourceException_When_RepositoryHasAnOpLockingFailure(){

        String alertId = "1";
        Alert alert = new Alert(alertId,"xxxx","This is an alert");
        when(alertRepository.getById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(alert)).thenReturn(false);

        AlertAcknowledgementCommand command = new AlertAcknowledgementCommand(alertId);

        assertThrows(LockedResourceException.class,() -> service.acknowledgeAlarm(command));
    }

    @Test
    void acknowledgeAlert_Should_UnlockResource_When_AnExceptionOccurs(){

        String alertId = "1";
        Alert alert = new Alert(alertId,"xxxx","This is an alert");
        when(alertRepository.getById(alertId)).thenThrow(new RuntimeException());

        AlertAcknowledgementCommand command = new AlertAcknowledgementCommand(alertId);

        assertThrows(RuntimeException.class,() -> service.acknowledgeAlarm(command));
        verify(lockPort, times(1)).lock(alert.getId());
        verify(lockPort, times(1)).unlock(alert.getId());
    }
}
