package com.projects.cloudstorage.service.impl;

import com.projects.cloudstorage.model.User;
import com.projects.cloudstorage.repository.UserRepository;
import com.projects.cloudstorage.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    @Override
    public User findByLogin(String login) {
        User result = userRepository.findByLogin(login);
        log.info("METHOD: findByUserName - user: {} found by username: {}", result.getLogin(), login);
        return result;

    }
}
