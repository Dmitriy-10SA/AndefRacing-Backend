package ru.andef.andefracing.backend;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Profile("!test")
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json())
                )
                .disableCachingNullValues();
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Справочные данные - кешируем на 3 часа
        cacheConfigurations.put(CacheNames.REGIONS, defaultConfig.entryTtl(Duration.ofHours(3)));
        cacheConfigurations.put(CacheNames.CITIES, defaultConfig.entryTtl(Duration.ofHours(3)));
        cacheConfigurations.put(CacheNames.GAMES, defaultConfig.entryTtl(Duration.ofHours(3)));

        // Информация о клубах - кешируем на 1 час
        cacheConfigurations.put(CacheNames.CLUB_FULL_INFO, defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put(CacheNames.CLUBS_IN_CITY, defaultConfig.entryTtl(Duration.ofHours(1)));

        // Профили - кешируем на 30 минут
        cacheConfigurations.put(CacheNames.CLIENT_PROFILE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put(CacheNames.CLIENT_FAVORITE_CLUBS, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put(CacheNames.EMPLOYEE_PROFILE, defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    public static class CacheNames {
        public static final String REGIONS = "regions";
        public static final String CITIES = "cities";
        public static final String GAMES = "games";

        public static final String CLUB_FULL_INFO = "clubFullInfo";
        public static final String CLUBS_IN_CITY = "clubsInCity";

        public static final String CLIENT_PROFILE = "clientProfile";
        public static final String CLIENT_FAVORITE_CLUBS = "clientFavoriteClubs";
        public static final String EMPLOYEE_PROFILE = "employeeProfile";
    }
}