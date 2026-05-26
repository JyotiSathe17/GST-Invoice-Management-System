package com.fullstack.gstbillingwithinvoicemngmt.controller;

import com.fullstack.gstbillingwithinvoicemngmt.dto.*;
import com.fullstack.gstbillingwithinvoicemngmt.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Authentication End Point", description = "The endpoint consists of user sign up and sign in operation")
@Slf4j
public class AuthController {

    private final UserInfoService service;

    public AuthController(@Lazy UserInfoService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "The endpoint register user to system", responses = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = HashMap.class)))})
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        log.debug("New user registration request: {}", request);
        UserResponse response = service.registerUser(request);
        log.info("User register successfully: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    @Operation(summary = "Sign In", description = "Signin using userName and password. Click on Authorized button and paste the token for accessing GST Controller",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    public ResponseEntity<LoginResponse> generateToken(@Valid @RequestBody LoginRequest request) {
        log.debug("Sign in request from: {}", request.userName());
        LoginResponse response = service.validateUser(request);
        log.debug("User '{}' successfully authenticated", request.userName());

        return ResponseEntity.ok(response);
    }
}
