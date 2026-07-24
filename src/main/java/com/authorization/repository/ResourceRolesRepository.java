package com.authorization.repository;

import com.authorization.entity.ResourceRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface ResourceRolesRepository extends JpaRepository<ResourceRoles,Long> {

    @Query("SELECT rr.role.id FROM ResourceRoles rr WHERE rr.resource.id = :resourceId AND rr.isActive = true")
    Set<Long> findActiveRoleIdsByResourceId(@Param("resourceId") Long resourceId);

    @Query("""
    SELECT rr.role.id 
    FROM ResourceRoles rr 
    JOIN rr.resource r 
    WHERE r.path = :resourcePath
      AND r.service = :resourceService
      AND rr.isActive = true
""")
    Set<Long> findActiveRoleIdsByResourceServiceAndResourcePath(@Param("resourceService") String resourceService,@Param("resourcePath") String resourcePath);
}
