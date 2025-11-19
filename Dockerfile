FROM eclipse-temurin:17-jdk

# Install Maven
RUN apt-get update && apt-get install -y maven curl && rm -rf /var/lib/apt/lists/*

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
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# Create a non-root user
RUN useradd -m -u 1001 appuser
RUN chown -R appuser:appuser /app
USER appuser

# Health check script
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/health || exit 1

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar target/levelup-gamer-0.0.1-SNAPSHOT.jar"]