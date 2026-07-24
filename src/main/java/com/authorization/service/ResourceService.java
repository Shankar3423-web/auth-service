package com.authorization.service;

public interface ResourceService {
    public boolean checkAccess(String accessToken, String service, String resourcePath);
}
