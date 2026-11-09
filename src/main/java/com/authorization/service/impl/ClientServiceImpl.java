package com.authorization.service.impl;

import com.authorization.dto.request.ClientRegisterRequest;
import com.authorization.dto.response.ClientRegisterResponse;
import com.authorization.entity.Client;
import com.authorization.entity.ClientRoles;
import com.authorization.entity.Role;
import com.authorization.repository.ClientRepository;
import com.authorization.repository.ClientRolesRepository;
import com.authorization.repository.RoleRepository;
import com.authorization.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ClientRolesRepository clientRolesRepository;

    @Override
    @Transactional
    public ClientRegisterResponse registerOrUpdateClient(ClientRegisterRequest request) {
        String clientId = "client_" + request.getUid();

        // 1. Fetch or Create Client
        Client client = clientRepository.findClientByClientId(clientId)
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setClientId(clientId);
                    // Generate a secure random 16-character secret key
                    String randomSecret = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
                    newClient.setClientSecret(randomSecret);
                    newClient.setClientName(request.getClientName());
                    return clientRepository.save(newClient);
                });

        // 2. Resolve Role Names based on Subscription Plan
        List<String> roleNames = new ArrayList<>();
        if ("job_posting".equalsIgnoreCase(request.getPlan())) {
            roleNames.add("RECRUITER_POST_JOB");
        } else if ("applicant_viewing".equalsIgnoreCase(request.getPlan())) {
            roleNames.add("RECRUITER_VIEW_APPLICANTS");
        } else if ("combined".equalsIgnoreCase(request.getPlan())) {
            roleNames.add("RECRUITER_POST_JOB");
            roleNames.add("RECRUITER_VIEW_APPLICANTS");
        } else {
            roleNames.add("RECRUITER_POST_JOB"); // Default fallback
        }

        // 3. Map Roles in MySQL client_roles Table
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found in database: " + roleName));

            // Check if mapping already exists
            ClientRoles existingRoleMapping = client.getClientRoles().stream()
                    .filter(cr -> cr.getRole().getName().equalsIgnoreCase(roleName))
                    .findFirst()
                    .orElse(null);

            if (existingRoleMapping == null) {
                ClientRoles clientRole = new ClientRoles();
                clientRole.setClient(client);
                clientRole.setRole(role);
                clientRole.setActive(true);
                clientRolesRepository.save(clientRole);
            } else if (!existingRoleMapping.isActive()) {
                existingRoleMapping.setActive(true);
                clientRolesRepository.save(existingRoleMapping);
            }
        }

        return ClientRegisterResponse.builder()
                .success(true)
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .clientName(client.getClientName())
                .roles(roleNames)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientRegisterResponse getClientDetails(String clientId) {
        Client client = clientRepository.findClientByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found for ID: " + clientId));

        List<String> activeRoles = client.getClientRoles().stream()
                .filter(ClientRoles::isActive)
                .map(cr -> cr.getRole().getName())
                .collect(Collectors.toList());

        return ClientRegisterResponse.builder()
                .success(true)
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .clientName(client.getClientName())
                .roles(activeRoles)
                .build();
    }

    @Override
    @Transactional
    public boolean deleteClientRoles(String clientId) {
        Client client = clientRepository.findClientByClientId(clientId).orElse(null);
        if (client == null) {
            return false;
        }

        // Deactivate all role mappings for the client (Soft delete)
        for (ClientRoles clientRole : client.getClientRoles()) {
            if (clientRole.isActive()) {
                clientRole.setActive(false);
                clientRolesRepository.save(clientRole);
            }
        }
        return true;
    }
}
