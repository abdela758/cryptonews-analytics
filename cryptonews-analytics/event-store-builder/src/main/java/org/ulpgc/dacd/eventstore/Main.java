package org.ulpgc.dacd.eventstore;

import org.ulpgc.dacd.eventstore.controller.EventStoreController;
import org.ulpgc.dacd.eventstore.store.FileEventStore;
import org.ulpgc.dacd.eventstore.subscriber.ActiveMqSubscriber;

public class Main {

    public static void main(String[] args) {
        String brokerUrl = "tcp://localhost:61616";
        String[] topics = {"CryptoPrice", "TechNews"};
        String clientId = "event-store-builder";

        EventStoreController controller = new EventStoreController(
                new ActiveMqSubscriber(
                        brokerUrl,
                        topics,
                        clientId,
                        new FileEventStore("eventstore")
                )
        );
        controller.start();
    }
}