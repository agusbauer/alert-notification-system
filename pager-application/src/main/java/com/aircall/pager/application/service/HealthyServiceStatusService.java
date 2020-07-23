package com.aircall.pager.application.service;

import com.aircall.pager.application.port.out.LockPort;
import com.aircall.pager.common.LockedResourceException;
import com.aircall.pager.common.MonitoredServiceNotFoundException;
import com.aircall.pager.application.port.in.HealthyServiceEventUseCase;
import com.aircall.pager.application.port.out.MonitoredServiceRepository;
import com.aircall.pager.domain.MonitoredService;

public class HealthyServiceStatusService implements HealthyServiceEventUseCase {

    private final MonitoredServiceRepository monitoredServiceRepository;
    private final LockPort lockPort;

    public HealthyServiceStatusService(MonitoredServiceRepository monitoredServiceRepository, LockPort lockPort) {
        this.monitoredServiceRepository = monitoredServiceRepository;
        this.lockPort = lockPort;
    }

    @Override
    public void setServiceAsHealthy(HealthyServiceEventCommand command) {

        String serviceId = command.getServiceId();

        lockPort.lock(serviceId);
        try{
            MonitoredService monitoredService = monitoredServiceRepository
                    .getById(command.getServiceId())
                    .orElseThrow(() -> new MonitoredServiceNotFoundException(command.getServiceId()));

            monitoredService.setAsHealthy();

            if(!monitoredServiceRepository.save(monitoredService))
                throw new LockedResourceException(serviceId);

        }finally {
            lockPort.unlock(serviceId);
        }
    }
}
