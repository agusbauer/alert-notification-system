package com.aircall.pager.application.port.in;

import com.aircall.pager.common.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegisterAlertCommandTest {

    @Test
    void validateSelf_Should_doNothing_When_validationsAreOkay(){

        assertDoesNotThrow(() -> new RegisterAlertUseCase.RegisterAlertCommand("1","new alert"));
    }

    @Test
    void validateSelf_Should_ThrowValidationException_When_ServiceIdIsNull(){

        assertThrows(ValidationException.class,() -> new RegisterAlertUseCase.RegisterAlertCommand(null, "new alert"));
    }

    @Test
    void validateSelf_Should_ThrowValidationException_When_MessageIsNull(){

        assertThrows(ValidationException.class,() -> new RegisterAlertUseCase.RegisterAlertCommand("1", null));
    }
}
