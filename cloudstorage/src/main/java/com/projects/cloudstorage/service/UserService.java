package com.projects.cloudstorage.service;

import com.projects.cloudstorage.model.User;

public interface UserService {
    User findByLogin(String Login);
}