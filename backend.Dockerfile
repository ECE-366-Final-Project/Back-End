# syntax=docker/dockerfile:1

# Dockerfile for building CooperCasino Maven
# James Ryan

#stage1 build
#FROM maven:3.9.6-eclipse-temurin-21 AS build
#ADD . /project
#WORKDIR /project
#RUN mvn -T 4 -e package

#stage2 run
FROM eclipse-temurin:latest
#COPY --from=build /project/target/cooper-casino.jar /app/cooper-casino.jar
ADD ./target /app
EXPOSE 8080
ENTRYPOINT java -jar /app/cooper-casino.jar
