$ErrorActionPreference = "Stop"

if (Get-Command docker -ErrorAction SilentlyContinue) {
    Write-Host "Iniciando Damas Turcas via Docker..." -ForegroundColor Cyan
    docker build -t damas-turcas:latest .
    docker run --rm -it damas-turcas:latest
} elseif (Get-Command java -ErrorAction SilentlyContinue) {
    Write-Host "Docker nao encontrado. Executando nativamente com Java..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path bin | Out-Null
    javac -d bin -sourcepath src/main/java (Get-ChildItem -Path src/main/java -Filter *.java -Recurse | ForEach-Object { $_.FullName })
    java -cp bin br.com.damas.turcas.Main
} else {
    Write-Host "Erro: Nem Docker nem Java foram encontrados no PATH." -ForegroundColor Red
}
