@echo off
where docker >nul 2>nul
if %errorlevel% equ 0 (
    echo Iniciando Damas Turcas via Docker...
    docker build -t damas-turcas:latest .
    docker run --rm -it damas-turcas:latest
    goto end
)

where java >nul 2>nul
if %errorlevel% equ 0 (
    echo Docker nao encontrado. Executando nativamente com Java...
    if not exist bin mkdir bin
    dir /s /b src\main\java\*.java > sources.txt
    javac -d bin @sources.txt
    del sources.txt
    java -cp bin br.com.damas.turcas.Main
    goto end
)

echo Erro: Nem Docker nem Java foram encontrados no PATH.
:end
