# DB Migrator

A powerful database migration system for **PostgreSQL**, **MySQL**, and **Cassandra**, built with **Java 17+**, **Gradle**, and **Clean Architecture** principles.  
Inspired by Laravel's migration flow, it supports:

- Manual SQL-based migrations
- Rollbacks by tag or filename
- Multi-engine config
- YAML + Environment configuration
- JAR/CLI support
- Migration status tracking
- Cassandra `.cql` file parsing with multiple `up/down` blocks

---

## 🔧 Setup

### Requirements

- Java 17+ (tested with OpenJDK 24)
- Gradle 8+
- PostgreSQL, MySQL, Cassandra drivers installed

### Configuration

Edit the `application.yml` file at the root:

```yaml
default_engine: ${DEFAULT_ENGINE:postgres}

engines:
  postgres:
    enabled: true
    datasource:
      host: ${POSTGRES_DB_HOST:localhost}
      port: ${POSTGRES_DB_PORT:5432}
      name: ${POSTGRES_DB_NAME:db_name}
      username: ${POSTGRES_DB_USER:db_name}
      password: ${POSTGRES_DB_PASSWORD:pass123}

  mysql:
    enabled: true
    datasource:
      host: ${MYSQL_DB_HOST:localhost}
      port: ${MYSQL_DB_PORT:3306}
      name: ${MYSQL_DB_NAME:db_name}
      username: ${MYSQL_DB_USER:db_username}
      password: ${MYSQL_DB_PASSWORD:pass123}

  cassandra:
    enabled: true
    datasource:
      auth: false
      contactPoints: ${CASSANDRA_HOST:127.0.0.1}
      port: ${CASSANDRA_PORT:9042}
      datacenter: ${CASSANDRA_DC:datacenter1}
      keyspace: ${CASSANDRA_KEYSPACE:keyspace_name}
      username: ${CASSANDRA_USER:admin}
      password: ${CASSANDRA_PASSWORD:admin}
```

> ✅ Environment variables are supported with default values fallback.

---

## 🚀 Usage
### Run From Gradle

```bash

./gradlew run --args="check --engine={db-engine:postgres,mysql,cassandra} "
./gradlew run --args="migrate --engine={db-engine:postgres,mysql,cassandra} "
./gradlew run --args="rollback-tag --tag=<tag> --engine={db-engine:postgres,mysql,cassandra} "
./gradlew run --args="make --name=create_users_table --engine={db-engine:postgres,mysql,cassandra} "
```

### Run From Built JAR

```bash
java -jar db-migrator.jar migrate --engine=postgres
java -jar db-migrator.jar rollback-file --name=20250712000001__create_users_table.sql --engine=postgres
java -jar db-migrator.jar make --name=create_users_table --engine=postgres
```

> 📝 If no `--engine` is passed, it uses the one in `default_engine` in `application.yml`.

---

## 📁 Migrations

Each engine has its own folder:

```
migrations/
├── postgres/
│   └── 20250712000001__create_users_table.sql
├── mysql/
├── cassandra/
│   └── 20250712000001__create_tracks_table.cql
```

### PostgreSQL / MySQL File Format

```sql
-- tag: 20250712000001

-- up
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY,
  name TEXT
);

-- down
DROP TABLE IF EXISTS users;
```

### Cassandra File Format

```sql
-- tag: 20250712000001

-- up
CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY,
  name TEXT
);

-- down
DROP TABLE IF EXISTS users;
```

> ☑️ Cassandra files support **multi-statement `up` and `down`** blocks parsed into lists.

---

## 🧪 Commands

| Command                       | Description                                      |
|------------------------------|--------------------------------------------------|
| `migrate [--engine=...]`     | Runs all new migrations                         |
| `rollback-tag <tag>`         | Rollbacks a migration by its tag                |
| `rollback-file <filename>`   | Rollbacks using the full file name              |
| `make --name=...`            | Generates a new migration file                  |
| `check`                      | Verifies DB connectivity per engine             |

---

## 🧼 Clean Architecture

- `core/` → MigrationRunner orchestration logic
- `infra/adapters/` → Per-engine migration services
- `util/` → YAML config loader, logger, path helpers
- `resources/migrations/` → SQL and CQL migration files

---

## 🛠 Development Notes

- `migrations` table is created per engine if missing.
- `tag` must be unique across applied/rolled_back migrations.
- JAR-safe resource handling is supported via `AppConfig.isRunningFromJar()`.

---

## 🧯 Troubleshooting

- **Migration not applied?** Check the tag is unique.
- **JAR execution error?** Ensure `migrations/` exists next to JAR.
- **Cassandra keyspace error?** Auto-creation is supported if configured.
- **SLF4J warning?** Add proper binding (e.g. logback) to suppress NOP logger warning.

---

## 📦 Build

```bash
./gradlew clean build
```

Outputs to:

```
build/libs/eand-migrator.jar
```

You can rename and distribute this CLI JAR tool.

---

## 📄 License

MIT