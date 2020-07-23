package com.aircall.pager.application.port.out;

import com.aircall.pager.domain.Alert;

import java.util.Optional;

public interface AlertRepository {

    boolean save(Alert alert);

    Optional<Alert> getById(String id);
}
