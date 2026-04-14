package org.ulpgc.dacd.crypto.controller;

import org.ulpgc.dacd.crypto.feeder.Feeder;
import org.ulpgc.dacd.crypto.model.CryptoRecord;
import org.ulpgc.dacd.crypto.serializer.Serializer;
import org.ulpgc.dacd.crypto.store.DataStore;

import java.util.List;

public class CryptoController {

    private final Feeder feeder;
    private final Serializer serializer;
    private final DataStore store;

    public CryptoController(Feeder feeder, Serializer serializer, DataStore store) {
        this.feeder = feeder;
        this.serializer = serializer;
        this.store = store;
    }

    public void execute() {
        String json = feeder.fetch();
        List<CryptoRecord> records = serializer.deserialize(json);
        store.save(records);
        System.out.println("Saved " + records.size() + " crypto records.");
        records.forEach(System.out::println);
    }
}