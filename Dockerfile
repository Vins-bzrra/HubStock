FROM maven:3.9.9-amazoncorretto-23-al2023 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


FROM openjdk:23

WORKDIR /app

COPY --from=build ./app/target/*.jar ./hubstock-api.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/hubstock-api.jar"]
