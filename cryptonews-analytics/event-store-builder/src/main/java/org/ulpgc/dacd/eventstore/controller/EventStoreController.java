package org.ulpgc.dacd.eventstore.controller;

import org.ulpgc.dacd.eventstore.subscriber.Subscriber;

public class EventStoreController {

    private final Subscriber subscriber;

    public EventStoreController(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void start() {
        subscriber.start();
        System.out.println("Event Store Builder is running...");
    }
}
