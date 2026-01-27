# Platform Web (Microservices + AI Manager)

## Description
Esta es la interfaz de control de la plataforma. Su proposito es visualizar la salud de los microservicios, interactual con los agentes de IA en tiempo real y gestionar los incidentes reportados. Debe sentirse como una herramienta de trabajo pero tambien con una UI amigable y agradable para el usuario final.

## Stack Tech
- Vue 3
- Vite
- Pinia
- TailwindCSS + Shadcn UI
- TanStack Query para sincronización con los microservicios
- Lucide Vue Next para iconos y websockets (o Server-Sent Events) para ver al agente "escribiendo" o "testando"
- vue-diff para mostrar las sugerencias de fix de la IA

## Main Views (Feature)
1. Service Map: Un grid o grafo simple que muestra el estado (UP / Down) de cada microservicio registrado en Eureka.
2. Agent Console: Una terminal simulada donde puedes ver el log de razonamiento del agent-orchestrator.
3. Incident Feed: Lista de tickets provenientes del incident manager, ordenados por fecha de creación. Al hacer click en un ticket, se muestra el detalle del incidente con la explicación de la IA y las sugerencias de fix.
4. Knowledge Explorer: Buscador para consultar la base de conocimiento RAG del document-analyzer.
5. Real time Viewer: Ver en tiempo real los agentes ejecutandose, los logs que generan y las respuestas que obtienen.

# Gateway Integration
- Front-End nunca habla directamente con los microservicios. Todas las llamadas van al puerto del api-gateway.
- Implementar un AuthContext para manejar el JWT que el Gateway exige.