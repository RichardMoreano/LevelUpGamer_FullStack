package cl.duoc.levelup.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("railway")
public class RailwayDataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        System.out.println("Original DATABASE_URL: " + databaseUrl);
        
        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            // Convert Railway DATABASE_URL format to JDBC URL format
            databaseUrl = databaseUrl.replace("postgresql://", "jdbc:postgresql://");
            System.out.println("Converted DATABASE_URL: " + databaseUrl);
        }
        
        if (databaseUrl == null) {
            throw new RuntimeException("DATABASE_URL environment variable is not set");
        }
        
        return DataSourceBuilder.create()
                .url(databaseUrl)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}