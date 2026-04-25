package org.ulpgc.dacd.news.controller;

import org.ulpgc.dacd.news.feeder.Feeder;
import org.ulpgc.dacd.news.model.NewsRecord;
import org.ulpgc.dacd.news.serializer.Serializer;
import org.ulpgc.dacd.news.store.DataStore;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewsController {

    private final Feeder feeder;
    private final Serializer serializer;
    private final DataStore store;

    public NewsController(Feeder feeder, Serializer serializer, DataStore store) {
        this.feeder = feeder;
        this.serializer = serializer;
        this.store = store;
    }

    public void start() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::execute, 0, 30, TimeUnit.MINUTES);
        System.out.println("NewsFeeder started. Capturing every 30 minutes...");
    }

    private void execute() {
        try {
            String json = feeder.fetch();
            List<NewsRecord> records = serializer.deserialize(json);
            store.save(records);
            System.out.println("Saved " + records.size() + " news records.");
            records.forEach(System.out::println);
        } catch (Exception e) {
            System.err.println("Error during news capture: " + e.getMessage());
        }
    }
}