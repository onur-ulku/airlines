package com.example.airlines.event;

import org.springframework.context.ApplicationEvent;

/**
 * Bir veya daha fazla transportation oluşturuldu, güncellendi veya silindi.
 * Routes cache'inin temizlenmesi için yayımlanır.
 */
public class TransportationsChangedEvent extends ApplicationEvent {

    public TransportationsChangedEvent(Object source) {
        super(source);
    }
}
