#!/bin/bash
set -e

if command -v docker >/dev/null 2>&1; then
    echo "Iniciando Damas Turcas via Docker..."
    docker build -t damas-turcas:latest .
    docker run --rm -it damas-turcas:latest
elif command -v java >/dev/null 2>&1; then
    echo "Docker não encontrado. Executando nativamente com Java..."
    mkdir -p bin
    javac -encoding UTF-8 -d bin -sourcepath src/main/java $(find src/main/java -name "*.java")
    java -cp bin br.com.damas.turcas.Main
else
    echo "Erro: Nem Docker nem Java foram encontrados no PATH."
    exit 1
fi
