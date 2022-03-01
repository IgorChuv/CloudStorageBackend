package com.projects.cloudstorage.service;

import com.projects.cloudstorage.entity.AuthenticationRequest;

public interface AuthenticationService {
    String authenticate(AuthenticationRequest authRequest);
}
