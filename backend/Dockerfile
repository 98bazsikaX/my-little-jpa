FROM eclipse-temurin:22-jdk AS build

WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .
COPY src ./src

# Download Maven manually since Maven image may not have JDK 26 tag yet
RUN apt-get update && apt-get install -y maven && \
    mvn package -DskipTests -q

FROM eclipse-temurin:22-jre AS runtime

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
