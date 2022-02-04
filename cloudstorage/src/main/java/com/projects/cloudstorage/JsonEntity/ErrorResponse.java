package com.projects.cloudstorage.JsonEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("id")
    private int id;

}