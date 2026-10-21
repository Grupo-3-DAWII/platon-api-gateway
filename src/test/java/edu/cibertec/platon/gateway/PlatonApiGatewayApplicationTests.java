package edu.cibertec.platon.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.r2dbc.url=r2dbc:postgresql://localhost:5432/account_db",
        "spring.r2dbc.username=account_user",
        "spring.r2dbc.password=account_pass",
        "jwt.secret-key=platon-local-secret-key-change-me-please-123456",
        "jwt.validity-in-ms=3600000"
})
class PlatonApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}
