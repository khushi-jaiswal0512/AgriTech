# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Copy the entire project
COPY . .

# Ensure maven wrapper is executable
RUN chmod +x mvnw

# Accept the module name as a build argument
ARG MODULE_NAME

# Build only the specified module and its dependencies
RUN ./mvnw clean package -pl ${MODULE_NAME} -am -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

ARG MODULE_NAME

# Copy the built jar from the build stage
COPY --from=build /app/${MODULE_NAME}/target/*.jar app.jar

# Copy the config-repo folder so config-server can find it locally
COPY --from=build /app/config-repo /app/config-repo

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
