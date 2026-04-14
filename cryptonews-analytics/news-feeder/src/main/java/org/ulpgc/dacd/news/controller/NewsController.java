package org.ulpgc.dacd.news.controller;

import org.ulpgc.dacd.news.feeder.Feeder;
import org.ulpgc.dacd.news.model.NewsRecord;
import org.ulpgc.dacd.news.serializer.Serializer;
import org.ulpgc.dacd.news.store.DataStore;

import java.util.List;

public class NewsController {

    private final Feeder feeder;
    private final Serializer serializer;
    private final DataStore store;

    public NewsController(Feeder feeder, Serializer serializer, DataStore store) {
        this.feeder = feeder;
        this.serializer = serializer;
        this.store = store;
    }

    public void execute() {
        String json = feeder.fetch();
        List<NewsRecord> records = serializer.deserialize(json);
        store.save(records);
        System.out.println("Saved " + records.size() + " news records.");
        records.forEach(System.out::println);
    }
}