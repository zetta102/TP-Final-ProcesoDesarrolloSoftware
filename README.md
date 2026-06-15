# eScrims 🎮

> Plataforma de organización de scrims y partidas amistosas de eSports (Valorant, LoL, CS2)
> con emparejamiento inteligente, ciclo de vida por estados y notificaciones multi-canal.

**Materia:** Proceso de Desarrollo de Software — UADE
**Comisión:** Jueves Noche — 2026 1C
**Entrega final:** 18/06/2026
**Repositorio:** https://github.com/zetta102/TP-Final-ProcesoDesarrolloSoftware

---

## 👥 Integrantes

| Nombre y Apellido | LU |
|---|---|
| Indriago Ramírez, Miguel | 1196929 |
| Maver, Joaquin | 1030299 |
| Posse Presa, Matias | 1055352 |
| Sanchez Carrizo, Naiara | 1196621 |
| Sierra, Jose | 1193916 |

---

## 📋 Descripción del sistema

eScrims permite a jugadores y organizadores de eSports:

- **Registrarse y autenticarse** en la plataforma (estándar y OAuth simulado).
- **Crear scrims** definiendo juego, formato (1v1, 3v3, 5v5), región, rango mínimo/máximo, latencia máxima, fecha/hora y modalidad.
- **Buscar y postularse** a scrims compatibles con su perfil.
- **Emparejar jugadores** mediante algoritmos intercambiables (por MMR, latencia o historial).
- **Gestionar el ciclo de vida del scrim:**
  `Buscando → LobbyArmado → Confirmado → EnJuego → Finalizado / Cancelado`
- **Recibir notificaciones** por múltiples canales (Push, Email, Discord, iCal) ante eventos clave.
- **Cargar estadísticas** y reportar conducta al finalizar.
- **Moderar reportes** automáticamente mediante una cadena de responsabilidad.

---

## 🏗️ Arquitectura

El proyecto sigue una arquitectura en capas **MVC + Dominio**:

```
┌──────────────────────────────────────────────────────────┐
│  Controller  →  REST (Spring Web)                        │
│               AuthController, ScrimController            │
├──────────────────────────────────────────────────────────┤
│  Service     →  Lógica de aplicación / casos de uso      │
│               AuthService, ScrimService, ReportService   │
├──────────────────────────────────────────────────────────┤
│  Domain      →  Entidades, estados, strategies,          │
│               patrones de negocio, eventos de dominio    │
├──────────────────────────────────────────────────────────┤
│  Infra       →  Repositorios JPA, Kafka,                 │
│               Notificaciones, OpenTelemetry              │
└──────────────────────────────────────────────────────────┘
```

### Paquetes principales

| Paquete | Responsabilidad |
|---|---|
| `controller` | Expone los endpoints REST y delega en los servicios |
| `service` | Orquesta los casos de uso |
| `domain/entity` | Entidades JPA: `Player`, `Scrim`, `Lobby`, `ScrimStatistics`, `Report`, `Waitlist`, `SavedSearch` |
| `domain/state` | Patrón State: `ScrimContext`, `ScrimState` y 6 estados concretos |
| `domain/strategy` | Patrón Strategy: `ByMMRStrategy`, `ByLatencyStrategy`, `ByHistoryStrategy` |
| `domain/builder` | Patrón Builder: `ScrimBuilder` |
| `domain/validation` | Patrón Template Method: `GameValidator` y subclases |
| `domain/moderation` | Patrón Chain of Responsibility: `ModerationNode` y nodos concretos |
| `domain/event` | Eventos de dominio: `ScrimStateChangedEvent`, `ScrimCreatedEvent` |
| `domain/valueobject` | Value Objects: `UserRole`, `EmailVerificationStatus`, `ReportStatus`, `WaitlistStatus` |
| `infrastructure/notification` | Observer + Abstract Factory + Adapter: `NotificationSubscriber`, `NotifierFactory`, adapters |
| `repository` | Interfaces Spring Data JPA |
| `config` | Spring Security, OpenTelemetry, filtros de trace ID |

---

## 🎯 Patrones de diseño aplicados

El sistema implementa **9 patrones de diseño** (mínimo requerido: 4).

| Patrón | Categoría | Aplicación en eScrims | Paquete |
|---|---|---|---|
| **State** | Comportamiento | Ciclo de vida del Scrim: 6 estados con transiciones automáticas | `domain/state` |
| **Strategy** | Comportamiento | Algoritmos de matchmaking intercambiables (MMR, latencia, historial) | `domain/strategy` |
| **Observer** | Comportamiento | `NotificationSubscriber` escucha `ScrimStateChangedEvent` vía `@EventListener` | `infrastructure/notification` |
| **Abstract Factory** | Creacional | `NotifierFactory` crea notifiers por canal y entorno (Dev/Prod) | `infrastructure/notification` |
| **Adapter** | Estructural | `DiscordAdapter`, `SendGridAdapter`, `ICalAdapter` adaptan servicios externos | `infrastructure/notification` |
| **Builder** | Creacional | `ScrimBuilder` construye un `Lobby` paso a paso con validaciones | `domain/builder` |
| **Template Method** | Comportamiento | `GameValidator` define el esqueleto de validación; subclases implementan reglas por juego | `domain/validation` |
| **Chain of Responsibility** | Comportamiento | Pipeline de moderación: AutoResolver → BotAnalyzer → HumanMod | `domain/moderation` |
| **Facade** | Estructural | `ScrimFacade` simplifica el acceso al subsistema de scrims desde el controller | `service` |

