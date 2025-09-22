# Servicio ms-transactions

Microservicio reactivo responsable de la gestión de **Cuentas** y **Movimientos**. Implementa Clean Architecture, usa Spring WebFlux + R2DBC para la capa de datos y se integra con Kafka tanto para consumir eventos de ms-users como para publicar confirmaciones de transacciones.

## 🚀 Clonación desde Orquestación

Para clonar todo el proyecto completo (orquestador + microservicios):

```bash
git clone https://github.com/Exloz/reto-arq-ms.git
cd reto-arq-ms
# Los microservicios estarán disponibles en ms-transactions/ y ms-users/
```

## Tecnologías Principales

## Tecnologías Principales
- Java 21 · Spring Boot 3.5.6 (WebFlux)
- Spring Data R2DBC · PostgreSQL
- Spring Cloud Stream + Kafka
- Reactor · Gradle · Docker

## Configuración y Puesta en Marcha
### Prerrequisitos
- Java 21
- Docker / Docker Compose

### Ejecución Local (perfil `dev`)
```bash
cd ms-transactions
./gradlew bootRun --args='--spring.profiles.active=dev'
```
API disponible en `http://localhost:8081` con documentación Swagger en `http://localhost:8081/swagger-ui.html`.

### Ejecución con Docker (perfil `docker`)
1. Empaqueta el servicio:
   ```bash
   cd ms-transactions && ./gradlew bootJar
   ```
2. Desde la raíz del repositorio levanta toda la plataforma (PostgreSQL, Kafka y ambos microservicios):
   ```bash
   docker compose build
   docker compose up -d
   ```
   El perfil `docker` configura hosts internos (`postgres:5432`, `kafka:9092`) y se activa mediante `SPRING_PROFILES_ACTIVE=docker`.
3. El script `BaseDatos.sql` en la raíz mantiene el mismo esquema que R2DBC inicializa.

### Perfiles Soportados
- `dev`: PostgreSQL local (`localhost:5432`), Kafka en `localhost:9092`.
- `docker`: PostgreSQL/Kafka en la red de contenedores.

## Endpoints Principales
### Cuentas
- `GET /cuentas`
- `GET /cuentas/{cuentaId}`
- `POST /cuentas`
- `PUT /cuentas/{cuentaId}`
- `DELETE /cuentas/{cuentaId}`

### Movimientos
- `GET /movimientos`
- `GET /movimientos/{movimientoId}`
- `POST /movimientos`
- `PUT /movimientos/{movimientoId}`
- `DELETE /movimientos/{movimientoId}`

### Reportes
- `GET /reportes?clienteId={id}&startDate={yyyy-mm-dd}&endDate={yyyy-mm-dd}` — Genera el estado de cuenta solicitado en las especificaciones.

## Eventos y Mensajería
- **Consumo**: `ClienteCreatedEvent` (topic `cliente-created-topic`) para crear cuentas por defecto cuando se registra un cliente. El binding activo es `clienteCreatedConsumer-in-0`, registrado mediante `spring.cloud.function.definition=clienteCreatedConsumer` y asociado al grupo `ms-transactions`.
- **Publicación**: `TransactionCompletedEvent` (topic `transaction-completed-topic`) al registrar un movimiento exitoso. Se emite vía `transactionCompleted-out-0`.

## Pruebas
```bash
cd ms-transactions
./gradlew test
```
Incluye pruebas unitarias (servicios, casos de uso) e integración WebFlux con repositorios en memoria y `StreamBridge` simulado.

## Estructura de Carpetas
```
src/main/java/com/sofka/ms_transactions/
├── application        # Casos de uso (CreateCuenta, GetAccountStatement)
├── domain             # Modelos, repositorios y servicios de dominio
├── event              # Eventos compartidos
├── infrastructure     # Adaptadores (R2DBC, Kafka consumer/producer)
└── presentation       # Controladores WebFlux y DTOs
```

## Consideraciones de Producción
- Asegurar la creación (o control de auto-creación) de `cliente-created-topic` y `transaction-completed-topic` en Kafka.
- Externalizar credenciales a variables de entorno o secretos.
- Definir políticas de retención de eventos y monitoreo/reactive metrics.
- Revisar configuraciones de TLS para Kafka/PostgreSQL antes de despliegues productivos.
