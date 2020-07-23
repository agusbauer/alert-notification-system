package com.aircall.pager.application.port.in;

import com.aircall.pager.common.ValidationException;

public interface ProcessAckTimeoutUseCase {

    void processAckTimeout(AckTimeoutCommand command);

    class AckTimeoutCommand {

        private final String alertId;

        public AckTimeoutCommand(String alertId) {
            this.alertId = alertId;
            this.validateSelf();
        }

        private void validateSelf() {
            if(alertId == null || alertId.isEmpty())
                throw new ValidationException("invalid fields");
        }

        public String getAlertId() {
            return alertId;
        }

    }
}
