package cn.migu.unify.ai.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Configuration
public class Configs {

//    @Autowired
//    private RedisTemplate r3;

    //redis-cli -n 8 keys "*" | xargs redis-cli -n 8 del
//    @Autowired(required = false)
//    public void setRedisTemplate(RedisTemplate redisTemplate) {
//        RedisSerializer stringSerializer = new StringRedisSerializer();
//        redisTemplate.setKeySerializer(stringSerializer);
//        redisTemplate.setValueSerializer(stringSerializer);
//        redisTemplate.setHashKeySerializer(stringSerializer);
//        redisTemplate.setHashValueSerializer(stringSerializer);
//        this.r3 = r3;
//    }

    @Bean(name = "d1")
    @Qualifier("d1")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.m1")
    public DataSource DataSource1() {
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "d2")
    @Qualifier("d2")
    @ConfigurationProperties(prefix="spring.datasource.m2")
    public DataSource DataSource2() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "m1")
    public JdbcTemplate m1(
            @Qualifier("d1") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    @Bean(name = "m2")
    public JdbcTemplate m2(
            @Qualifier("d2") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
