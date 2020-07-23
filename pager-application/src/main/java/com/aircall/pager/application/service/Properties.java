package com.aircall.pager.application.service;

import java.util.concurrent.TimeUnit;

public class Properties {

    public static long ACK_TIMEOUT = TimeUnit.MINUTES.toMillis(15l);
    public static String ALERT_RESOURCE_TYPE = "Alert";
}
