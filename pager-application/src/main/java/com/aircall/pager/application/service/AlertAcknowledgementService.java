package com.aircall.pager.application.service;

import com.aircall.pager.application.port.in.AlertAcknowledgementUseCase;
import com.aircall.pager.application.port.out.AlertRepository;
import com.aircall.pager.application.port.out.LockPort;
import com.aircall.pager.common.AlertNotFoundException;
import com.aircall.pager.common.LockedResourceException;
import com.aircall.pager.domain.Alert;

public class AlertAcknowledgementService implements AlertAcknowledgementUseCase {

    private final AlertRepository alertRepository;
    private final LockPort lockPort;

    public AlertAcknowledgementService(AlertRepository alertRepository, LockPort lockPort) {
        this.alertRepository = alertRepository;
        this.lockPort = lockPort;
    }

    @Override
    public void acknowledgeAlarm(AlertAcknowledgementCommand command) {

        String alertId = command.getAlertId();

        lockPort.lock(alertId);
        try{
            Alert alert = alertRepository
                .getById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));

            alert.acknowledge();
            if(!alertRepository.save(alert))
                throw new LockedResourceException(alertId);
        }
        finally {
            lockPort.unlock(alertId);
        }

    }
}
