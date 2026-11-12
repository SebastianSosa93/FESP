# Requisitos

## Para compilar y ejecutar el proyecto se necesitan:

-Java JDK 23 o compatible.

-Apache Maven.

-Ollama instalado y en ejecución.

-El modelo llama3.2:1b descargado en Ollama.


## Configuración de Ollama

Una vez instalado Ollama, abrir una terminal y ejecutar: 

**ollama pull llama3.2:1b**

Este comando descarga el modelo utilizado por FESP para la generación de código HTML.
Ollama debe permanecer en ejecución durante el uso del programa, ya que Java se comunica con su API local mediante HTTP.

## Compilación y ejecución

Desde la carpeta raíz del proyecto, ejecutar: 

**mvn clean compile**
