package com.authorization.utils;

import java.util.UUID;

public class UUIDUtility {

    public static String generateUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
