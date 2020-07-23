package com.aircall.pager.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MonitoredServiceTest {

    @Test
    void setAsUnHealthy_Should_ChangeStatusToUnHealthy_When_ServiceIsHealthy(){
        MonitoredService service = new MonitoredService("1");
        assertTrue(service.isHealthy());

        service.setAsUnhealthy();
        assertFalse(service.isHealthy());
    }

    @Test
    void setAsHealthy_Should_ChangeStatusToHealthy_When_ServiceIsUnhealthy(){
        MonitoredService service = new MonitoredService("1");
        service.setAsUnhealthy();
        assertFalse(service.isHealthy());

        service.setAsHealthy();
        assertTrue(service.isHealthy());
    }
}
