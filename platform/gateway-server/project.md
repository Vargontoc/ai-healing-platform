# Gateway Server

## Description
Este servicio es el Punto de Entrada Único (Edge Server) de la plataforma. Se encarga de la seguridad perimetral, el enrutamiento dinámico hacia los microservicios y aplicaciones del sistema. Gestiona también los límites de consumo (Rate Limiting) para proteger los costos de la IA.

## Stack Tech
- Spring Cloud Gateway
- Spring Security OAUth2 / JWT
- Resilience4j (Circuit Breaker para evitar fallos en cascada si un microservicio falla)
- Micrometer + Zipkin / Brave para trazar una petición desde el front hasta el Agente

## Backlog
El Gateway debe exponer las siguiente rutas base:
/api/v1/incidents/** -> Incident Manager Service
/api/v1/agents/** -> Agent Orchestrator Service
/api/v1/analysis/** -> Document Analysis Service (Pending Implementation)
/auth/** -> Auth Service

## Security Policies
JWT Relay: El Gateway ddebe propagar el token de autenticación a todos los microservicios y aplicaciones mediante el filtro TokenRelay
AI Rate Limiter: Implementar un limite de peticiones por usuarios a los endpoints de IA para prevenir el "Token Draining"
CORS Policy: Configuracdo estrictamente para permitir unicamente el origen del platform-web
