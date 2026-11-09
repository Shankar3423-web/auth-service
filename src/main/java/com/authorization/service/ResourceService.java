package com.authorization.service;

public interface ResourceService {
    public String checkAccess(String accessToken, String service, String resourcePath);
}
