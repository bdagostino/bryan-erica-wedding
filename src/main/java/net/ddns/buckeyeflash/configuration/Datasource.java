package net.ddns.buckeyeflash.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile(value = {"dev"})
public class Datasource {

    @Bean
    public DataSource pooledDataSource() throws URISyntaxException {
        final URI dbUri = new URI(System.getenv("DATABASE_URL"));
        final String[] userInfoArray = StringUtils.split(dbUri.getUserInfo(),':');
        final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";

        final PGSimpleDataSource pgSimpleDataSource = new PGSimpleDataSource();
        pgSimpleDataSource.setUrl(dbUrl);
        pgSimpleDataSource.setConnectTimeout(2000);
        pgSimpleDataSource.setUser(userInfoArray[0]);
        pgSimpleDataSource.setPassword(userInfoArray[1]);

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDataSource(pgSimpleDataSource);
        hikariConfig.setMaximumPoolSize(10);
        return new HikariDataSource(hikariConfig);
    }
}
