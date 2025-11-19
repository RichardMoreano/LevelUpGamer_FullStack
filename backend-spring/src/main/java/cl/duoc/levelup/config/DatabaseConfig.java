package cl.duoc.levelup.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() throws URISyntaxException {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl == null) {
            // Fallback for local development
            return DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:3306/levelup_gamer?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC")
                    .username("root")
                    .password("admin")
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .build();
        }

        System.out.println("DATABASE_URL found: " + databaseUrl);
        
        // Parse Railway PostgreSQL URL
        URI dbUri = new URI(databaseUrl);
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();
        
        System.out.println("Parsed JDBC URL: " + jdbcUrl);
        System.out.println("Username: " + username);
        
        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}