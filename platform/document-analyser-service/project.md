# Document Analyser Service

## Description
Motor de Generacio n Aumentada por Recuperacion (RAG Engine). Su funcion es procesar documentación técnica, manuales y logs, transformarlos en representaciones vectoriales y permitir que los agentes de IA consulten esta base de conocimientos para encontrar soluciones a errores basadas en experiencia previa.

## Stack Tech
- Spring Boot
- Spring AI (módulo Vector Stores)
- PostgreSQL con la extension pgvector
-  Modelos: Embeddings (OpenAI), locales con Ollama, LLM para resumenes GPT-4o-mini
- Apache Tika para extraer texto de documentos


# Capabilities
- Endpoint para subir archivos que automaticamente dividen en fragmentos y vectorizan
- Endopoint que recibe una consulta en lenguaje natural y devuelve los fragmentos de texto relevantes por similitud
Cada fragmento guardado debe estar vinculado a un service_id y version permitiendo búsquedas filtradas por proyecto

# Agent Strategy
Debe configurar TokenTextSplitter de Spring AI con los siguientes paámetros por defecto.
- Chunk Size: 1000 tokens
- Chunk Overlap: 200 tokens



