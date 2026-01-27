# Project Roadmap & Status Report

Date: 2026-01-16
Status: **STABLE** (Phoenix Protocol Complete)

## 1. Project Overview & Current State
The platform consists of 5 microservices. Source code and tests have been fully recovered following the Phoenix Protocol. The system is operational and ready for demo.

| Service | Status | Tests | Description |
| :--- | :--- | :--- | :--- |
| **Document Analyser** | 🟢 **READY** | ✅ | Fully refactored, functional API (`/api/v1/analysis`). |
| **Infrastructure Server** | 🟢 **RECOVERED** | ✅ | Source restored. Compiles. Tests Pass. |
| **Gateway Server** | 🟢 **RECOVERED** | ✅ | Source restored. Config/Security restored. Tests Pass. |
| **Incident Manager** | 🟢 **RECOVERED** | ✅ | Source restored. CRUD/Stats logic restored. Tests Pass. |
| **Agent Orchestrator** | 🟢 **RECOVERED** | ✅ | Source restored. Agent Tools/Service restored. Tests Pass. |

## 2. Immediate Recovery Plan (Phoenix Protocol)
To make the platform viable for a demo, we must execute an emergency recovery plan.

### Priority 1: Infrastructure & Gateway
- **Infrastructure**: Re-initialize Spring Boot project with Eureka Server and Config Server. restore `application.yml` configs.
- **Gateway**: Re-initialize with Spring Cloud Gateway. Restore `SecurityConfig` (OAuth2/JWT), `GatewayConfig` (RateLimit), and routes.

### Priority 2: Core Business Services
- **Incident Manager**: Re-implement `Incident` entity, `IncidentRepository`, `IncidentService` (CRUD + Stats), and `IncidentController`.
- **Agent Orchestrator**: Re-implement `AgentService`, `Tools` (IncidentAnalysis, KnowledgeBase), and integration logic.

## 3. Viable Demo Checklist
Once code is recovered, the following features constitute a viable demo:
1.  **System Startup**: Infrastructure and Gateway start without errors.
2.  **Incident Lifecycle**: Create, Read, Update Incidents via API.
3.  **AI Analysis**: Send document to Document Analyser and get response.
4.  **Agent Action**: Agent Orchestrator can query Incident Service (mocked or real).

## 4. Future Improvements & Features
After recovery, we will focus on:
-   **Observability**: Centralized logging (ELK/Loki) and Tracing (Zipkin/Jaeger).
-   **Security**: Full OAuth2 flow implementation with Keycloak.
-   **Resilience**: Retry/Circuit Breaker fine-tuning.
-   **Frontend**: Develop `platform-web` to consume these APIs.



AGENT-ORCHESTRATOR-SERVICE
❌ FALTANTES CRÍTICOS
TestRunnerTool - NO IMPLEMENTADO

Según PROJECT.md debe tener:
runTests(moduleName)
runSingleTest(moduleName, testClassName, testMethodName)
Sin esto el agente NO puede ejecutar tests automáticamente
IncidentManagerClient - NO EXISTE

Necesita Feign Client para comunicarse con incident-manager-service
El tool analyzeIncident no tiene forma de crear tickets reales
Memoria Persistente en PostgreSQL - NO IMPLEMENTADO

Actualmente usa InMemoryAgentMemory
Según PROJECT.md debe usar PostgreSQL para:
Guardar historial de razonamiento
Evitar repetir soluciones fallidas
Context persistente entre ejecuciones
Endpoint /api/v1/analysis/ - MAPEO INCORRECTO

