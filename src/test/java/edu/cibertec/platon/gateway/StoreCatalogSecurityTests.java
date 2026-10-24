package edu.cibertec.platon.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.cloud.config.enabled=false", "eureka.client.enabled=false",
        "spring.r2dbc.url=r2dbc:postgresql://localhost:5432/account_db",
        "spring.r2dbc.username=account_user", "spring.r2dbc.password=account_pass",
        "jwt.secret-key=platon-local-secret-key-change-me-please-123456",
        "jwt.validity-in-ms=3600000"
})
class StoreCatalogSecurityTests {
    @LocalServerPort
    private int port;

    private WebTestClient client() {
        return WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
    }

    @Test
    void publicCatalogReadsPassSecurityWithoutPublishingPrivateProductRoutes() {
        // No gateway routes are loaded in this context: an allowed request reaches routing and returns 404.
        client().get().uri("/api/products/catalog").exchange().expectStatus().isNotFound();
        client().get().uri("/api/products/catalog/12").exchange().expectStatus().isNotFound();
        client().get().uri("/api/products").exchange().expectStatus().isUnauthorized();
        client().get().uri("/api/products/12").exchange().expectStatus().isUnauthorized();
        client().get().uri("/api/products/12/stock-movements").exchange().expectStatus().isUnauthorized();
        client().get().uri("/api/products/editorials").exchange().expectStatus().isUnauthorized();
    }

    @Test
    void writesAndCustomerOperationsStillRequireAuthentication() {
        client().post().uri("/api/accounts/register").exchange().expectStatus().isNotFound();
        client().post().uri("/api/products").exchange().expectStatus().isUnauthorized();
        client().put().uri("/api/products/12").exchange().expectStatus().isUnauthorized();
        client().delete().uri("/api/products/12").exchange().expectStatus().isUnauthorized();
        client().get().uri("/api/products-sold").exchange().expectStatus().isUnauthorized();
        client().post().uri("/api/products-sold").exchange().expectStatus().isUnauthorized();
        client().get().uri("/api/accounts").exchange().expectStatus().isUnauthorized();
    }
}
