# Servicio ms-transactions

Este es el microservicio ms-transactions para la gestión de las entidades Cuenta y Movimientos utilizando programación reactiva.

## Descripción

El servicio ms-transactions implementa una arquitectura limpia (Clean Architecture) con programación reactiva y maneja las operaciones para las entidades Cuenta y Movimientos. Utiliza Spring WebFlux para el manejo reactivo de solicitudes y R2DBC para la persistencia reactiva de datos.

## Tecnologías

- Java 21
- Spring Boot 3.5.6 con WebFlux
- Spring Data R2DBC
- PostgreSQL
- Kafka
- Gradle
- Docker

## Configuración

### Prerrequisitos

- Java 21 instalado
- Docker y Docker Compose
- Gradle (incluido en el proyecto)

### Base de Datos

1. Inicia PostgreSQL y Kafka usando Docker Compose desde el directorio raíz del proyecto:
   ```bash
   docker-compose up -d
   ```

2. El servicio se conectará automáticamente a la base de datos PostgreSQL configurada.

### Ejecución

1. Ejecuta la aplicación:
   ```bash
   ./gradlew bootRun
   ```

2. La aplicación estará disponible en: http://localhost:8081

## Documentación de la API

La documentación de la API está disponible en Swagger UI: http://localhost:8081/swagger-ui.html

## Endpoints

### Cuenta

- **GET /cuentas** - Obtener todas las cuentas
- **GET /cuentas/{cuentaId}** - Obtener cuenta por ID
- **POST /cuentas** - Crear una nueva cuenta
- **PUT /cuentas/{cuentaId}** - Actualizar cuenta
- **DELETE /cuentas/{cuentaId}** - Eliminar cuenta

### Movimientos

- **GET /movimientos** - Obtener todos los movimientos
- **POST /movimientos** - Crear un nuevo movimiento
- **GET /movimientos/reportes?cuentaId={id}&startDate={date}&endDate={date}** - Obtener reporte de movimientos

## Perfiles de Configuración

- **dev**: Utiliza base de datos PostgreSQL con R2DBC para desarrollo
- **test**: Utiliza base de datos H2 en memoria para pruebas

Para ejecutar con un perfil específico:
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## Pruebas

Ejecuta las pruebas unitarias e de integración:
```bash
./gradlew test
```

## Eventos

El servicio consume y publica eventos a través de Kafka:
- **ClienteCreatedEvent**: Consume eventos de creación de clientes desde ms-users
- **TransactionCompletedEvent**: Publica eventos cuando se completan transacciones

## Arquitectura

Este servicio utiliza Clean Architecture con programación reactiva:

- **Capa de Dominio**: Entidades e interfaces del dominio
- **Capa de Casos de Uso**: Lógica de negocio reactiva
- **Capa de Infraestructura**: Repositorios R2DBC y adaptadores externos
- **Capa de Presentación**: Controladores WebFlux

## Estructura del Proyecto

```
src/main/java/com/sofka/ms_transactions/
├── application/   # Casos de uso y lógica de aplicación
├── domain/        # Entidades del dominio y servicios
├── event/         # Eventos para comunicación asíncrona
├── infrastructure/ # Implementaciones de infraestructura
└── presentation/  # Controladores y presentación
```

## Despliegue

Para desplegar en producción, asegúrate de:
1. Configurar las variables de entorno para la base de datos
2. Usar HTTPS
3. Configurar autenticación JWT si es necesario
4. Ajustar los límites de recursos según sea necesario
5. Configurar el manejo reactivo de errores y backpressure