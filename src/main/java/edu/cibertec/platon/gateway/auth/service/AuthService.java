package edu.cibertec.platon.gateway.auth.service;

import edu.cibertec.platon.gateway.auth.dto.AuthenticationRequest;
import edu.cibertec.platon.gateway.auth.dto.AuthenticationResponse;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<AuthenticationResponse> login(AuthenticationRequest request);
}
