package ar.unrn.tp.config;

import ar.unrn.tp.modelo.Venta;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, List<Venta>> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<Venta>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configurar serializadores
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}


