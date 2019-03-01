package br.dcc.ufba.themoviefinder;

import java.util.Arrays;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class AppConfig 
{
    @Bean
    public CacheManager cacheManager() 
    {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
          new ConcurrentMapCache("lodCache"),
          new ConcurrentMapCache("lodCacheSaveLater"),
          new ConcurrentMapCache("lodCacheRelation"),
          new ConcurrentMapCache("lodCacheRelationSaveLater")));
        return cacheManager;
    }
}
