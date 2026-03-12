package com.example.airlines.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutesCacheEvictListenerTest {

    @Mock
    CacheManager cacheManager;
    @Mock
    Cache cache;
    @InjectMocks
    RoutesCacheEvictListener listener;

    @Test
    void onTransportationsChanged_clearsRoutesCache() {
        when(cacheManager.getCache("routes")).thenReturn(cache);

        listener.onTransportationsChanged(new TransportationsChangedEvent(this));

        verify(cacheManager).getCache("routes");
        verify(cache).clear();
    }

    @Test
    void onTransportationsChanged_cacheNull_doesNotThrow() {
        when(cacheManager.getCache("routes")).thenReturn(null);

        listener.onTransportationsChanged(new TransportationsChangedEvent(this));

        verify(cacheManager).getCache("routes");
        verify(cache, never()).clear();
    }
}
