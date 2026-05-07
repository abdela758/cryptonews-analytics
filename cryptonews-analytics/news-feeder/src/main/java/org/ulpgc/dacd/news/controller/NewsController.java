package org.ulpgc.dacd.news.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.ulpgc.dacd.news.feeder.Feeder;
import org.ulpgc.dacd.news.model.NewsRecord;
import org.ulpgc.dacd.news.publisher.Publisher;
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
    private final Publisher publisher;
    private final Gson gson = new Gson();

    public NewsController(Feeder feeder, Serializer serializer, DataStore store, Publisher publisher) {
        this.feeder = feeder;
        this.serializer = serializer;
        this.store = store;
        this.publisher = publisher;
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

            for (NewsRecord record : records) {
                JsonObject event = new JsonObject();
                event.addProperty("ts", record.getCapturedAt().toString());
                event.addProperty("ss", "news-feeder");
                event.addProperty("title", record.getTitle());
                event.addProperty("description", record.getDescription());
                event.addProperty("source", record.getSource());
                event.addProperty("url", record.getUrl());
                event.addProperty("publishedAt", record.getPublishedAt());
                publisher.publish(event.toString());
            }

            System.out.println("Published " + records.size() + " news events to ActiveMQ.");
        } catch (Exception e) {
            System.err.println("Error during news capture: " + e.getMessage());
        }
    }
}