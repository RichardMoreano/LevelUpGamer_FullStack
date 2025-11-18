FROM eclipse-temurin:17-jdk

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy the backend project
COPY backend-spring/ /app/

# Build the application
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Set environment variables for Railway
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application with proper JVM settings for Railway
CMD ["java", "-Xmx512m", "-Xms256m", "-Dserver.port=${PORT:-8080}", "-jar", "target/levelup-gamer-0.0.1-SNAPSHOT.jar"]