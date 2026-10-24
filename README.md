# platon-api-gateway

Punto de entrada único a los microservicios de Platon. Autentica a los usuarios con JWT y enruta cada petición al servicio correspondiente.

## Requisitos

- Java 25 y Maven 3.9+ (para ejecución local) o Docker con Compose.
- Proyecto `DATABASE` levantado (el login consulta las cuentas de usuario).
- `platon-config-server` en ejecución: de él se cargan las rutas. Sin él, el gateway arranca sin rutas.
- `platon-eureka-server` en ejecución: la ruta de Account se resuelve con Eureka.

## Ejecutar

```bash
# Local
mvn spring-boot:run

# Docker
docker compose up --build
```

El gateway queda en <http://localhost:8080>. Se inicia al final, cuando los demás servicios ya están arriba.

## Autenticación

Todas las rutas exigen un JWT, excepto el login.

**1. Iniciar sesión**

```bash
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{"username":"usuario","password":"usuario123"}'
```

La respuesta trae el token:

```json
{ "access_token": "eyJhbGciOi..." }
```

Si el usuario no existe, está inactivo o la contraseña no coincide, responde `401`.

**2. Usar el token en cada petición**

```bash
curl http://localhost:8080/api/accounts \
  -H "Authorization: Bearer <access_token>"
```

Sin token, o con uno inválido o vencido, responde `401`.

## Rutas

Las rutas se cargan desde `platon-config-server/config-repo/platon-api-gateway.yml`:

| Ruta en el gateway | Servicio | Destino por defecto |
| --- | --- | --- |
| `/api/accounts/**`, `/api/roles/**` | Account | `lb://platon-microservice-account` (Eureka) |
| `/api/products/**` | Products | `http://localhost:8081` |
| `/api/payments/**`, `/api/products-sold/**`, `/api/states-sold/**`, `/api/user-products/**` | Payments | `http://localhost:8082` |

## Configuración

Los valores se pueden sobrescribir con variables de entorno:

| Variable | Para qué sirve |
| --- | --- |
| `CONFIG_SERVER_URL` | Dirección del Config Server (por defecto `http://localhost:8888`) |
| `EUREKA_SERVER_URL` | Dirección de Eureka |
| `ACCOUNT_SERVICE_URI` | Destino de Account |
| `PRODUCTS_SERVICE_URL` | Destino de Products |
| `PAYMENTS_SERVICE_URL` | Destino de Payments |
| `JWT_SECRET_KEY` | Clave para firmar los tokens |
| `JWT_VALIDITY_IN_MS` | Duración del token (por defecto 1 hora) |
| `SERVER_PORT` | Puerto del gateway (por defecto `8080`) |

> Cambia `JWT_SECRET_KEY` fuera de desarrollo local: el valor por defecto es público.

El gateway solo acepta peticiones de navegador desde `http://localhost:4200` (CORS).

## Puertos

| Servicio | Puerto |
| --- | --- |
| Gateway | `8080` |
| Eureka | `8761` |
| Config Server | `8888` |
| Products | `8081` |
| Payments | `8082` |
| Account | `8083` |

## Orden recomendado

1. `platon-eureka-server`
2. `platon-config-server`
3. Microservicios
4. `platon-api-gateway`
