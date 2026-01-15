FROM gradle:8.7-jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8098
ENTRYPOINT ["java", "-jar", "app.jar"]