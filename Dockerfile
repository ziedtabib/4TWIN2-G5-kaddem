FROM openjdk:17-jdk-slim
COPY target/4TWIN2-G5-KADDEM-1.0-RELEASE.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "/app.jar"]  
