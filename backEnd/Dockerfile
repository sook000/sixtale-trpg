# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-alpine

# 필요 패키지 설치 (xargs 포함)
RUN apk add --no-cache findutils

# Set the working directory
WORKDIR app

# Copy the Gradle wrapper and project files
COPY . .

# Ensure the Gradle wrapper script has execution permissions
# RUN chmod +x ./gradlew

# Build the project
# RUN ./gradlew bootJar

# 빌드 아티팩트 확인 (디버깅용)
# RUN ls -la build/libs

# Copy the packaged jar file
# COPY build/libs/*.jar app.jar

#RUN chmod +x ./gradlew && \
#    ./gradlew bootJar

# RUN ls -la build/libs

# COPY /app/build/libs/*.jar app.jar

RUN chmod +x ./gradlew && \
    ./gradlew bootJar && \
    ls -la build/libs && \
    cp /app/build/libs/*.jar app.jar

# Expose port 8888
EXPOSE 8888

# Run the Spring Boot application
CMD ["java", "-jar", "app.jar"]
