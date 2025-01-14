# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the application JAR
COPY target/*.jar app.jar

# Expose the application's port
EXPOSE 9595

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
