package com.aircall.pager.application.port.out;

public interface LockPort {

    //in the adapter implementation this could have a TTL of ~300ms
    void lock(String resourceId);

    void unlock(String resourceId);
}
