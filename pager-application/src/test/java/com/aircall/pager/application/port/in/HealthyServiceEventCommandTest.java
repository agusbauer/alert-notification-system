package com.aircall.pager.application.port.in;

import com.aircall.pager.common.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HealthyServiceEventCommandTest {

    @Test
    void validateSelf_Should_doNothing_When_validationsAreOkay(){

        assertDoesNotThrow(() -> new HealthyServiceEventUseCase.HealthyServiceEventCommand("1"));
    }

    @Test
    void validateSelf_Should_ThrowValidationException_When_ServiceIdIsNull(){

        assertThrows(ValidationException.class,() -> new HealthyServiceEventUseCase.HealthyServiceEventCommand(null));
    }
}
