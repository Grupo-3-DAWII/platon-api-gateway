package edu.cibertec.platon.gateway.auth.controller;

import edu.cibertec.platon.gateway.auth.dto.AuthenticationRequest;
import edu.cibertec.platon.gateway.auth.dto.AuthenticationResponse;
import edu.cibertec.platon.gateway.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mono<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        return authService.login(request);
    }
}
