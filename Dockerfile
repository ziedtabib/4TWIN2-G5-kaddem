FROM openjdk:17-jdk-slim
COPY target/4TWIN2-G5-kaddem-1.0.0.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app.jar"]
