package com.lifeverse.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;
//    @JsonIgnore
    private String password;
}
