package com.projects.cloudstorage.repository;

import com.projects.cloudstorage.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

}
