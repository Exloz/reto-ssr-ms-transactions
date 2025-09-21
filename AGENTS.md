# Repository Guidelines

## Project Structure & Module Organization
Source lives under `src/main/java/com/sofka/ms_transactions`, following Clean Architecture layers (`domain`, `application`, `infrastructure`, `presentation`, plus `event`). Shared DTOs and models sit in `domain/model`, use cases in `application/usecase`, and adapters such as Kafka consumers and persistence gateways in `infrastructure`. REST controllers reside in `presentation/controller`. Configuration files are in `src/main/resources` with profile overrides in `application-dev.properties` and `application-test.properties`. Tests mirror the production tree under `src/test/java` for fast discovery.

## Build, Test, and Development Commands
- `./gradlew bootRun`: start the reactive service on port 8081 using the current profile.
- `./gradlew clean build`: compile, run unit tests, and produce the bootable JAR.
- `./gradlew test`: execute the JUnit 5 suite with the H2 in-memory `test` profile.
- `docker-compose up -d`: spin up PostgreSQL and Kafka defined in `compose.yaml` before running the app locally.

## Coding Style & Naming Conventions
Target Java 21 and keep indentation at four spaces; avoid tabs. Class names use `UpperCamelCase`, methods and fields use `lowerCamelCase`, and constants stay `UPPER_SNAKE_CASE`. Stick to the existing package structure and favour reactive types (`Mono`, `Flux`) for flows that touch IO. Lombok simplifies boilerplate—ensure annotations such as `@RequiredArgsConstructor` remain close to class declarations.

## Testing Guidelines
Unit tests rely on JUnit 5, Mockito, and Reactor `StepVerifier`. Name test classes after the subject with a `Test` suffix (e.g., `CreateCuentaUseCaseTest`) and test methods in a readable `shouldDoThing_whenCondition` style. Run `./gradlew test` before pushing; aim to cover new branches, especially messaging and persistence adapters that wrap external systems.

## Commit & Pull Request Guidelines
Follow the existing concise, imperative style (`setup project`) for commit headers, keeping them under ~72 characters. Each PR should describe the change, note affected modules, and reference tracking tickets when available. Include test evidence (command output or screenshots) for behavioural updates, and update API or configuration documentation if user-facing changes occur.

## Environment & Tooling Tips
Copy `.env` secrets or local overrides into profile-specific properties rather than committing them. Use the `dev` profile for PostgreSQL via R2DBC, and switch to `test` when executing automated tests to prevent external dependencies.
