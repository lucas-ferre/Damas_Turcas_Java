FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY src ./src
RUN mkdir -p bin && \
    javac -encoding UTF-8 -d bin -sourcepath src/main/java $(find src/main/java -name "*.java")

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /app/bin ./bin

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

ENTRYPOINT ["java", "-cp", "bin", "br.com.damas.turcas.Main"]
