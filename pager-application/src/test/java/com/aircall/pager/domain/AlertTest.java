package com.aircall.pager.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlertTest {

    @Test
    void acknowledge_Should_ChangeAlertStateToAcknowledge_Always(){

        Alert alert = new Alert("1","2","alert");
        assertFalse(alert.isAcknowledge());

        alert.acknowledge();
        assertTrue(alert.isAcknowledge());
    }
}
