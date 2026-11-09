package com.authorization.service;

import com.authorization.dto.request.ClientRegisterRequest;
import com.authorization.dto.response.ClientRegisterResponse;

public interface ClientService {
    ClientRegisterResponse registerOrUpdateClient(ClientRegisterRequest request);
    ClientRegisterResponse getClientDetails(String clientId);
    boolean deleteClientRoles(String clientId);
}
