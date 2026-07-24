package com.authorization.repository;

import com.authorization.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
        FROM resource r
        JOIN resource_roles rr ON r.id = rr.resource_id
        JOIN roles ro ON rr.role_id = ro.id
        WHERE ro.name = :roleName
        AND r.path = :resourcePath
    """, nativeQuery = true)
    boolean existsResourceAccessForRole(@Param("roleName") String roleName,
                                        @Param("resourcePath") String resourcePath);


    Optional<Resource> findByPath(@Param("resourcePath") String resourcePath);

    Optional<Resource> findById(@Param("id") long id);

}
