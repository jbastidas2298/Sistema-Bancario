Sistema Bancario - Full Stack
Sistema bancario completo desarrollado con arquitectura full stack utilizando tecnologías modernas.

* Tecnologías
Frontend: Angular 15

Backend: Java 21 + Spring Boot

Base de datos: SQL Server 2022

Contenedores: Docker + Docker Compose

API Testing: Postman

* Instalación Rápida
Requisitos previos:

Docker Desktop instalado y en ejecución

Levantar servicios:

docker-compose up -d 

- Puertos
Frontend: http://localhost:4200

Backend: http://localhost:8098

- Credenciales de Prueba
Usuario: 1317706123
Clave: 1234

- Base de Datos
Nombre de la BD: bd_transaction

Si la base de datos no se crea automáticamente, ejecutar dentro del contenedor SQL Server:

bash
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Alt!ora2015" -C -Q "CREATE DATABASE IF NOT EXISTS bd_transaction"

- Estructura del Proyecto
├── full-stack-bp-frontend/     # Aplicación Angular
├── full-Stack-BP/              # Backend Spring Boot
├── database/                   # Scripts SQL
└── docker-compose.yml          # Orquestación Docker

- Testing API
El proyecto incluye colección de Postman para pruebas de endpoints.

- Notas Importantes
Asegurar que la BD bd_transaction exista para el correcto funcionamiento

Todos los servicios se ejecutan en contenedores Docker

La aplicación está completamente containerizada
