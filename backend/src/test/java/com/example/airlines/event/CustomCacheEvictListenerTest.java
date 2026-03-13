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
class CustomCacheEvictListenerTest {

    @Mock
    CacheManager cacheManager;
    @Mock
    Cache cache;
    @InjectMocks
    CustomCacheEvictListener listener;

    @Test
    void onTransportationsChanged_clearsRoutesAndTransportationsCache() {
        when(cacheManager.getCache("routes")).thenReturn(cache);
        when(cacheManager.getCache("transportations")).thenReturn(cache);

        listener.onTransportationsChanged(new TransportationsChangedEvent(this));

        verify(cacheManager).getCache("routes");
        verify(cacheManager).getCache("transportations");
        verify(cache, times(2)).clear();
    }

    @Test
    void onTransportationsChanged_cacheNull_doesNotThrow() {
        when(cacheManager.getCache("routes")).thenReturn(null);
        when(cacheManager.getCache("transportations")).thenReturn(null);

        listener.onTransportationsChanged(new TransportationsChangedEvent(this));

        verify(cacheManager).getCache("routes");
        verify(cacheManager).getCache("transportations");
        verify(cache, never()).clear();
    }
}
