FROM openjdk:8-jdk-alpine

COPY target/*.jar app.jar

RUN apk update && apk add gcc && apk add musl-dev && apk add libc-dev

ENTRYPOINT ["java", "-jar", "/app.jar"]