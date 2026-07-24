package com.authorization.service.impl;

import com.authorization.cache.TokenCache;
import com.authorization.dto.response.AuthResponse;
import com.authorization.entity.Client;
import com.authorization.entity.Resource;
import com.authorization.entity.*;
import com.authorization.exception.AccessCheckFailedException;
import com.authorization.loggers.Loggers;
import com.authorization.dto.AuthSessionDTO;
import com.authorization.repository.*;
import com.authorization.service.ResourceService;
import com.authorization.service.TokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private AuthSessionRepository authSessionRepo;

    @Autowired
    private ClientRepository clientRepo;

    @Autowired
    private ClientRolesRepository clientRolesRepository;

    @Autowired
    private ResourceRepository resourceRepo;

    @Autowired
    private ResourceRolesRepository resourceRolesRepository;

    @Autowired
    private TokenCache tokenCache;

    @Autowired
    private TokenService tokenService;


    @Override
    public boolean checkAccess(String accessTokenUUID, String resourceService,String resourcePath) {
        Loggers.AUTHORIZATION_LOGGER.info("reached to service layer with UUID {} - path {} ",accessTokenUUID,resourcePath);
        try {
            AuthSessionDTO session = tokenService.getActiveAuthSession(accessTokenUUID);
            if (session == null) return false;

            String clientId = session.getClientId();

            Set<Long> clientRoleIds = clientRolesRepository.findActiveRoleIdsByClientId(clientId);

            Loggers.AUTHORIZATION_LOGGER.info("The fetched client form Db is {}" , clientRoleIds);

            if (clientRoleIds == null ) return false;

            Set<Long> resourceRoleIds = resourceRolesRepository.findActiveRoleIdsByResourceServiceAndResourcePath(resourceService,resourcePath);
            if (resourceRoleIds == null) return false;


            Loggers.AUTHORIZATION_LOGGER.info("the fetched the clientRoleIds {} resourceRoleIds is {} ", clientRoleIds,resourceRoleIds);


            return clientRoleIds.stream().anyMatch(resourceRoleIds::contains);

        } catch (Exception ex) {

            Loggers.AUTHORIZATION_LOGGER.error("Error checking access: {}", ex.getMessage(), ex);
            return false;
        }
    }



}
