# eScrims 🎮

> Plataforma de organización de scrims y partidas amistosas de eSports (Valorant, LoL, CS2, etc.)  
> con emparejamiento inteligente, ciclo de vida por estados y notificaciones multi-canal.

**Materia:** Proceso de Desarrollo de Software — UADE  
**Comisión:** Jueves Noche — 2026 1C  
**Entrega final:** 18/06/2026

---

## 👥 Integrantes

| Nombre y Apellido        | LU      |
|--------------------------|---------|
| Indriago Ramírez, Miguel | 1196929 |
| Maver, Joaquin           | 1030299 |
| Posse Presa, Matias      | 1055352 |
| Sanchez Carrizo, Naiara  | 1196621 |
| Sierra, Jose             | 1193916 |

---

## 📋 Descripción del sistema

eScrims permite a jugadores y organizadores de eSports:

- **Registrarse y autenticarse** en la plataforma (credenciales + OAuth + verificación de email).
- **Crear scrims** definiendo juego, formato (1v1, 3v3, 5v5), región, rango mínimo/máximo, latencia máxima, fecha/hora y
  modalidad.
- **Buscar y postularse** a scrims compatibles con su perfil.
- **Emparejar jugadores** mediante algoritmos intercambiables (por MMR, latencia o historial).
- **Gestionar el ciclo de vida del scrim:**  
  `Buscando → LobbyArmado → Confirmado → EnJuego → Finalizado / Cancelado`
- **Recibir notificaciones** por múltiples canales (Push, Email, Discord) ante eventos clave.
- **Cargar estadísticas** y reportar conducta al finalizar.
- **Guardar búsquedas** para reutilizarlas en el futuro.

---

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura en capas** (Clean Architecture simplificada):

```
┌──────────────────────────────────────────────────┐
│  Controller  →  REST (Spring Web)                │
├──────────────────────────────────────────────────┤
│  Application →  Facade, Services, Commands,      │
│                 DTOs, Scheduler                   │
├──────────────────────────────────────────────────┤
│  Domain      →  Entidades, State, Strategy,      │
│                 Events, Builder, Moderation,      │
│                 Validation, Value Objects         │
├──────────────────────────────────────────────────┤
│  Infrastructure → Repositorios JPA, Kafka,       │
│                   Notificaciones, Adapters,       │
│                   Persistence Converters          │
├──────────────────────────────────────────────────┤
│  Config      →  Security, JWT, Rate Limiting,    │
│                 OpenTelemetry, Audit Log          │
└──────────────────────────────────────────────────┘
```

### Paquetes principales

| Paquete                          | Responsabilidad                                                                   |
|----------------------------------|-----------------------------------------------------------------------------------|
| `controller/`                    | Endpoints REST: `AuthController`, `ScrimController`, `PlayerController`            |
| `application/service/`           | Orquestación de casos de uso: `AuthService`, `ScrimService`, `ReportService`       |
| `application/facade/`            | Fachada `ScrimFacade` que simplifica la interacción del controller con servicios    |
| `application/command/`           | Patrón Command: `SwapPlayersCommand`, `CommandExecutor` con undo                   |
| `application/dto/`               | DTOs (records) de entrada/salida                                                   |
| `application/scheduler/`         | `ScrimLifecycleScheduler` para transiciones automáticas por tiempo                 |
| `domain/entity/`                 | Entidades JPA: `Player`, `Scrim`, `Lobby`, `ScrimStatistics`, `Report`, `SavedSearch`, `Waitlist`, `PlayerScrimStats` |
| `domain/state/`                  | Patrón State: ciclo de vida del Scrim                                              |
| `domain/strategy/`               | Patrón Strategy: algoritmos de matchmaking                                         |
| `domain/event/`                  | Eventos de dominio: `ScrimCreatedEvent`, `ScrimStateChangedEvent`                  |
| `domain/builder/`                | Patrón Builder: `ScrimBuilder`                                                     |
| `domain/moderation/`             | Patrón Chain of Responsibility: pipeline de moderación de reportes                 |
| `domain/validation/`             | Patrón Template Method: `GameValidator` con variantes por juego                    |
| `domain/valueobject/`            | Value Objects: `UserRole`, `ReportStatus`, `EmailVerificationStatus`, `WaitlistStatus` |
| `domain/shared/`                 | Utilidades de dominio: `RankScale`                                                 |
| `infrastructure/notification/`   | Observer + Abstract Factory + Adapter: Kafka pub/sub, notifiers, adapters           |
| `infrastructure/repository/`     | Interfaces Spring Data JPA                                                         |
| `infrastructure/persistence/`    | JPA converters para value objects                                                  |
| `config/`                        | Security (JWT + roles), OpenTelemetry, Rate Limiting, Audit Log, filtros           |

