# AI Healing Platform

## Description
Plataforma basada en Spring Cloud que actua como orquestador de microservicios. Cada microservicio registrado el administrador pude asignarle un Agente que será el encargado de ejecutar las tareas con memoria contextual. Este agente asignado se encargará: 

- Ejecutar suites de tests de forma autonoma de los test del microservicio que este asignado
- Analizar fallos de ejecuciión.
- Generar tickets de incidencia con analisis de causa raiz y sugerencias de soluciones.
- Centralizar estos tickets en un Central Incident Service.

## Tech Stack
- Java 21
- Spring Boot
- Spring Cloud
- Spring AI + LangChain4j (para la gestion de Agentes y herramientas)
- PostgreSQL con pgvector (memoeria largo plazo de los agentes)
- REST para comandos, RabbitMQ para eventos de fallos

## Architecture
platform/infraestructure-server/: Spring Cloud + Discovery Server (Eureka)
platform/gateway-server/: Spring Cloud Gateway
platform/agent-orchestrator-service/: Gestiona el ciclo de vida de los agentes y la ejecucion de tests
platform/incident-manager-service/: Microservicio general que centraliza y deduplica los tickets generados por los agentes. Este micro tiene que estar activo para que los agentes puedan generar tickets y ejecutar tests.
platform-web/: Interfaz de usuario para gestionar los microservicios y los agentes.

## Agentic Workflows
Trigger: El agente recibe una señal de "Check Health"
Tool Use: El agente llama a una funcion runTests() que ejecuta las suites de tests del microservicio o aplicacion asignada.
Reasoning: Si los tests fallan, el agente extrae el stacktrace y lo compara con su history en pgvector
Action: Si no hay ticket similar activo, llama al Inicident Service para crear uno nuevo. Si hay un ticket similar activo, llama al Inicident Service para actualizarlo.

## Custom Instructions
Arquitectura Hexagonal: Mantener la lógica de IA y ejecución de tests dfuera de las entidades de dominio.
Resilencia: Implementar Resilience4j en las llamadas entre el Orquestador y el Incident Manager.
Documentación: Cada endpoint debe estar documentado en Swagger/OpenAPI automáticamente
Logging: Usar logs estructurados en JSON para facilitar el parsing de la IA

## License