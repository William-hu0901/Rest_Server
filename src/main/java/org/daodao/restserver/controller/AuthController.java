package org.daodao.restserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.daodao.restserver.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "JWT Token generation and authentication")
public class AuthController {

    private final JwtTokenProvider tokenProvider;

    @Value("${spring.security.user.name}")
    private String configUsername;

    @Value("${spring.security.user.password}")
    private String configPassword;

    public AuthController(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token", description = "Authenticate with username and password to get JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if (configUsername.equals(loginRequest.getUsername()) && 
            configPassword.equals(loginRequest.getPassword())) {
            
            String token = tokenProvider.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        }
        
        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;

        public LoginResponse(String token) {
            this.token = token;
        }
    }
}
