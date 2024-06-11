package com.project.ecommerce.payload.request.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "Username must not be empty")
    private String username;

    @NotNull(message = "Password must not be empty")
    private String password;
}