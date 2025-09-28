FROM openjdk:17-jdk-slim
COPY target/smart-edu-platform-1.0.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]