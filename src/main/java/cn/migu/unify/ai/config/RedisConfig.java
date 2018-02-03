package cn.migu.unify.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.r3.host}") String r3Host;

    @Value("${spring.redis.r3.database}") int r3BbIndex;

    @Value("${spring.redis.r3.port}") int r3Port;

    @Value("${spring.redis.r3.password}") String r3Password;

    @Value("${spring.redis.r3.pool.max-active}") int r3MaxActive;

    @Value("${spring.redis.r3.pool.max-idle}") int r3MaxIdle;

    @Value("${spring.redis.r3.pool.max-wait}") int r3MaxWait;

    @Value("${spring.redis.r3.pool.min-idle}") int r3MinIdle;


    @Bean
    public RedisConnectionFactory jedisConnectionFactory3() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(r3MaxActive);
        poolConfig.setMaxIdle(r3MaxIdle);
        poolConfig.setMinIdle(r3MinIdle);
        poolConfig.setMaxWaitMillis(r3MaxWait);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnCreate(true);
        poolConfig.setTestWhileIdle(true);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(poolConfig);
        jedisConnectionFactory.setHostName(r3Host);
        jedisConnectionFactory.setDatabase(r3BbIndex);
        jedisConnectionFactory.setPassword(r3Password);
        jedisConnectionFactory.setPort(r3Port);

        //其他配置，可再次扩展

        return jedisConnectionFactory;
    }

    @Bean(name = "r3")
    public RedisTemplate redisTemplate() {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory3());
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer);
        return redisTemplate;
    }
}