Gateway espera: /api/v1/analysis/**
Document Analyser tiene: /api/v1/analysis (sin wildcard)
Feign Client busca: /api/v1/documents/search
Hay inconsistencia en las rutas
DOCUMENT-ANALYSER-SERVICE
❌ FALTANTES CRÍTICOS
DocumentAnalysisService está STUB

Solo retorna string básico sin usar LLM
No integra Spring AI para análisis real
Necesita:
Llamar a ChatClient para generar resúmenes
Usar RAG para contexto
Procesar análisis de errores
Endpoint /api/v1/analysis/** - RUTA DESAJUSTADA

Gateway enruta a /api/v1/analysis/**
Pero el controller está en /api/v1/analysis (sin subdivisión)
Agent busca en /api/v1/documents/search
INCIDENT-MANAGER-SERVICE
❌ FALTANTES CRÍTICOS
De-duplicación SHA-256 - NO IMPLEMENTADO

PROJECT.md indica: "normalizar stacktrace y generar hash SHA-256"
Actual: campo stackTraceHash existe pero no se usa
Sin esto se crearán incidentes duplicados
Lógica de Incremento de Ocurrencias - NO IMPLEMENTADO

Si llega hash existente → incrementar contador
Actualizar lastOccurrence
NO crear nuevo ticket
GATEWAY-SERVER
✅ CORRECTO PERO...
Rutas Inconsistentes
/api/v1/analysis/** → document-analyser-service
Pero document-analyser usa /api/v1/documents/ y /api/v1/analysis
Necesita alineación
🚀 PLAN DE ACCIÓN PARA MVP
Prioridad CRÍTICA (Sin esto NO funciona la demo)

1. ✅ TestRunnerTool (Agent Orchestrator)
   - Implementar MCP tool que ejecute `mvn test`
   - Parsear resultados de Surefire
   - Retornar stacktraces al agente

2. ✅ IncidentManagerClient (Agent Orchestrator)
   - Feign Client a /api/v1/incidents
   - Método createIncident(serviceName, errorType, stackTrace)
   - Método getIncidentByHash(hash)

3. ✅ Hash SHA-256 + De-duplicación (Incident Manager)
   - En IncidentService.createIncident():
     - Normalizar stacktrace (quitar líneas, números)
     - Generar SHA-256
     - Buscar hash existente
     - Si existe → incrementar, actualizar timestamp
     - Si no → crear nuevo

4. ✅ Alinear Rutas API
   - Document Analyser: mover todo a /api/v1/analysis/**
   - O cambiar Gateway a /api/v1/documents/**
   - Decidir una convención y aplicarla

5. ✅ DocumentAnalysisService Real
   - Integrar ChatClient de Spring AI
   - Hacer RAG: buscar docs + generar respuesta
   - Tipos: SUMMARY, ERROR_ANALYSIS, SUGGESTION
Prioridad ALTA (Mejora significativa para demo)

6. ⚠️ Memoria Persistente PostgreSQL (Agent Orchestrator)
   - Entity: AgentExecutionHistory
   - Campos: contextId, goal, steps, result, timestamp
   - Permitir al agente "recordar" intentos previos

7. ⚠️ Endpoint de Demo Completo
   - POST /api/v1/agents/analyze-and-fix
   - Parámetros: serviceId, testClassName
   - Flow completo: run test → analyze → search KB → create incident
💡 MEJORAS PARA HACER EL PRODUCTO INTERESANTE
Nivel 1: Quick Wins (1-2 días)
1. Dashboard Web Reactivo

- Frontend en React/Vue
- Visualización en tiempo real de:
  ✓ Agentes ejecutándose (WebSocket)
  ✓ Incidentes creados/resueltos
  ✓ Estadísticas (gráficos)
  ✓ Logs del agente (reasoning steps)
2. Agente con Reasoning Explicable

- Añadir campo "reasoning" en AgentMemory
- Retornar paso a paso:
  ✓ "Paso 1: Leí el stacktrace..."
  ✓ "Paso 2: Busqué en KB y encontré..."
  ✓ "Paso 3: Analicé el código en..."
  ✓ "Conclusión: El error es causado por..."
- Permite al usuario entender QUÉ hizo el agente
3. Auto-Fix con Pull Request

- Integrar con Git (JGit library)
- Si agente encuentra solución:
  1. Crear branch
  2. Aplicar fix
  3. Commit
  4. Crear PR (GitHub API)
- Usuario solo revisa y aprueba
4. Notificaciones

- Slack/Discord webhook
- Email cuando:
  ✓ Test falla
  ✓ Agente propone solución
  ✓ Incidente crítico detectado
Nivel 2: Game Changers (1 semana)
5. Multi-Agent Collaboration

- Agente Analizador: detecta el problema
- Agente Buscador: consulta KB + Google
- Agente Fixer: propone código
- Agente Revisor: valida la solución

Coordinator: orquesta los 4 agentes
6. Knowledge Base Enriquecida

- Scrapers automáticos:
  ✓ StackOverflow (vía API)
  ✓ GitHub Issues similares
  ✓ Documentación oficial
- Ingesta automática semanal
- Ranking de soluciones (por éxito histórico)
7. Self-Learning Loop

1. Agente propone solución
2. Usuario acepta/rechaza
3. Si acepta → marcar solución como válida
4. Si rechaza → ajustar embeddings (RLHF ligero)
5. Próxima vez → priorizar soluciones aceptadas
8. Incident Impact Analysis

- Grafo de dependencias entre servicios
- Si falla incident-manager:
  ✓ Afecta a agent-orchestrator
  ✓ Prioridad: CRÍTICA
- Si falla document-analyser:
  ✓ Afecta a agents (sin KB)
  ✓ Prioridad: ALTA
- Clasificación automática de severidad
Nivel 3: Innovación (2-3 semanas)
9. Predictive Healing

- ML Model entrenado con histórico
- Predice fallos ANTES de que ocurran:
  ✓ "CPU al 80% → en 2h fallará el servicio"
  ✓ "Pattern detectado → test va a fallar pronto"
- Agente actúa PROACTIVAMENTE
10. Costos de IA Optimizados

- Cache de embeddings (Redis)
- Modelo local (Ollama) para tareas simples
- GPT-4 solo para análisis complejos
- Dashboard de costos por agente/servicio
11. Plugin System

- Marketplace de "Agent Skills"
- Community plugins:
  ✓ JavaScriptAnalyzer
  ✓ KubernetesHealthChecker
  ✓ DatabaseQueryOptimizer
- Hot-reload de nuevos tools sin restart
12. Chaos Engineering Integrado

- Agente "Chaos Monkey":
  1. Introduce fallo controlado
  2. Observa cómo reaccionan otros agentes
  3. Valida que se auto-reparen
  4. Genera reporte de resiliencia
- Pruebas automatizadas de self-healing
13. Multi-Tenant con Isolation

- Cada cliente tiene:
  ✓ Su propia KB
  ✓ Sus propios agentes
  ✓ Datos aislados
- Portal SaaS:
  ✓ Sign up → onboarding automático
  ✓ Integrar repos GitHub
  ✓ Pay-per-agent pricing
🎬 DEMO SCRIPT IDEAL (Con MVP completo)

┌─────────────────────────────────────────────────────────┐
│ ESCENA 1: Test Falla                                    │
└─────────────────────────────────────────────────────────┘
1. Developer hace push con bug
2. CI/CD ejecuta tests → FALLA
3. Webhook notifica a Agent Orchestrator

┌─────────────────────────────────────────────────────────┐
│ ESCENA 2: Agente Analiza                                │
└─────────────────────────────────────────────────────────┘
1. Agente lee stacktrace con SourceCodeTool
2. Identifica: "NullPointerException en UserService:42"
3. Lee código fuente del método
4. Reasoning: "La variable 'user' es null porque no se valida"

┌─────────────────────────────────────────────────────────┐
│ ESCENA 3: Busca en Knowledge Base                       │
└─────────────────────────────────────────────────────────┘
1. KnowledgeBaseTool busca: "NullPointerException UserService"
2. Encuentra doc: "Always validate user != null before .getEmail()"
3. Contexto recuperado: "Best practice: Optional<User>"

┌─────────────────────────────────────────────────────────┐
│ ESCENA 4: Crea Incidente (dedup)                        │
└─────────────────────────────────────────────────────────┘
1. IncidentTool calcula hash del stacktrace
2. Busca hash en incident-manager
3. Hash NO existe → Crea nuevo incident
4. Si existiera → Incrementa contador + actualiza timestamp

┌─────────────────────────────────────────────────────────┐
│ ESCENA 5: Propone Solución                              │
└─────────────────────────────────────────────────────────┘
1. Agente genera fix:
   if (user == null) throw new IllegalArgumentException("User cannot be null");
2. Retorna al usuario con explicación
3. [OPCIONAL] Crea Pull Request automático

┌─────────────────────────────────────────────────────────┐
│ ESCENA 6: Dashboard Visualiza                           │
└─────────────────────────────────────────────────────────┘
1. Dashboard muestra:
   - Incidente creado (badge rojo)
   - Reasoning del agente (timeline)
   - Solución propuesta (diff code)
   - Link a PR (si auto-fix habilitado)
📊 COMPARATIVA: MVP vs Full Product
Feature	MVP (Demo)	Enhanced (Interesante)	Innovation
Tiempo	3-5 días	+1-2 semanas	+2-3 semanas
Test Execution	✅ Manual trigger	✅ Auto en CI/CD	✅ Predictive
Knowledge Base	✅ Manual upload	✅ Auto-scraping	✅ Self-learning
Incident Dedup	✅ SHA-256 hash	✅ + ML similarity	✅ + Impact graph
Agent Memory	✅ PostgreSQL	✅ + Embeddings	✅ + RLHF
Solutions	✅ Suggestions	✅ Auto-PR	✅ + Validation
UI	❌ REST API	✅ Dashboard	✅ + WebSocket real-time
Notifications	❌ None	✅ Slack/Email	✅ + Smart routing
Multi-Agent	❌ Single	❌ Single	✅ Collaboration
Pricing	-	-	✅ SaaS Multi-tenant
🎯 RECOMENDACIÓN FINAL
Para Demo INMEDIATA (esta semana):

Implementar SOLO los 5 críticos:
1. TestRunnerTool
2. IncidentManagerClient  
3. Hash + De-duplicación
4. Alinear rutas API
5. DocumentAnalysisService real

TIEMPO: ~2-3 días
IMPACTO: Demo funcional end-to-end
Para Demo IMPRESIONANTE (próximas 2 semanas):

MVP + Quick Wins:
- Dashboard web reactivo
- Reasoning explicable
- Auto-fix con PR
- Notificaciones Slack

TIEMPO: +1 semana después de MVP
IMPACTO: "WOW factor" para inversores/clientes
Para Producto COMERCIALIZABLE (1-2 meses):

Todo lo anterior + Game Changers:
- Multi-agent collaboration
- Self-learning loop
- Incident impact analysis
- Predictive healing (básico)

TIEMPO: +2-3 semanas después
IMPACTO: Producto vendible, diferenciado en mercado