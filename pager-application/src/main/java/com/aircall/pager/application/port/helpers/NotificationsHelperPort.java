package com.aircall.pager.application.port.helpers;

import com.aircall.pager.domain.Target;

import java.util.Set;

public interface NotificationsHelperPort {

    void sendNotifications(String message, Set<Target> targetsToNotify);
}
