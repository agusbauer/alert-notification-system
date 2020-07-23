package com.aircall.pager.common;

public class LockedResourceException extends RuntimeException {

    public LockedResourceException(String resourceId) {
        super(String.format("Resource with id %s is locked", resourceId));
    }
}
