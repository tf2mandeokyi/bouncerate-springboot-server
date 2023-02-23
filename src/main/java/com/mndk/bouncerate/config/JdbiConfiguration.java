package com.mndk.bouncerate.config;

import com.mndk.bouncerate.db.BounceRateDAO;
import com.mndk.bouncerate.db.ProductCategoryDAO;
import com.mndk.bouncerate.db.ScheduleTableDAO;
import com.mndk.bouncerate.db.SetTopBoxesDAO;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@ComponentScan(basePackageClasses = ProductCategoryDAO.class)
@SuppressWarnings("unused")
public class JdbiConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public Jdbi jdbi(DataSource dataSource, List<RowMapper<?>> rowMappers) {
        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        rowMappers.forEach(jdbi::registerRowMapper);
        return jdbi;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
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
    public BounceRateDAO bounceRateDAO(
            Jdbi jdbi, ProductCategoryDAO productCategoryDAO, SetTopBoxesDAO setTopBoxesDAO
    ) {
        var result = jdbi.onDemand(BounceRateDAO.class);
        result.initializeTable();
        return result;
    }
}
