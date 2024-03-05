# syntax=docker/dockerfile:1

# Dockerfile for building CooperCasino Maven
# James Ryan

#stage0 deps
FROM maven:3.9.6-eclipse-temurin-21 AS deps
ADD pom.xml /project/pom.xml
WORKDIR /project
RUN mvn dependency:go-offline

#stage1 build
FROM deps AS build
ADD . /project
WORKDIR /project
RUN mvn -T 4 -e package

#stage2 run
FROM eclipse-temurin:latest
COPY --from=build /project/target/cooper-casino.jar /app/cooper-casino.jar
#COPY ./target/cooper-casino.jar /app
EXPOSE 8080
ENTRYPOINT java -jar /app/cooper-casino.jar
