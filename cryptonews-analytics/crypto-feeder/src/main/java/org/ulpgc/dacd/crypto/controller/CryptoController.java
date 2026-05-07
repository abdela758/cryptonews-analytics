package org.ulpgc.dacd.crypto.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.ulpgc.dacd.crypto.feeder.Feeder;
import org.ulpgc.dacd.crypto.model.CryptoRecord;
import org.ulpgc.dacd.crypto.publisher.Publisher;
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
    private final Publisher publisher;
    private final Gson gson = new Gson();

    public CryptoController(Feeder feeder, Serializer serializer, DataStore store, Publisher publisher) {
        this.feeder = feeder;
        this.serializer = serializer;
        this.store = store;
        this.publisher = publisher;
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

            for (CryptoRecord record : records) {
                JsonObject event = new JsonObject();
                event.addProperty("ts", record.getCapturedAt().toString());
                event.addProperty("ss", "crypto-feeder");
                event.addProperty("id", record.getId());
                event.addProperty("symbol", record.getSymbol());
                event.addProperty("name", record.getName());
                event.addProperty("priceUsd", record.getPriceUsd());
                event.addProperty("marketCapUsd", record.getMarketCapUsd());
                event.addProperty("volume24h", record.getVolume24h());
                publisher.publish(event.toString());
            }

            System.out.println("Published " + records.size() + " crypto events to ActiveMQ.");
        } catch (Exception e) {
            System.err.println("Error during crypto capture: " + e.getMessage());
        }
    }
}