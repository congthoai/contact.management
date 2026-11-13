# Contact Management

Spring Boot 4.1.2 / Java 17 web service.

## Requirements

- Java 17
- Maven Wrapper
- PostgreSQL

## Run

Generate a JWT secret, configure the database environment variables, apply the database scripts, and start the service.

On Windows PowerShell:

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET_BASE64 = [Convert]::ToBase64String($bytes)
```

On macOS/Linux:

```bash
export JWT_SECRET_BASE64="$(openssl rand -base64 32)"
```

Do not commit the generated secret.

Start the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
mvnw.cmd spring-boot:run
```

## Environment

Database settings default to a local PostgreSQL database named `contact_management`.

| Variable | Description | Default |
|---|---|---|
| `SERVER_PORT` | HTTP port | `9981` |
| `DB_PRIMARY_URL` | Primary PostgreSQL URL | Local `contact_management` |
| `DB_PRIMARY_USERNAME` | Primary database username | `postgres` |
| `DB_PRIMARY_PASSWORD` | Primary database password | Empty |
| `DB_SECONDARY_URL` | Secondary PostgreSQL URL | Local `contact_management` |
| `DB_SECONDARY_USERNAME` | Secondary database username | `postgres` |
| `DB_SECONDARY_PASSWORD` | Secondary database password | Empty |
| `JWT_SECRET_BASE64` | Required Base64 HS256 secret | Empty |
| `JWT_EXPIRATION_MINUTES` | JWT access-token lifetime | `30` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins | `http://localhost:3000` |
| `EXECUTION_POOL_SIZE` | Scheduled executor pool size | `10` |

## Database

The project uses the custom JDBC components and PostgreSQL. Apply the SQL scripts manually in this order:

1. `database/identity/V1__user.sql`
2. `database/identity/V2__role.sql`
3. `database/contact/V1__create_contacts.sql`

The identity scripts create users and roles. Registration does not assign a role automatically; an administrator must provision a role association explicitly.

The Contact module creates its own `contacts` table and indexes. Email uniqueness is case-insensitive.

## Modules

- `module/auth`: username/password login and JWT response.
- `module/user`: registration, user lookup and profile.
- `module/role`: role lookup.
- `module/contact`: contact REST API, validation, search, pagination, statistics, CSV export and persistence.
- `config/security`: stateless HTTP security and JWT configuration.
- `common/security`: JWT token generation.
- `component/jdbc`: custom JDBC template and PostgreSQL connection support.

The Contact module follows the project flow:

```text
ContactController
    -> ContactService
        -> ContactRepository
            -> ContactPersistent
                -> MyJdbcTemplate
```

It uses the existing JDBC layer and response envelope. It does not introduce JPA/Hibernate or Flyway. The source repository's frontend, API Gateway, Discovery Server, Config Server and RabbitMQ relay are not part of this server API port.

## Authentication

- `POST /user/register`: public registration.
- `POST /auth/login`: public login; returns a Bearer JWT.
- All other endpoints require a valid JWT.
- Contact endpoints are available to authenticated users. No role is assigned automatically on registration.

JSON API responses use the project's `{ code, data }` envelope. Authentication and authorization failures use HTTP 401 and 403.

## Contact API

Base path: `/api/v1/contacts`.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/contacts` | Search, filter, sort and paginate contacts |
| GET | `/api/v1/contacts/stats` | Total, favorites and counts by category |
| GET | `/api/v1/contacts/export` | Export matching contacts as CSV |
| GET | `/api/v1/contacts/{id}` | Get one contact |
| POST | `/api/v1/contacts` | Create a contact |
| PUT | `/api/v1/contacts/{id}` | Replace contact fields |
| PATCH | `/api/v1/contacts/{id}/favorite` | Toggle favorite |
| DELETE | `/api/v1/contacts/{id}` | Delete a contact |

Search query parameters:

- `q`: searches name, email, phone and company.
- `category`: `FAMILY`, `FRIEND`, `WORK`, `CLIENT` or `OTHER`.
- `favorites`: `true` to return favorites only.
- `sort`: `NAME`, `NAME_DESC`, `COMPANY`, `NEWEST` or `UPDATED`.
- `page`: zero-based page number; defaults to `0`.
- `size`: page size from 1 to 100; defaults to `20`.

CSV export accepts the same `q`, `category`, `favorites` and `sort` filters. The CSV output protects spreadsheet consumers from formula injection.
