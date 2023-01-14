package com.mndk.bouncerate.config;

import com.mndk.bouncerate.db.AdvertisementProductDAO;
import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@ComponentScan(basePackageClasses = AdvertisementProductDAO.class)
@PropertySource("file:application.properties")
@SuppressWarnings("unused")
public class JdbiConfiguration {

    @Value("${bouncerate_db.url}")
    private String databaseUrl;

    @Value("${bouncerate_db.user}")
    private String databaseUsername;

    @Value("${bouncerate_db.password}")
    private String databasePassword;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource result = new DriverManagerDataSource();
        result.setDriverClassName("com.mysql.cj.jdbc.Driver");
        result.setUrl(databaseUrl);
        result.setUsername(databaseUsername);
        result.setPassword(databasePassword);
        return result;
    }

    @Bean
    public Jdbi jdbi(DataSource dataSource, List<RowMapper<?>> rowMappers) {
        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        rowMappers.forEach(jdbi::registerRowMapper);
        return jdbi;
    }

    @Bean
    public AdvertisementProductDAO productDAO(Jdbi jdbi) {
        return jdbi.onDemand(AdvertisementProductDAO.class);
    }

    @Bean
    public BounceRateDAO bounceRateDAO(Jdbi jdbi) {
        return jdbi.onDemand(BounceRateDAO.class);
    }

    @Bean
    public SetTopBoxesDAO setTopBoxesDAO(Jdbi jdbi) {
        return jdbi.onDemand(SetTopBoxesDAO.class);
    }
}
