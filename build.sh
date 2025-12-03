#!/bin/bash

# Script para compilar el proyecto antes de desplegarlo en Render

echo "Compilando el proyecto Spring Boot..."
cd backend-spring
mvn clean package -DskipTests

echo "Compilacion terminada"
