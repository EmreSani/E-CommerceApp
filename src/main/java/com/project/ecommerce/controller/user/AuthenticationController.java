package com.project.ecommerce.controller.user;


import com.project.ecommerce.payload.messages.SuccessMessages;
import com.project.ecommerce.payload.request.authentication.LoginRequest;
import com.project.ecommerce.payload.request.business.UpdatePasswordRequest;
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

    @PostMapping("/login") // http://localhost:8080/auth/login + POST + JSON
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest){
        return authenticationService.authenticateUser(loginRequest);
    }

    //Not: ODEV : updatePassword() --> Controller ve Service
    @PatchMapping("/updatePassword") // http://localhost:8080/auth/updatePassword
    @PreAuthorize("hasAnyAuthority('ADMIN','CUSTOMER')")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                                 HttpServletRequest request ){
        authenticationService.updatePassword(updatePasswordRequest , request);
        String response = SuccessMessages.PASSWORD_CHANGED_RESPONSE_MESSAGE; // paylod.messages
        return ResponseEntity.ok(response);
    }

}