---

## 🛠️ Stack tecnológico

| Capa           | Tecnología                          |
|----------------|-------------------------------------|
| Lenguaje       | Java 25                             |
| Framework      | Spring Boot 4.1                     |
| Web            | Spring Web MVC                      |
| Persistencia   | Spring Data JPA + Hibernate         |
| Migraciones    | Flyway                              |
| Base de datos  | PostgreSQL 17                       |
| Mensajería     | Apache Kafka                        |
| Mail           | Spring Boot Starter Mail (JavaMail) |
| Auth           | Spring Security + JWT + OAuth2      |
| Observabilidad | OpenTelemetry + Grafana LGTM stack  |
| Build          | Maven (con wrapper `mvnw`)          |
| Tests          | JUnit 5 + Testcontainers            |
| Contenedores   | Docker Compose                      |

---

## 🎯 Patrones de diseño aplicados

El sistema implementa **9 patrones de diseño**:

| Patrón                       | Tipo           | Aplicación en el sistema                                                                               | Ubicación                          |
|------------------------------|----------------|--------------------------------------------------------------------------------------------------------|------------------------------------|
| **State**                    | Comportamiento | Ciclo de vida del Scrim: `Searching`, `CreatedLobby`, `Confirmed`, `Playing`, `Finished`, `Canceled`   | `domain/state/`                    |
| **Strategy**                 | Comportamiento | Algoritmos de matchmaking intercambiables: por MMR, por latencia, por historial                        | `domain/strategy/`                 |
| **Observer**                 | Comportamiento | Kafka event publishing + `NotificationSubscriber` que reacciona a eventos de dominio                   | `infrastructure/notification/`     |
| **Abstract Factory**         | Creacional     | `NotifierFactory` con implementaciones `DevNotifierFactory` y `ProdNotifierFactory`                    | `infrastructure/notification/`     |
| **Builder**                  | Creacional     | `ScrimBuilder` para armado incremental de lobbies con validaciones y defaults                           | `domain/builder/`                  |
| **Command**                  | Comportamiento | `SwapPlayersCommand` + `CommandExecutor` con soporte de undo                                           | `application/command/`             |
| **Chain of Responsibility**  | Comportamiento | Pipeline de moderación: `AutoResolverNode` → `BotAnalyzerNode` → `HumanModNode`                       | `domain/moderation/`               |
| **Template Method**          | Comportamiento | `GameValidator` con reglas por juego: `StrictGameFormatValidator`, `FlexibleGameFormatValidator`        | `domain/validation/`               |
| **Adapter**                  | Estructural    | `DiscordAdapter`, `SendGridAdapter`, `ICalAdapter` para integraciones externas                         | `infrastructure/notification/`     |

> Los estereotipos (`<<State>>`, `<<Strategy>>`, `<<Observer>>`, etc.) están marcados explícitamente en el diagrama de
> clases.

---

## 🗂️ Modelo de dominio

### Entidades principales

**Player** — usuario del sistema.  
Atributos: `username`, `password`, `preferredRole`, `region`, `platform`, `availability`, `visibleRank`, `rank` (MMR),
`gamesPlayed`, `wins`, `losses`, `kda`.

**Lobby** — sala de espera con cupos.  
Atributos: `scheduledTime`, `maxPlayers`, `minPlayers`, `region`, `minRank`, `maxRank`, `maxPing`, `gameMode`, `map`,
`status`, `host`, `players`.

**Scrim** — el encuentro en sí.  
Atributos: `lobbyId`, `gameMode`, `map`, `startTime`, `endTime`, `status`.

**ScrimStatistics** — resultados post-partida: `redTeam`, `blueTeam`, `winningTeam`.

**Report** — reportes de conducta: `scrim`, `player`, `reason`, `description`, `status`, `reportedAt`, `resolvedAt`,
`resolutionDetails`.

**SavedSearch** — búsquedas guardadas por jugador: `game`, `region`, `minRank`, `maxRank`, `maxLatency`, `format`.

**Waitlist** — lista de espera: jugadores en cola para scrims completos.

**PlayerScrimStats** — estadísticas individuales por jugador por scrim.

### Value Objects

- `UserRole` — roles del sistema: `USER`, `MOD`, `ADMIN`
- `ReportStatus` — estados de reporte
- `EmailVerificationStatus` — estados de verificación de email
- `WaitlistStatus` — estados de lista de espera

---

## 🚀 Cómo correr el proyecto

### Prerrequisitos

