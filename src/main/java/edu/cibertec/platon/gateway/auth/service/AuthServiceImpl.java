package edu.cibertec.platon.gateway.auth.service;

import edu.cibertec.platon.gateway.auth.dto.AuthAccountUser;
import edu.cibertec.platon.gateway.auth.dto.AuthenticationRequest;
import edu.cibertec.platon.gateway.auth.dto.AuthenticationResponse;
import edu.cibertec.platon.gateway.auth.repository.AccountUserRepository;
import edu.cibertec.platon.gateway.auth.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Long ACTIVE_STATUS_ID = 1L;
    private static final String INVALID_CREDENTIALS_MESSAGE = "Usuario o contraseña inválido";

    private final AccountUserRepository accountUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(
            AccountUserRepository accountUserRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.accountUserRepository = accountUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<AuthenticationResponse> login(AuthenticationRequest request) {
        return accountUserRepository.findByUsername(request.username())
                .switchIfEmpty(invalidCredentials())
                .flatMap(user -> validateCredentials(user, request.password()))
                .map(this::toResponse);
    }

    private Mono<AuthAccountUser> validateCredentials(AuthAccountUser user, String rawPassword) {
        if (!ACTIVE_STATUS_ID.equals(user.idStatus())) {
            return invalidCredentials();
        }
        if (!passwordEncoder.matches(rawPassword, user.password())) {
            return invalidCredentials();
        }
        return Mono.just(user);
    }

    private AuthenticationResponse toResponse(AuthAccountUser user) {
        return new AuthenticationResponse(jwtTokenProvider.createToken(user));
    }

    private <T> Mono<T> invalidCredentials() {
        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE));
    }
}
