package com.project.ecommerce.controller.user;


import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.request.authentication.LoginRequest;
import com.project.ecommerce.payload.request.authentication.UpdatePasswordRequest;
import com.project.ecommerce.payload.request.authentication.UserRequestForRegister;
import com.project.ecommerce.payload.response.authentication.AuthResponse;
import com.project.ecommerce.service.user.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // POST http://localhost:8080/auth/login - Endpoint to authenticate a user
// PATCH http://localhost:8080/auth/updatePassword - Endpoint to update user password (requires ADMIN or CUSTOMER role)
// POST http://localhost:8080/auth/register - Endpoint to register a new user


    @PostMapping("/login") // http://localhost:8080/auth/login + POST + JSON
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){
        return authenticationService.authenticateUser(loginRequest);
    }

    @PatchMapping("/updatePassword") // http://localhost:8080/auth/updatePassword
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                                 HttpServletRequest request ){
        authenticationService.updatePassword(updatePasswordRequest , request);
        String response = SuccessMessages.PASSWORD_CHANGED_RESPONSE_MESSAGE; // paylod.messages
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register") // POST http://localhost:8080/auth/register - Endpoint to register a new user
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserRequestForRegister userRequestForRegister) {
        return authenticationService.register(userRequestForRegister);
    }

}











