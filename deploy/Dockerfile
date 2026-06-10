FROM gradle:9.2.1-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
RUN chmod +x ./gradlew

COPY src ./src
RUN ./gradlew clean bootJar --no-daemon || ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 18080

ENTRYPOINT ["java", "-jar", "app.jar"]
