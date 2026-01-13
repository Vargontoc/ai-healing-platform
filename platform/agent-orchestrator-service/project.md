# Agent Orchestrator Service

## Description
Este es el Cerebro Operativo de la plataforma. Su función es coordinar agentes autónomos que observan, testean y proponen correcciones para los microservicios y aplicaciones del ecosistema.

## Tech Stack
- Spring Boot  + Java 21 (Virtual Thread para concurrencia de agentes)
- LangChain4j
- Integracion personalizada con OpenAI o Anthropic via Spring AI
- Spring Boot Actuator + Micrometer para monitorización de los agentes

## MCP Tools
Implementar las siguientes interfaces:
- SourceCodeTool:
    - readFile(path): lee el contenido de un archivo de un microservicio
    - listFiles(directory): explora la estructura del proyecto
- TestRunnerTool:
    - runTests(moduleName): ejecuta las suites de tests del microservicio o aplicación
    - runSingleTest(moduleName, testClassName, testMethodName): ejecuta un solo test
- KnowledgeBaseTool:
    - queryDocumentation(query): consulta al document-analyzer (RAG) para buscar patrones de solución
- IncidentTool:
    - createIncident(jsonTool): crea un incidente en el incident-manager 

## Memory and context
Cada agente tiene su propia memoria contextual persistente:
- ContextID: el id del microservicio o aplicación que está analizando
- Session Storage: Usa PostgreSQL para almacenar el historial de razonamiento previos. Si el agente ya intentó arreglar un bug y falló, debe saberlo para no repetir el mismo error.

## System prompt
Eres un ingeniero de Staff especializado en SRE y Calidad. Tu objetivo es minimizar el tiempo de inactividad. Cuando un test falla, tu flujo dene ser:
1. Analizar el stacktrace del fallo
2. Leer las clases involucradas en el fallo
3. Buscar en la base de conocimiento si es un error conocido
4. Si no hay ticket similar activo, llama al Inicident Service para crear uno nuevo. Si hay un ticket similar activo, llama al Inicident Service para actualizarlo

## Structure Packages
- es.vargontoc.platform.agent.domain: Lógica de decisión del agente
- es.vargontoc.platform.agent.mcp.tools: Implementacion de los tools del MCP
- es.vargontoc.platform.agent.config: Configuración de modelos de IA y parametros
- es.vargontoc.platform.agent.infrastructure: Feign Clients para llamadas externas   
