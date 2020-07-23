package com.aircall.pager.application.port.in;

import com.aircall.pager.common.ValidationException;

public interface RegisterAlertUseCase {

    void registerAlert(RegisterAlertCommand registerAlertCommand);

    class RegisterAlertCommand {

        private final String serviceId;
        private final String message;

        public RegisterAlertCommand(String serviceId, String message) {
            this.serviceId = serviceId;
            this.message = message;
            this.validateSelf();
        }

        private void validateSelf() {
            if(serviceId == null || message == null)
                throw new ValidationException("invalid fields");
        }

        public String getServiceId() {
            return serviceId;
        }

        public String getMessage() {
            return message;
        }
    }

}
