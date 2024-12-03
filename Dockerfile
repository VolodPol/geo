FROM openjdk:22
LABEL authors="vova"

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} /usr/src/geo/app.jar
WORKDIR /usr/src/geo

ENTRYPOINT ["java","-jar","app.jar"]