package com.projects.cloudstorage.JsonEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationRequest {

    @JsonProperty("login")
    private String login;

    @JsonProperty("password")
    private String password;

}
