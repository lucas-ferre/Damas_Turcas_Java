.PHONY: all build run test clean docker-build docker-run

all: build

build:
	mkdir -p bin
	javac -encoding UTF-8 -d bin -sourcepath src/main/java $(shell find src/main/java -name "*.java")

run: build
	java -cp bin br.com.damas.turcas.Main

test:
	mkdir -p bin
	javac -encoding UTF-8 -d bin -sourcepath src/main/java $(shell find src/main/java -name "*.java") src/test/java/br/com/damas/turcas/GameAndAITests.java
	java -cp bin br.com.damas.turcas.GameAndAITests

clean:
	rm -rf bin target

docker-build:
	docker build -t damas-turcas:latest .

docker-run:
	docker run --rm -it damas-turcas:latest
