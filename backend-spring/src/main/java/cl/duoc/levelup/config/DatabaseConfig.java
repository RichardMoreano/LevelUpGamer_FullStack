package cl.duoc.levelup.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            System.out.println("DATABASE_URL not found, using MySQL for local development");
            return DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:3306/levelup_gamer?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC")
                    .username("root")
                    .password("admin")
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .build();
        }

        System.out.println("Railway DATABASE_URL found: " + databaseUrl);
        
        try {
            // Simple regex replacement for Railway URL
            if (databaseUrl.startsWith("postgresql://")) {
                String jdbcUrl = databaseUrl.replace("postgresql://", "jdbc:postgresql://");
                System.out.println("Converted to JDBC URL: " + jdbcUrl);
                
                return DataSourceBuilder.create()
                        .url(jdbcUrl)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            }
            
            // If already JDBC format, use as-is
            return DataSourceBuilder.create()
                    .url(databaseUrl)
                    .driverClassName("org.postgresql.Driver")
                    .build();
                    
        } catch (Exception e) {
            System.err.println("Error configuring database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to configure database", e);
        }
    }
}