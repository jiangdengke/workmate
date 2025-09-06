package org.jdk.workmate.config;

import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** jOOQ 基础配置 */
@Configuration
public class JooqConfig {
  @Bean
  public DSLContext dslContext(
      DataSource dataSource, @Value("${spring.jooq.sql-dialect:MYSQL}") String dialect) {
    Settings settings = new Settings().withRenderSchema(false);
    return DSL.using(dataSource, SQLDialect.valueOf(dialect), settings);
  }
}
