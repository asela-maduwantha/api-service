FROM hseeberger/scala-sbt:eclipse-temurin-11.0.14.1_1.6.2_2.12.15 AS builder

WORKDIR /app
COPY . .
RUN sbt clean assembly

FROM eclipse-temurin:11-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/scala-2.13/api-service.jar ./app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
