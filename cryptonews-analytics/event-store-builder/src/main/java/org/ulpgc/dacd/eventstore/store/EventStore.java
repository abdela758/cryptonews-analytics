package org.ulpgc.dacd.eventstore.store;

public interface EventStore {
    void save(String topic, String eventJson);
}