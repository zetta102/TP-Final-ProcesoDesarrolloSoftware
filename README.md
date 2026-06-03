TPO Final — eScrims
Plataforma de organización de scrims y partidas amistosas de eSports (Valorant, LoL, CS2, etc.) con emparejamiento por rango/MMR, latencia e historial, ciclo de vida del scrim gestionado por estados, y notificaciones multi-canal.
Materia: Proceso de Desarrollo de Software — UADEComisión: Jueves Noche — 2026 1CEntrega final: 18/06
Integrantes
Nombre y Apellido	LU
Indriago Ramírez, Miguel	1196929
Maver, Joaquin	1030299
Posse Presa, Matias	1055352
Sanchez Carrizo, Naiara	1196621
Sierra, Jose	1193916
Tabla de contenidos
Descripción del sistema
Arquitectura
Stack tecnológico
Patrones de diseño aplicados
Modelo de dominio
Cómo correr el proyecto
Endpoints principales
Diagramas
Testing
Estructura del repositorio
Estado de avance
1) Descripción del sistema
eScrims permite a jugadores y organizadores de eSports:
Registrarse y autenticarse.
Crear scrims definiendo juego, formato (1v1, 3v3, 5v5), región, rango mínimo/máximo, latencia máxima, fecha/hora y modalidad.
Buscar y postularse a scrims compatibles con su perfil.
Emparejar jugadores mediante algoritmos intercambiables (por MMR, por latencia, por historial).
Gestionar el ciclo de vida del scrim (Buscando → LobbyArmado → Confirmado → EnJuego → Finalizado / Cancelado).
Recibir notificaciones por múltiples canales (Push, Email, Discord) ante eventos clave.
Cargar estadísticas y reportar conducta al finalizar.
2) Arquitectura
El proyecto sigue una arquitectura en capas inspirada en MVC + Dominio:
┌─────────────────────────────────────────────────┐
│  Controller   →  REST (Spring Web)              │
├─────────────────────────────────────────────────┤
│  Service      →  Lógica de aplicación / casos   │
│                  de uso, orquestación           │
├─────────────────────────────────────────────────┤
│  Domain       →  Entidades, estados, strategies │
│                  patrones de negocio            │
├─────────────────────────────────────────────────┤
│  Infra        →  Repositorios JPA, Kafka,       │
│                  Mail, OAuth, OpenTelemetry     │
└─────────────────────────────────────────────────┘
Controller (com.pds.tp.controller) — Expone los endpoints REST y delega en los servicios.
Service (com.pds.tp.service) — Orquesta los casos de uso (AuthService, ScrimService).
Entity (com.pds.tp.entity) — Entidades JPA (Player, Scrim, Lobby, ScrimStatistics, Report).
Model (com.pds.tp.model) — DTOs (records) de entrada/salida.
Repository (com.pds.tp.repository) — Interfaces Spring Data JPA.
Config (com.pds.tp.config) — Spring Security, OpenTelemetry, filtros de trace ID.
3) Stack tecnológico
Capa	Tecnología
Lenguaje	Java 25
Framework	Spring Boot 4.1
Web	Spring Web MVC
Persistencia	Spring Data JPA + Hibernate
Migraciones	Flyway
Base de datos	PostgreSQL 17
Mensajería	Apache Kafka
Mail	Spring Boot Starter Mail (JavaMail)
Auth	Spring Security + OAuth2 Client
Observabilidad	OpenTelemetry + Grafana LGTM stack
Build	Maven (con wrapper mvnw)
Tests	JUnit 5 + Testcontainers
Contenedores	Docker Compose
4) Patrones de diseño aplicados
El enunciado pide mínimo 4 patrones con foco en State, Strategy, Observer y Abstract Factory/Facade.
Patrón	Tipo	Aplicación en el sistema	Ubicación en el código
State	Comportamiento	Ciclo de vida del Scrim: Buscando, LobbyArmado, Confirmado, EnJuego, Finalizado, Cancelado	Pendiente: domain/state/
Strategy	Comportamiento	Algoritmos de matchmaking intercambiables: por MMR, por latencia, por historial	Pendiente: domain/matchmaking/
Observer	Comportamiento	DomainEventBus con suscriptores que reaccionan a eventos de dominio (cambio de estado, lobby armado, etc.)	Pendiente: domain/events/
Abstract Factory	Creacional	NotifierFactory crea notifiers por canal (Push/Email/Discord) y entorno (Dev/Prod)	Pendiente: infra/notifications/
Builder (opcional)	Creacional	ScrimBuilder para armado incremental de un Scrim con validaciones	Pendiente
Command (opcional)	Comportamiento	AsignarRolCommand, SwapJugadoresCommand con undo antes de confirmar	Pendiente
Adapter (opcional)	Estructural	Integración con APIs externas: Discord, SendGrid, iCal	Pendiente
Template Method (opcional)	Comportamiento	GameValidator con reglas de composición que varían por juego	Pendiente
Chain of Responsibility (opcional)	Comportamiento	Pipeline de moderación de reportes (auto-resolver → bot → moderador humano)	Pendiente
Trazabilidad: los diagramas de secuencia en diagramas/casosDeUso/ ya muestran cómo intervienen estos patrones en cada caso de uso. Los estereotipos (<<State>>, <<Strategy>>, <<Factory>>, etc.) están marcados explícitamente en el diagrama de clases.
5) Modelo de dominio
Entidades principales
Player — usuario del sistema. Atributos: username, password, preferredRole, region, platform, availability, visibleRank, rank (MMR), estadísticas históricas (gamesPlayed, wins, losses, kda).
Lobby — sala de espera con cupos. Atributos: scheduledTime, maxPlayers, minPlayers, region, minRank, maxRank, maxPing, gameMode, map, status, host, players.
Scrim — el encuentro en sí. Atributos: lobbyId, gameMode, map, startTime, endTime, status.
ScrimStatistics — resultados post-partida: redTeam, blueTeam, winningTeam.
Report — reportes de conducta: scrim, player, reason, description, status, reportedAt, resolvedAt, resolutionDetails.
Entidades pendientes (según enunciado, sección 6)
Postulacion — relación Jugador ↔ Scrim con rolDeseado y estado (Pendiente/Aceptada/Rechazada).
Confirmacion — confirmación individual por jugador para pasar de LobbyArmado → Confirmado.
Notificacion — registro de notificaciones enviadas por canal.
6) Cómo correr el proyecto
Prerrequisitos
Java 25 instalado (java -version).
Docker y Docker Compose para levantar Postgres, Kafka y el stack de observabilidad.
Maven se incluye via wrapper (./mvnw).
Paso 1 — Levantar la infraestructura
docker compose up -d
Esto inicia tres servicios:
Servicio	Puerto	Descripción
postgres	5432	Base de datos principal (user/secret/postgres)
broker (Kafka)	9092	Mensajería para eventos de dominio
observability (Grafana LGTM)	3000, 4317, 4318	Logs, métricas y traces
Grafana queda disponible en http://localhost:3000.
Paso 2 — Compilar y correr la app
Linux / macOS:
./mvnw spring-boot:run
Windows:
mvnw.cmd spring-boot:run
La aplicación arranca en http://localhost:8080.
Paso 3 — Compilar el JAR (opcional)
./mvnw clean package
java -jar target/final-0.0.1-SNAPSHOT.jar
Configuración
La configuración base vive en src/main/resources/application.yaml. Los endpoints de OpenTelemetry (localhost:4318) y la base de datos (localhost:5432) ya están apuntando a los contenedores levantados por compose.yaml.
7) Endpoints principales
Nota: Spring Security está actualmente abierto (permitAll) para facilitar desarrollo. El refuerzo con roles USER/MOD/ADMIN y rate limiting está pendiente (ver sección 11).
Autenticación (AuthController)
Método	Path	Body	Descripción
POST	/auth/register	PlayerData	Registra un nuevo jugador
POST	/auth/login	LoginData	Autentica usuario y password
Scrims (ScrimController)
Método	Path	Body	Descripción
POST	/createLobby	LobbyData	Crea un lobby asociado a un host
POST	/startScrim	ScrimData	Inicia un scrim si hay cupos suficientes
GET	/findLobbies	FindLobbyData	Busca lobbies por región, rango y latencia
POST	/{id}/cancelLobby	—	Cancela un lobby en estado Started
POST	/{id}/finishScrim	—	Finaliza un scrim en estado Started
POST	/{id}/postulaciones	Pendiente	Postular jugador a un scrim
POST	/{id}/confirmaciones	Pendiente	Confirmar participación
GET	/{id}/estadisticas	Pendiente	Consultar estadísticas del scrim
8) Diagramas
Todos los diagramas viven en la carpeta diagramas/ en formato PlantUML (.puml / .txt).
Diagramas de secuencia por caso de uso
Cubren los 11 casos de uso del enunciado y se encuentran en diagramas/casosDeUso/:
CU 1 — Registrar Usuario
CU 2 — Autenticar Usuario
CU 3 — Crear Scrim (Builder + Template Method)
CU 4 — Postularse a Scrim (State)
CU 5 — Emparejar y crear lobby (Strategy + Observer)
CU 6 — Confirmar Emparejamiento
CU 7 — Iniciar Scrim (Scheduler)
CU 8 — Finalizar y cargar estadísticas
CU 9 — Cancelar Scrim
CU 10 — Notificar Eventos (Observer + Abstract Factory + Adapter)
CU 11 — Moderar Reportes (Chain of Responsibility)
Otros diagramas
diagramas/DiagramaDeClases.puml — Pendiente: diagrama de clases UML completo con estereotipos de patrones.
diagramas/DiagramaDeEstados.puml — Pendiente: diagrama de estados del Scrim.
Cómo renderizar los diagramas
Opción A — VS Code: instalar la extensión PlantUML y usar Alt+D con el archivo abierto.
Opción B — Online: pegar el contenido del archivo en plantuml.com/plantuml.
Opción C — CLI:
java -jar plantuml.jar diagramas/casosDeUso/*.txt
9) Testing
Tests actuales en src/test/java/com/pds/tp/:
FinalApplicationTests — smoke test de contexto Spring.
TestcontainersConfiguration — bootstrap de Testcontainers para integración.
Plan de tests pendiente (según enunciado, sección 13)
Unitarios: ByMMRStrategyTest, ScrimStateTransitionsTest, NotifierFactoryTest.
Integración: flujo completo crearScrim → postular → armarLobby → confirmar → iniciar → finalizar.
Carga: emparejamiento de 500 candidatos en menos de 2 segundos.
Correr los tests
./mvnw test
10) Estructura del repositorio
TP-Final-ProcesoDesarrolloSoftware/
├── compose.yaml                     # Postgres + Kafka + Grafana LGTM
├── pom.xml                          # Dependencias Maven
├── mvnw, mvnw.cmd                   # Maven wrapper
├── README.md
├── diagramas/
│   └── casosDeUso/                  # Diagramas de secuencia (.txt)
│       ├── CU 1 - Registrar Usuario.txt
│       ├── ...
│       └── CU 11 - Moderar Reporte.txt
└── src/
    ├── main/
    │   ├── java/com/pds/tp/
    │   │   ├── FinalApplication.java
    │   │   ├── config/              # Security, OpenTelemetry, filtros
    │   │   ├── controller/          # AuthController, ScrimController
    │   │   ├── entity/              # Player, Scrim, Lobby, ScrimStatistics, Report
    │   │   ├── model/               # DTOs (records)
    │   │   ├── repository/          # Interfaces Spring Data JPA
    │   │   └── service/             # AuthService, ScrimService
    │   └── resources/
    │       ├── application.yaml
    │       └── logback-spring.xml
    └── test/
        └── java/com/pds/tp/
11) Estado de avance
✅ Implementado
Esqueleto Spring Boot + Maven + Docker Compose.
Entidades JPA: Player, Scrim, Lobby, ScrimStatistics, Report.
Repositorios Spring Data JPA.
AuthService con registro y autenticación básica.
ScrimService con creación de lobby, inicio, búsqueda, cancelación y finalización.
11 casos de uso documentados como diagramas de secuencia en PlantUML.
Stack de observabilidad (OpenTelemetry → Grafana LGTM) integrado.
🚧 En progreso / pendiente
Diagrama de clases UML con estereotipos de patrones.
Diagrama de estados del Scrim.
Implementación en código de los 4 patrones obligatorios (State, Strategy, Observer, Abstract Factory).
Endpoints postulaciones, confirmaciones y estadisticas (hoy son TODO).
Entidades Postulacion, Confirmacion, Notificacion.
Hashing de passwords (BCrypt) y roles USER/MOD/ADMIN en Spring Security.
Suite completa de tests unitarios, de integración y de carga.
Video demo (≤5 min).
Documento PDF final con carátula.
Entregables (según consigna)
✅ / 🚧 Diagrama de clases UML con estereotipos de patrones.
🚧 Diagrama de estados del scrim.
✅ / 🚧 Modelo de dominio y casos de uso documentados.
✅ / 🚧 Código fuente (Controller–Service–Domain–Infra) y README.
🚧 Suite de tests y evidencias.
🚧 Video demo (≤5 min).
Licencia
Trabajo académico — UADE 2026
