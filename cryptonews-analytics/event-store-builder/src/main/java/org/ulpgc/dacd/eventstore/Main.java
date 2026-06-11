package org.ulpgc.dacd.eventstore;

import org.ulpgc.dacd.eventstore.controller.EventStoreController;
import org.ulpgc.dacd.eventstore.store.FileEventStore;
import org.ulpgc.dacd.eventstore.subscriber.ActiveMqSubscriber;

public class Main {

    public static void main(String[] args) {
        Config config = new Config();

        EventStoreController controller = new EventStoreController(
                new ActiveMqSubscriber(
                        config.get("broker.url"),
                        config.getArray("broker.topics"),
                        config.get("broker.client.id"),
                        new FileEventStore(config.get("eventstore.path"))
                )
        );
        controller.start();
    }
}