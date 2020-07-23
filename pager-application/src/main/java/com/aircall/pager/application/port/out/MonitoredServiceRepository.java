package com.aircall.pager.application.port.out;

import com.aircall.pager.domain.MonitoredService;

import java.util.Optional;

public interface MonitoredServiceRepository {

    boolean save(MonitoredService alert);
    Optional<MonitoredService> getById(String id);
}
