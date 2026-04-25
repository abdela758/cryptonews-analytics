package org.ulpgc.dacd.crypto.controller;

import org.ulpgc.dacd.crypto.feeder.Feeder;
import org.ulpgc.dacd.crypto.model.CryptoRecord;
import org.ulpgc.dacd.crypto.serializer.Serializer;
import org.ulpgc.dacd.crypto.store.DataStore;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CryptoController {

    private final Feeder feeder;
    private final Serializer serializer;
    private final DataStore store;

    public CryptoController(Feeder feeder, Serializer serializer, DataStore store) {
        this.feeder = feeder;
        this.serializer = serializer;
        this.store = store;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::execute, 0, 30, TimeUnit.MINUTES);
        System.out.println("CryptoFeeder started. Capturing every 30 minutes...");
    }

    private void execute() {
        try {
            String json = feeder.fetch();
            List<CryptoRecord> records = serializer.deserialize(json);
            store.save(records);
            System.out.println("Saved " + records.size() + " crypto records.");
            records.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error during crypto capture: " + e.getMessage());
        }
    }
}