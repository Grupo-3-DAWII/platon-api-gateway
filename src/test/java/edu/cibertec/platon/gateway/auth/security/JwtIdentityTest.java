package edu.cibertec.platon.gateway.auth.security;
import edu.cibertec.platon.gateway.auth.dto.AuthAccountUser;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import static org.junit.jupiter.api.Assertions.*;
class JwtIdentityTest {
    @Test
    void discardsForgedIdentityAndForwardsOnlyVerifiedTokenIdentity() {
        var provider=new JwtTokenProvider("platon-local-secret-key-change-me-please-123456",3600000);
        provider.init();
        var filter=new JwtAuthenticationFilter(provider);
        var anonymous=MockServerWebExchange.from(MockServerHttpRequest.get("/api/accounts/me/contact").header("X-Platon-User-Id","99"));
        filter.filter(anonymous,exchange->{assertNull(exchange.getRequest().getHeaders().getFirst("X-Platon-User-Id"));return Mono.empty();}).block();
        var token=provider.createToken(new AuthAccountUser(2L,"cliente","encoded",2L,1L,"cliente@example.test"));
        var authenticated=MockServerWebExchange.from(MockServerHttpRequest.get("/api/accounts/me/contact").header("Authorization","Bearer "+token).header("X-Platon-User-Id","99"));
        filter.filter(authenticated,exchange->{assertEquals("2",exchange.getRequest().getHeaders().getFirst("X-Platon-User-Id"));return Mono.empty();}).block();
    }
}

