package org.ulpgc.dacd.business.controller;

import org.ulpgc.dacd.business.api.RestApi;
import org.ulpgc.dacd.business.eventstore.EventStoreReader;
import org.ulpgc.dacd.business.subscriber.Subscriber;

public class BusinessController {

    private final EventStoreReader eventStoreReader;
    private final Subscriber subscriber;
    private final RestApi restApi;

    public BusinessController(EventStoreReader eventStoreReader, Subscriber subscriber, RestApi restApi) {
        this.eventStoreReader = eventStoreReader;
        this.subscriber = subscriber;
        this.restApi = restApi;
    }

    public void start() {
        System.out.println("Loading historical events...");
        eventStoreReader.loadAll();

        System.out.println("Starting real-time subscription...");
        subscriber.start();

        System.out.println("Starting REST API...");
        restApi.start();
    }
}
