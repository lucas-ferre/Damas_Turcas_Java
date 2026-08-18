.PHONY: all build run test clean docker-build docker-run install-java

all: build

build:
	@if command -v javac >/dev/null 2>&1; then \
		mkdir -p bin && \
		javac -encoding UTF-8 -d bin -sourcepath src/main/java $$(find src/main/java -name "*.java"); \
	elif command -v docker >/dev/null 2>&1; then \
		echo "javac não encontrado. Construindo via Docker..."; \
		docker build -t damas-turcas:latest .; \
	else \
		echo "Erro: Nem javac nem docker encontrados. Instale o Java ou execute via Docker."; \
		exit 1; \
	fi

run:
	@if command -v javac >/dev/null 2>&1; then \
		mkdir -p bin && \
		javac -encoding UTF-8 -d bin -sourcepath src/main/java $$(find src/main/java -name "*.java") && \
		java -cp bin br.com.damas.turcas.Main; \
	elif command -v docker >/dev/null 2>&1; then \
		echo "javac não encontrado no host. Executando via Docker..."; \
		docker build -t damas-turcas:latest . && \
		docker run --rm -it damas-turcas:latest; \
	else \
		echo "Erro: Nem java nem docker disponíveis."; \
		exit 1; \
	fi

test:
	@if command -v javac >/dev/null 2>&1; then \
		mkdir -p bin && \
		javac -encoding UTF-8 -d bin -sourcepath src/main/java $$(find src/main/java -name "*.java") src/test/java/br/com/damas/turcas/GameAndAITests.java && \
		java -cp bin br.com.damas.turcas.GameAndAITests; \
	elif command -v docker >/dev/null 2>&1; then \
		echo "Executando testes no contêiner Docker..."; \
		docker build -t damas-turcas:latest . && \
		docker run --rm --entrypoint java damas-turcas:latest -cp bin br.com.damas.turcas.GameAndAITests; \
	else \
		echo "Erro: Nem javac nem docker disponíveis."; \
		exit 1; \
	fi

install-java:
	sudo apt-get update && sudo apt-get install -y openjdk-21-jdk

clean:
	rm -rf bin target

docker-build:
	docker build -t damas-turcas:latest .

docker-run:
	docker run --rm -it damas-turcas:latest
