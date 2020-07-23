package com.aircall.pager.application.port.out;

import com.aircall.pager.domain.Escalation;

import java.util.Optional;

public interface EscalationPolicyRepository {

    Optional<Escalation> getEscalationByServiceId(String serviceId);
}
