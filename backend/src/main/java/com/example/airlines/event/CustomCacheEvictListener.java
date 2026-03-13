package com.example.airlines.event;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomCacheEvictListener {

    private final CacheManager cacheManager;

    public CustomCacheEvictListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @EventListener
    public void onTransportationsChanged(TransportationsChangedEvent event) {
        evictCache("routes");
        evictCache("transportations");
    }

    private void evictCache(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }
}
