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
            System.out.println("DATABASE_URL not found, using H2 PERSISTENT storage for local development");
            return DataSourceBuilder.create()
                    .url("jdbc:h2:file:./data/levelup_gamer_dev")
                    .username("sa")
                    .password("")
                    .driverClassName("org.h2.Driver")
                    .build();
        }

        System.out.println("Railway DATABASE_URL found: " + databaseUrl);
        
        try {
            // Parse Railway PostgreSQL URL
            if (databaseUrl.startsWith("postgresql://")) {
                // Extract components from postgresql://user:pass@host:port/db
                String withoutProtocol = databaseUrl.substring("postgresql://".length());
                String[] userHostSplit = withoutProtocol.split("@");
                String[] userPassSplit = userHostSplit[0].split(":");
                
                String username = userPassSplit[0];
                String password = userPassSplit[1];
                String hostPortDb = userHostSplit[1];
                
                String jdbcUrl = "jdbc:postgresql://" + hostPortDb;
                
                System.out.println("Parsed components:");
                System.out.println("- Username: " + username);
                System.out.println("- Password: [HIDDEN]");
                System.out.println("- JDBC URL: " + jdbcUrl);
                
                return DataSourceBuilder.create()
                        .url(jdbcUrl)
                        .username(username)
                        .password(password)
                        .driverClassName("org.postgresql.Driver")
                        .build();
            }
            
            // If already JDBC format, use as-is
            return DataSourceBuilder.create()
                    .url(databaseUrl)
                    .driverClassName("org.postgresql.Driver")
                    .build();
                    
        } catch (Exception e) {
            System.err.println("Error parsing DATABASE_URL: " + e.getMessage());
            System.err.println("DATABASE_URL was: " + databaseUrl);
            e.printStackTrace();
            throw new RuntimeException("Failed to configure database", e);
        }
    }
}