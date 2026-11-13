# Contact Management

A Spring Boot web service template for the contact management project.

## Requirements

- Java 17
- Maven Wrapper
- PostgreSQL

## Run

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
mvnw.cmd spring-boot:run
```

## Included foundation

- Shared string, JSON, map, and time utilities
- Custom JDBC template with PostgreSQL primary/secondary connection support
- Reusable Java HTTP client wrapper
- Scheduled executor and Spring-style cron task executor
- In-process `EventPublisher`

The `EventPublisher` dispatches events only within the current application process. Redis Pub/Sub and domain-specific modules are intentionally not included in this starter.

## Database configuration

Database settings are read from environment variables. The defaults target a local PostgreSQL database named `contact_management`.

- `DB_PRIMARY_URL`, `DB_PRIMARY_USERNAME`, `DB_PRIMARY_PASSWORD`
- `DB_SECONDARY_URL`, `DB_SECONDARY_USERNAME`, `DB_SECONDARY_PASSWORD`
- `EXECUTION_POOL_SIZE`
- `SERVER_PORT`
