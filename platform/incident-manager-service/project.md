# Incident Manager Service

## Description
Este microservicio centraliza todas las incidencias, fallos de tests y sugerencias de mejora generadas por los agentes autónomos de la plataforma. Es el encargado de evitar que el sistema se inunde con tickets duplicados y de proporcionar una API limpia para que el Dashboard visualice el estados de los servicios y aplicaciones.

## Stack Tech
- Spring Boot
- PostgreSQL
- Redis para de-duplicación rápida y rate-limiting de tickets
- Spring Cloud Stream + RabbitMQ para escuchar eventos de fallo de otros micros
- Swagger / OpenAPI para que otros agentes entiendan la API de incidendencias

## Logic Business
- El agente debe normalizar el stacktrace y generar un hash SHA-256
- Si llega un nuevo incidente con un hash existente, el servicio no cre un ticket nuevo, sino que incrementa el contador de ocurrencias y actualiza el timestamp 

