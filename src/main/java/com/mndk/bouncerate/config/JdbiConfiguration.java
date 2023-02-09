package com.mndk.bouncerate.config;

import com.mndk.bouncerate.db.*;
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
@ComponentScan(basePackageClasses = ProductCategoryDAO.class)
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
    public ProductCategoryDAO productCategoryDAO(Jdbi jdbi) {
        var result = jdbi.onDemand(ProductCategoryDAO.class);
        result.initializeTable();
        return result;
    }

    @Bean
    public SetTopBoxesDAO setTopBoxesDAO(Jdbi jdbi) {
        var result = jdbi.onDemand(SetTopBoxesDAO.class);
        result.initializeTable();
        return result;
    }

    @Bean
    public ScheduleTableDAO scheduleTableDAO(Jdbi jdbi) {
        var result = jdbi.onDemand(ScheduleTableDAO.class);
        result.initializeTable();
        return result;
    }

    @Bean
    public TemporaryBounceRateCalculationDAO temporaryBounceRateCalculationDAO(Jdbi jdbi) {
        return jdbi.onDemand(TemporaryBounceRateCalculationDAO.class);
    }

    @Bean
    public BounceRateDAO bounceRateDAO(
            Jdbi jdbi, ProductCategoryDAO productCategoryDAO, SetTopBoxesDAO setTopBoxesDAO
    ) {
        var result = jdbi.onDemand(BounceRateDAO.class);
        result.initializeTable();
        return result;
    }
}
