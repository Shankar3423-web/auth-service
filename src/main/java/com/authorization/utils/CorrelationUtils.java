package com.authorization.utils;

import com.authorization.constants.CorrelationConstants;

import java.util.UUID;

public class CorrelationUtils {
    public static String getCorrelationID() {
        String threadName = Thread.currentThread().getName();
        return (threadName != null && threadName.contains(CorrelationConstants.CIDPrefix)
                && threadName.length() >= CorrelationConstants.CIDLength) ? threadName
                : CorrelationConstants.CIDPrefix + UUID.randomUUID().toString();
    }
}