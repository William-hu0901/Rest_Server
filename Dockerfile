# Use the official OpenJDK 17 image as the base
FROM openjdk:21

# Set the working directory
WORKDIR /app

# Download ADOT Java Agent
ADD https://github.com/aws-observability/aws-otel-java-instrumentation/releases/latest/download/aws-opentelemetry-agent.jar /app/aws-opentelemetry-agent.jar


# Copy the JAR file into the container
COPY target/rest-server-0.0.1-SNAPSHOT.jar /app/app.jar

# set environment variables
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/aws-opentelemetry-agent.jar"
ENV OTEL_SERVICE_NAME="daodao-restful-api"
ENV OTEL_SERVICE_VERSION="1.0.0"
ENV OTEL_RESOURCE_ATTRIBUTES="service.name=daodao-restful-api,service.version=1.0.0,deployment.environment=production"


# Expose port 8081
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]