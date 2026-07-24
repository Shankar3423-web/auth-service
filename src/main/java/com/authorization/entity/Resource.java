package com.authorization.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false)
    private String service;

    @Column(nullable = false)
    private String path;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "resource")
    private Set<ResourceRoles> resourceRoles = new HashSet<>();

    //resource_roles should be as seperate entity/ not standard
}