---

## 🔄 Diagrama de estados del Scrim

```
                    [*]
                     │
                     ▼  crear scrim (ScrimBuilder.build())
              ┌─────────────┐
              │  Buscando   │ ◄─────────────────────────────┐
              │<<Searching>>│   timeout / jugador baja       │
              └──────┬──────┘                               │
                     │ [cupo completo]                      │
                     ▼                                      │
           ┌──────────────────┐                            │
           │   Lobby armado   │ ───────────────────────────┘
           │<<CreatedLobby>>  │
           └────────┬─────────┘
                    │ [todos confirman]
                    ▼
           ┌──────────────────┐
           │   Confirmado     │
           │<<Confirmed>>     │
           └────────┬─────────┘
                    │ [fecha/hora alcanzada]
                    ▼
           ┌──────────────────┐
           │    En juego      │
           │<<Playing>>       │
           └────────┬─────────┘
                    │ [finalizar()]
                    ▼
           ┌──────────────────┐
           │   Finalizado     │
           │<<Finished>>      │
           └────────┬─────────┘
                    ▼
                   [*]

   Cancelado ◄── cancelar() desde Buscando, LobbyArmado o Confirmado
   <<Canceled>>
       │
       ▼
      [*]
```

El diagrama completo en formato PlantUML se encuentra en `diagramas/DiagramaDeEstados_Scrim.puml`.

---

## 📐 Diagrama de clases

El diagrama de clases UML con todos los estereotipos de patrones se encuentra en:

`diagramas/DiagramaDeClases.puml`

Incluye los 8 paquetes del dominio:
`domain.entity` · `domain.valueobject` · `domain.state` · `domain.strategy` · `domain.builder` · `domain.validation` · `domain.moderation` · `domain.event` · `infrastructure.notification`

---

## 📂 Casos de uso

El sistema implementa **11 casos de uso**, documentados como diagramas de secuencia PlantUML en `diagramas/casosDeUso/`:

| CU | Descripción | Patrones involucrados |
|---|---|---|
| CU-01 | Registrar Usuario | — |
| CU-02 | Autenticar Usuario | — |
| CU-03 | Crear Scrim | Builder + Template Method |
| CU-04 | Postularse a Scrim | State |
| CU-05 | Emparejar y crear lobby | Strategy + State |
| CU-06 | Confirmar Emparejamiento | State |
| CU-07 | Iniciar Scrim | State + Scheduler |
| CU-08 | Finalizar y cargar estadísticas | State |
| CU-09 | Cancelar Scrim | State |
| CU-10 | Notificar Eventos | Observer + Abstract Factory + Adapter |
| CU-11 | Moderar Reportes | Chain of Responsibility |

---

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.1 |
| Web | Spring Web MVC |
| Persistencia | Spring Data JPA + Hibernate |
| Migraciones | Flyway |
| Base de datos | PostgreSQL 17 |
| Mensajería | Apache Kafka |
| Notificaciones | JavaMail (Email), Firebase Mock (Push), Discord Webhook, iCal RFC 5545 |
| Auth | Spring Security + OAuth2 Client |
| Observabilidad | OpenTelemetry + Grafana LGTM stack |
| Build | Maven (con wrapper `mvnw`) |
| Tests | JUnit 5 + Testcontainers |
| Contenedores | Docker Compose |

---

## 🚀 Cómo correr el proyecto

### Prerrequisitos

- **Java 25** (`java -version`)
- **Docker y Docker Compose**
- Maven incluido via wrapper (`./mvnw`)

### Paso 1 — Levantar la infraestructura

```bash
docker compose up -d
```

| Servicio | Puerto | Descripción |
|---|---|---|
| `postgres` | 5432 | Base de datos (`user/secret/postgres`) |
| `broker` (Kafka) | 9092 | Mensajería para eventos de dominio |
| `observability` (Grafana LGTM) | 3000, 4317, 4318 | Logs, métricas y traces |

Grafana disponible en http://localhost:3000

### Paso 2 — Compilar y correr la app

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

La aplicación arranca en http://localhost:8080

### Paso 3 — Correr los tests

```bash
./mvnw test
```

---

## 📡 Endpoints principales

### Autenticación — `AuthController`

