package com.example.airlines.event;

import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RoutesCacheEvictListener {

    private final CacheManager cacheManager;

    public RoutesCacheEvictListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @EventListener
    public void onTransportationsChanged(TransportationsChangedEvent event) {
        var cache = cacheManager.getCache("routes");
        if (cache != null) {
            cache.clear();
        }
    }
}
