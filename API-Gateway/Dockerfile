FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/API-Gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9999


ENTRYPOINT ["java", "-jar", "app.jar"]