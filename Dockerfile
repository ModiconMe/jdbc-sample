FROM openjdk:17-jdk-alpine
COPY /build/libs/web-template.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]