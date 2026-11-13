FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/blood-donor-tracker-1.0.0.jar app.jar
ENV ADMIN_PASSWORD=ChangeMe123
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