- **Java 25** instalado (`java -version`)
- **Docker y Docker Compose** para levantar Postgres, Kafka y el stack de observabilidad
- Maven se incluye via wrapper (`./mvnw`)

### Paso 1 — Levantar la infraestructura

```bash
docker compose up -d
```

Esto inicia tres servicios:

| Servicio                       | Puerto           | Descripción                                      |
|--------------------------------|------------------|--------------------------------------------------|
| `postgres`                     | 5432             | Base de datos principal (`user/secret/postgres`) |
| `broker` (Kafka)               | 9092             | Mensajería para eventos de dominio               |
| `observability` (Grafana LGTM) | 3000, 4317, 4318 | Logs, métricas y traces                          |

Grafana queda disponible en [http://localhost:3000](http://localhost:3000).

### Paso 2 — Compilar y correr la app

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

La aplicación arranca en [http://localhost:8081](http://localhost:8081).

### Paso 3 — Compilar el JAR (opcional)

```bash
./mvnw clean package
java -jar target/final-0.0.1-SNAPSHOT.jar
```

> La configuración base vive en `src/main/resources/application.yaml`.

---

## 📡 Endpoints principales

> Spring Security aplica roles `USER/MOD/ADMIN` mediante un header de autenticación.  
> Los endpoints de auth (register, login, verify-email, oauth) son públicos. El resto requiere autenticación.  
> Se aplica rate limiting a todas las peticiones.

### Autenticación — `AuthController` (`/v1/api/auth`)

| Método | Path                                   | Body                | Descripción                                |
|--------|----------------------------------------|---------------------|--------------------------------------------|
| `POST` | `/v1/api/auth/register`                | `PlayerData`        | Registra un nuevo jugador                  |
| `POST` | `/v1/api/auth/login`                   | `LoginData`         | Autentica y devuelve JWT                   |
| `POST` | `/v1/api/auth/oauth/callback`          | `OAuthCallbackData` | Registra/autentica vía OAuth               |
| `POST` | `/v1/api/auth/{username}/verify-email` | —(query `token`)    | Verifica email del jugador                 |

### Scrims — `ScrimController` (`/v1/api/scrims`)

| Método   | Path                                 | Body                        | Descripción                                           |
|----------|--------------------------------------|-----------------------------|-------------------------------------------------------|
| `POST`   | `/v1/api/scrims`                     | `CreateScrimRequest`        | Crea un lobby/scrim                                   |
| `GET`    | `/v1/api/scrims`                     | Query params                | Busca lobbies por juego, región, rango, fecha, latencia |
| `POST`   | `/v1/api/scrims/{id}/postulaciones`  | `ApplyToScrimRequest`       | Postular jugador a un scrim                           |
| `POST`   | `/v1/api/scrims/{id}/confirmaciones` | `ConfirmParticipationRequest` | Confirmar participación                             |
| `POST`   | `/v1/api/scrims/{id}/acciones/{cmd}` | `SwapPlayersRequest`        | Ejecutar comando (swap/undo) — MOD/ADMIN              |
| `POST`   | `/v1/api/scrims/{id}/iniciar`        | —                           | Iniciar scrim                                         |
| `POST`   | `/v1/api/scrims/{id}/cancelar`       | `CancelScrimRequest`        | Cancelar scrim — MOD/ADMIN                            |
| `POST`   | `/v1/api/scrims/{id}/finalizar`      | —                           | Finalizar scrim                                       |
| `POST`   | `/v1/api/scrims/{id}/reportes`       | `ReportApplication`         | Reportar jugador — USER/MOD/ADMIN                     |
| `POST`   | `/v1/api/scrims/{id}/estadisticas`   | `CreateStatisticsRequest`   | Cargar estadísticas post-partida                      |

### Jugadores — `PlayerController` (`/v1/api/players`)

| Método   | Path                                              | Body                 | Descripción                    |
|----------|---------------------------------------------------|----------------------|--------------------------------|
| `POST`   | `/v1/api/players/{username}/saved-searches`       | `SavedSearchRequest` | Guardar una búsqueda           |
| `GET`    | `/v1/api/players/{username}/saved-searches`       | —                    | Listar búsquedas guardadas     |
| `DELETE` | `/v1/api/players/{username}/saved-searches/{id}`  | —                    | Eliminar una búsqueda guardada |

---

## 📊 Diagramas

Todos los diagramas viven en `diagramas/` en formato **PlantUML** (`.txt`).

### Diagramas de secuencia por caso de uso

| CU    | Descripción                                                 |
|-------|-------------------------------------------------------------|
| CU 1  | Registrar Usuario                                           |
| CU 2  | Autenticar Usuario                                          |
| CU 3  | Crear Scrim *(Builder + Template Method)*                   |
| CU 4  | Postularse a Scrim *(State)*                                |
| CU 5  | Emparejar y crear lobby *(Strategy + Observer)*             |
| CU 6  | Confirmar Emparejamiento                                    |
| CU 7  | Iniciar Scrim *(Scheduler)*                                 |
| CU 8  | Finalizar y cargar estadísticas                             |
| CU 9  | Cancelar Scrim                                              |
| CU 10 | Notificar Eventos *(Observer + Abstract Factory + Adapter)* |
| CU 11 | Moderar Reportes *(Chain of Responsibility)*                |

### Otros diagramas

- `diagramas/Diagrama ER.txt` — Diagrama Entidad-Relación

### Cómo renderizar los diagramas

```bash
# Opción A — VS Code / IntelliJ: instalar extensión PlantUML → Alt+D con el archivo abierto

# Opción B — Online
# Pegar el contenido en: https://www.plantuml.com/plantuml

# Opción C — CLI
java -jar plantuml.jar diagramas/*.txt
```

---

## 🧪 Testing

### Correr los tests

```bash
./mvnw test
```

### Tests implementados

#### Domain — State

| Test                          | Descripción                                    |
|-------------------------------|------------------------------------------------|
| `ScrimStateTransitionsTest`   | Transiciones válidas e inválidas del ciclo      |
| `SearchingStateTest`          | Comportamiento del estado inicial Searching     |
| `OperationalScrimStateTest`   | Estados operacionales (Playing, Confirmed)      |
| `TerminalScrimStateTest`      | Estados terminales (Finished, Canceled)         |
| `StateMessageConventionTest`  | Convenciones de mensajes de error por estado    |

#### Domain — Strategy

| Test                          | Descripción                                    |
|-------------------------------|------------------------------------------------|
| `ByMMRStrategyTest`           | Emparejamiento por MMR                         |
| `ByLatencyStrategyTest`       | Emparejamiento por latencia                    |
| `ByMMRStrategyBenchmarkTest`  | Benchmark de rendimiento (500 candidatos)      |

#### Domain — Value Objects

| Test                  | Descripción                            |
|-----------------------|----------------------------------------|
| `ValueObjectsTest`   | Validación de value objects de dominio |

#### Application — Services

| Test                               | Descripción                                                           |
|------------------------------------|-----------------------------------------------------------------------|
| `AuthServiceTest`                  | Registro, login, verificación email, OAuth                            |
| `ReportServiceTest`                | Flujo de reportes con cadena de moderación                            |
| `ScrimServiceFlowIntegrationTest`  | Flujo completo: crear → postular → confirmar → iniciar → finalizar   |

#### Infrastructure — Notifications

| Test                          | Descripción                                    |
|-------------------------------|------------------------------------------------|
| `NotifierFactoryTest`         | Abstract Factory: creación de notifiers        |
| `NotificationSubscriberTest`  | Observer: suscripción y reacción a eventos     |

---

## 📁 Estructura del repositorio

```
TP-Final-ProcesoDesarrolloSoftware/
├── compose.yaml              # Postgres + Kafka + Grafana LGTM
├── Dockerfile                # Imagen Docker de la app
├── pom.xml                   # Dependencias Maven
├── mvnw, mvnw.cmd            # Maven wrapper
├── README.md
├── docs/
│   └── task.txt, TP-Final-E-Sports-Scrims-Matchmaking.pdf
├── diagramas/
│   ├── CU 1 - Registrar Usuario.txt
│   ├── ...
│   ├── CU 11 - Moderar Reporte.txt
│   └── Diagrama ER.txt
└── src/
    ├── main/
    │   ├── java/com/pds/tp/
    │   │   ├── FinalApplication.java
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java         # JWT + roles USER/MOD/ADMIN
    │   │   │   ├── JwtService.java             # Generación/validación de tokens
    │   │   │   ├── HeaderRoleAuthenticationFilter.java
    │   │   │   ├── RateLimitingFilter.java
    │   │   │   ├── AuditLogFilter.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   ├── OpenTelemetryConfiguration.java
    │   │   │   ├── TopicConfiguration.java
    │   │   │   └── TraceIdFilter.java
    │   │   ├── controller/
    │   │   │   ├── AuthController.java
    │   │   │   ├── ScrimController.java
    │   │   │   └── PlayerController.java
    │   │   ├── application/
    │   │   │   ├── service/        # AuthService, ScrimService, ReportService, MMRRecalculator
    │   │   │   ├── facade/         # ScrimFacade
    │   │   │   ├── command/        # CommandExecutor, SwapPlayersCommand
    │   │   │   ├── dto/            # Request/Response records
    │   │   │   └── scheduler/      # ScrimLifecycleScheduler
    │   │   ├── domain/
    │   │   │   ├── entity/         # Player, Scrim, Lobby, Report, ScrimStatistics,
    │   │   │   │                   #   SavedSearch, Waitlist, PlayerScrimStats
    │   │   │   ├── state/          # ScrimState + 6 estados concretos + ScrimContext
    │   │   │   ├── strategy/       # MatchmakingStrategy + 3 implementaciones
    │   │   │   ├── event/          # ScrimCreatedEvent, ScrimStateChangedEvent
    │   │   │   ├── builder/        # ScrimBuilder
    │   │   │   ├── moderation/     # ModerationNode chain (Auto → Bot → Human)
    │   │   │   ├── validation/     # GameValidator (Template Method) + variantes
    │   │   │   ├── valueobject/    # UserRole, ReportStatus, EmailVerificationStatus, WaitlistStatus
    │   │   │   └── shared/         # RankScale
    │   │   └── infrastructure/
    │   │       ├── notification/   # Kafka pub/sub, NotifierFactory, Adapters
    │   │       ├── repository/     # Spring Data JPA interfaces
    │   │       └── persistence/    # JPA converters para value objects
    │   └── resources/
    │       ├── application.yaml
    │       └── logback-spring.xml
    └── test/
        └── java/com/pds/tp/
            ├── FinalApplicationTests.java
            ├── TestcontainersConfiguration.java
            ├── domain/state/       # 5 tests de estados
            ├── domain/strategy/    # 3 tests de estrategias (incl. benchmark)
            ├── domain/valueobject/ # ValueObjectsTest
            ├── application/service/ # AuthServiceTest, ReportServiceTest, ScrimServiceFlowIntegrationTest
            └── infrastructure/notification/ # NotifierFactoryTest, NotificationSubscriberTest
```

---

## ✅ Estado de avance

### Implementado

- Arquitectura en capas (Controller → Application → Domain → Infrastructure)
- Entidades JPA: `Player`, `Scrim`, `Lobby`, `ScrimStatistics`, `Report`, `SavedSearch`, `Waitlist`, `PlayerScrimStats`
- Value Objects con JPA converters: `UserRole`, `ReportStatus`, `EmailVerificationStatus`, `WaitlistStatus`
- Repositorios Spring Data JPA (8 repositorios)
- `AuthService` con registro, login con JWT, verificación de email y OAuth callback
- `ScrimService` con creación, búsqueda, postulación, confirmación, inicio, cancelación y finalización
- `ReportService` con pipeline de moderación automática
- `ScrimFacade` como punto de entrada unificado para el controller
- `ScrimLifecycleScheduler` para transiciones automáticas por tiempo
- **9 patrones de diseño implementados:**
  - **State** — ciclo de vida del Scrim (6 estados + contexto + resolver)
  - **Strategy** — 3 algoritmos de matchmaking (MMR, Latencia, Historial)
  - **Observer** — Kafka event publish/subscribe + `NotificationSubscriber`
  - **Abstract Factory** — `NotifierFactory` con `DevNotifierFactory` / `ProdNotifierFactory`
  - **Builder** — `ScrimBuilder` con validaciones y defaults
  - **Command** — `SwapPlayersCommand` + `CommandExecutor` con undo
  - **Chain of Responsibility** — Pipeline de moderación (`AutoResolver → BotAnalyzer → HumanMod`)
  - **Template Method** — `GameValidator` con `StrictGameFormatValidator` / `FlexibleGameFormatValidator`
  - **Adapter** — `DiscordAdapter`, `SendGridAdapter`, `ICalAdapter`
- Spring Security con JWT, roles (`USER/MOD/ADMIN`), BCrypt password hashing
- Rate limiting filter y Audit log filter
- 11 casos de uso documentados como diagramas de secuencia en PlantUML
- Diagrama Entidad-Relación
- Stack de observabilidad (OpenTelemetry → Grafana LGTM) integrado
- Suite de tests: 15 clases de test (unitarios, integración y benchmark)
- Docker Compose con Postgres, Kafka y Grafana LGTM
- Dockerfile para la aplicación

### Pendiente

- [ ] Diagrama de clases UML con estereotipos de patrones
- [ ] Diagrama de estados del Scrim
- [ ] Video demo (≤ 5 min)
- [ ] Documento PDF final con carátula

---

## 📄 Licencia

Trabajo académico — UADE 2026
