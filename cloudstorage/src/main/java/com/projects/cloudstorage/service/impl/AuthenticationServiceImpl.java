package com.projects.cloudstorage.service.impl;

import com.projects.cloudstorage.entity.AuthenticationRequest;
import com.projects.cloudstorage.security.jwt.JwtTokenProvider;
import com.projects.cloudstorage.service.AuthenticationService;
import com.projects.cloudstorage.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public String authenticate(AuthenticationRequest authRequest) {
        String login = authRequest.getLogin();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, authRequest.getPassword()));
        return jwtTokenProvider.createToken(login, userService.findByLogin(login).getRoles());
    }
}
