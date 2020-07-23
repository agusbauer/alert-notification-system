package com.aircall.pager.application.port.in;

import com.aircall.pager.common.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AlertAcknowledgementCommandTest {

    @Test
    void validateSelf_Should_doNothing_When_validationsAreOkay(){

        assertDoesNotThrow(() -> new AlertAcknowledgementUseCase.AlertAcknowledgementCommand("1"));
    }

    @Test
    void validateSelf_Should_ThrowValidationException_When_AlertIdIsNull(){

        assertThrows(ValidationException.class,() -> new AlertAcknowledgementUseCase.AlertAcknowledgementCommand(null));
    }
}
