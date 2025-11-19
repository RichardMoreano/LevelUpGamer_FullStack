FROM eclipse-temurin:17-jdk-alpine

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy the backend project
COPY backend-spring/ /app/

# Build the application
RUN mvn clean package -DskipTests -q

# Expose port
EXPOSE 8080

# Set environment variables for Railway
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx400m -Xms200m"

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar target/levelup-gamer-0.0.1-SNAPSHOT.jar"]