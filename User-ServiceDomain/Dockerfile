FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/User-ServiceDomain-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9294


ENTRYPOINT ["java", "-jar", "app.jar"]