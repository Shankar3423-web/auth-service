package com.authorization.repository;

import com.authorization.entity.ClientRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ClientRolesRepository extends JpaRepository<ClientRoles,Long> {
    @Query("SELECT cr.role.id FROM ClientRoles cr " +
            "WHERE cr.client.id = :client_id AND cr.isActive = true")
    Set<Long> findActiveRoleIdsByClientId(@Param("client_id") Long client_id);

    @Query("""
    SELECT cr.role.id 
    FROM ClientRoles cr 
    JOIN cr.client c 
    WHERE c.clientId = :clientId 
      AND cr.isActive = true
""")
    Set<Long> findActiveRoleIdsByClientId(@Param("clientId") String clientId);
}