| Método | Path | Descripción |
|---|---|---|
| `POST` | `/auth/register` | Registra un nuevo jugador |
| `POST` | `/auth/login` | Autentica usuario y contraseña |
| `POST` | `/auth/{username}/verify-email` | Verifica el email con token |
| `POST` | `/auth/oauth/callback` | Registro/login vía OAuth simulado |

### Scrims — `ScrimController`

| Método | Path | Descripción |
|---|---|---|
| `POST` | `/createLobby` | Crea un lobby (usa Builder + Template Method) |
| `GET` | `/findLobbies` | Busca lobbies por región, rango y latencia |
| `POST` | `/{id}/startScrim` | Inicia el scrim (transición a EnJuego) |
| `POST` | `/{id}/cancelLobby` | Cancela el lobby |
| `POST` | `/{id}/finishScrim` | Finaliza el scrim |
| `POST` | `/{id}/postulaciones` | Postula un jugador al scrim |
| `POST` | `/{id}/confirmaciones` | Confirma participación de un jugador |
| `GET` | `/{id}/estadisticas` | Consulta estadísticas del scrim |
| `POST` | `/{id}/reportes` | Reporta conducta de un jugador (inicia Chain of Responsibility) |

---

## 📁 Estructura del repositorio

```
TP-Final-ProcesoDesarrolloSoftware/
├── compose.yaml
├── pom.xml
├── mvnw / mvnw.cmd
├── README.md
├── diagramas/
│   ├── DiagramaDeClases.puml          ← Diagrama de clases con estereotipos
│   ├── DiagramaDeEstados_Scrim.puml   ← Diagrama de estados del Scrim
│   └── casosDeUso/
│       ├── CU_1_-_Registrar_Usuario.txt
│       ├── CU_2_-_Autorizar_Usuario.txt
│       ├── CU_3_-_Crear_Scrim.txt
│       ├── CU_4_-_Postularse_a_Scrim.txt
│       ├── CU_5_-_Emparejar_y_crear_lobby.txt
│       ├── CU_6_-_Confirmar_Emparejamiento.txt
│       ├── CU_7_-_Iniciar_Scrim.txt
│       ├── CU_8_-_Finalizar_y_cargar_estadísticas.txt
│       ├── CU_9_-_Cancelar_Scrim.txt
│       ├── CU_10_-_Notificar_Eventos.txt
│       └── CU_11_-_Moderar_Reporte.txt
└── src/
    ├── main/
    │   ├── java/com/pds/tp/
    │   │   ├── FinalApplication.java
    │   │   ├── config/
    │   │   ├── controller/
    │   │   ├── domain/
    │   │   │   ├── entity/
    │   │   │   ├── state/
    │   │   │   ├── strategy/
    │   │   │   ├── builder/
    │   │   │   ├── validation/
    │   │   │   ├── moderation/
    │   │   │   ├── event/
    │   │   │   └── valueobject/
    │   │   ├── infrastructure/
    │   │   │   └── notification/
    │   │   ├── model/
    │   │   ├── repository/
    │   │   └── service/
    │   └── resources/
    │       ├── application.yaml
    │       ├── logback-spring.xml
    │       └── db/migration/
    └── test/
        └── java/com/pds/tp/
```

---

## 🧪 Testing

```bash
./mvnw test
```

| Test | Tipo | Descripción |
|---|---|---|
| `FinalApplicationTests` | Smoke | Verifica que el contexto Spring levanta correctamente |
| `TestcontainersConfiguration` | Integración | Bootstrap de Testcontainers con PostgreSQL real |
| `ScrimStateTransitionsTest` | Unitario | Transiciones del patrón State |
| `ByMMRStrategyTest` | Unitario | Algoritmo de matchmaking por MMR |
| `NotifierFactoryTest` | Unitario | Creación de notifiers por canal y entorno |

---

## 🔔 Notificaciones y retry

El `NotificationSubscriber` implementa reintentos con backoff exponencial:

- Máximo **3 intentos** por notificación
- Backoff: `200ms → 400ms → 800ms`
- Canales soportados: **Push** (Firebase mock), **Email** (SendGrid), **Discord** (webhook), **iCal** (RFC 5545)
- Los eventos también se publican a **Kafka** para consumidores downstream asíncronos

---

## ✅ Estado de entregables

| Entregable | Estado |
|---|---|
| Diagrama de clases UML con estereotipos | ✅ `diagramas/DiagramaDeClases.puml` |
| Diagrama de estados del Scrim | ✅ `diagramas/DiagramaDeEstados_Scrim.puml` |
| Modelo de dominio y 11 casos de uso documentados | ✅ `diagramas/casosDeUso/` |
| Código fuente (Controller–Service–Domain–Infra) | ✅ `src/` |
| README | ✅ Este archivo |
| Suite de tests y evidencias | ✅ `src/test/` |

---

## 📄 Licencia

Trabajo académico — UADE 2026
