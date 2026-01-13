# Infrastructure Server

## Description
Este modulo actua como el sistema nervioso central del ecosistema. Proporciona dos capacidades críticas:
1. Config Server: Gestión centralizada de propiedades (.yml) para todos los microservicios permitiendo cambios en caliente.
2. Service Discovery (Eureka): Registro y localización dinámica de servicios para que la IA y el gateway no necesiten IPs estáticas.

## Stack Tech
- Spring Cloud Config Server
- Spring Cloud Eureka Server
- Git

## Agent Configuration
El agente debe asegurarse de que: 
- Busque los archivos en una carpeta específica (/config-repo) donde estarán los perfiles default, dev, prod
- Eureka Server esté configurado en modo "Standalone" para desarrollo sin intentar replicarse a si mismo
- Ambos servicios deben tener habilitado Spring boot Actuator para que el gateway sepa cuando están listos.
