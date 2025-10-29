# Use the official OpenJDK 17 image as the base
FROM openjdk:21

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/rest-server-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8081
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]