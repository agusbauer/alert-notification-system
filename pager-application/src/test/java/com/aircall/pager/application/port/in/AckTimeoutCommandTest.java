package com.aircall.pager.application.port.in;

import com.aircall.pager.common.ValidationException;
import org.junit.jupiter.api.Test;
import com.aircall.pager.application.port.in.ProcessAckTimeoutUseCase.AckTimeoutCommand;

import static org.junit.jupiter.api.Assertions.*;

public class AckTimeoutCommandTest {

    @Test
    void validateSelf_Should_doNothing_When_validationsAreOkay(){

        assertDoesNotThrow(() -> new AckTimeoutCommand("1"));
    }

    @Test
    void validateSelf_Should_ThrowValidationException_When_AlertIdIsNull(){

        assertThrows(ValidationException.class,() -> new AckTimeoutCommand(null));
    }
}
