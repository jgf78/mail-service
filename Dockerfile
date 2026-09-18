#FROM openjdk:17-jdk-slim
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/mail-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
