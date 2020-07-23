package com.aircall.pager.application.port.in;

import com.aircall.pager.common.ValidationException;

public interface HealthyServiceEventUseCase {

    void setServiceAsHealthy(HealthyServiceEventCommand command);

    class HealthyServiceEventCommand {

        private final String serviceId;

        public HealthyServiceEventCommand(String serviceId) {
            this.serviceId = serviceId;
            this.validateSelf();
        }

        public String getServiceId() {
            return serviceId;
        }

        private void validateSelf() {
            if(serviceId == null || serviceId.isEmpty())
                throw new ValidationException("invalid fields");
        }
    }

}
