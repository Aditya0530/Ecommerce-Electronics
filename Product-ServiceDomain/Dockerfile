FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/Product-ServiceDomain-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9292


ENTRYPOINT ["java", "-jar", "app.jar"]