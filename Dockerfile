FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8080
ARG APP_NAME=task-management-system
ARG APP_VERSION=0.0.1-SNAPSHOT
ARG JAR_FILE=build/libs/${APP_NAME}-${APP_VERSION}.jar
COPY ${JAR_FILE} ./app.jar
ENTRYPOINT ["java","-jar","./app.jar"]