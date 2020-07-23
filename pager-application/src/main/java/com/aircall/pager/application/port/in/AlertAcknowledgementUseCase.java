package com.aircall.pager.application.port.in;

import com.aircall.pager.common.ValidationException;

public interface AlertAcknowledgementUseCase {

    void acknowledgeAlarm(AlertAcknowledgementCommand command);

    class AlertAcknowledgementCommand {

        private final String alertId;

        public AlertAcknowledgementCommand(String alertId) {
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
