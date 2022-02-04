package com.projects.cloudstorage.repository;

import com.projects.cloudstorage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByLogin(String login);

}
