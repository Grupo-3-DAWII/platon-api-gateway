# platon-api-gateway

API Gateway para centralizar el acceso a los microservicios de Platon.

## Requisitos

- Java 21 o superior
- Maven 3.9 o superior
- `platon-eureka-server` ejecutandose para descubrir servicios con Eureka
- `platon-config-server` ejecutandose para cargar configuracion centralizada

## Puertos

- Gateway: `8080`
- Eureka Server: `8761`
- Config Server: `8888`
- Account: `8083`
- Products: `8081`
- Payments: `8082`

## Ejecutar

```bash
mvn spring-boot:run
```

## Rutas

El gateway carga sus rutas desde `platon-config-server/config-repo/platon-api-gateway.yml`. Para `account` usa Eureka:

- `POST http://localhost:8080/api/v1/auth/login` -> login del gateway contra la BD de Platon
- `http://localhost:8080/api/accounts/**` -> `lb://platon-microservice-account`
- `http://localhost:8080/api/products/**` -> `http://localhost:8081/api/products/**`
- `http://localhost:8080/api/payments/**` -> `http://localhost:8082/api/payments/**`

Por ahora el microservicio `account` es el que tiene implementacion. Las rutas de `products` y `payments` quedan listas para cuando esos servicios existan.

## Variables de entorno

Puedes cambiar los destinos sin editar el codigo:

- `CONFIG_SERVER_URL`
- `ACCOUNT_SERVICE_URI`
- `PRODUCTS_SERVICE_URL`
- `PAYMENTS_SERVICE_URL`
- `EUREKA_SERVER_URL`
- `DB_R2DBC_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET_KEY`
- `JWT_VALIDITY_IN_MS`
- `SERVER_PORT`

Ejemplo:

```bash
$env:ACCOUNT_SERVICE_URI="lb://platon-microservice-account"
mvn spring-boot:run
```

## Prueba rapida

Con `platon-microservice-account` ejecutandose:

```bash
curl -u admin:admin123 http://localhost:8080/api/accounts
```

Login:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"usuario\",\"password\":\"usuario123\"}"
```
