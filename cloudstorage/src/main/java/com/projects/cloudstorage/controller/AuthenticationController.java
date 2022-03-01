package com.projects.cloudstorage.controller;

import com.projects.cloudstorage.constants.Endpoints;
import com.projects.cloudstorage.entity.AuthenticationRequest;
import com.projects.cloudstorage.service.impl.AuthenticationServiceImpl;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

    public AuthenticationController(AuthenticationServiceImpl authenticationService) {
        this.authenticationService = authenticationService;
    }

    //Проверка логина и пароля, присвоение токена пользователю
    @PostMapping(Endpoints.LOGIN_ENDPOINT)
    public ResponseEntity<String> login(@RequestBody AuthenticationRequest authRequest) {
        String token = authenticationService.authenticate(authRequest);
        return ResponseEntity.ok(new JSONObject().put("auth-token", token).toString());
    }

    //Обработчик запроса logout
    @GetMapping(Endpoints.LOGIN_ENDPOINT)
    public HttpStatus logout() {
        return HttpStatus.OK;
    }

}
