package org.ulpgc.dacd.eventstore.store;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class FileEventStore implements EventStore {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    private final String basePath;

    public FileEventStore(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void save(String topic, String eventJson) {
        try {
            JsonObject event = JsonParser.parseString(eventJson).getAsJsonObject();
            String ss = event.get("ss").getAsString();
            String ts = event.get("ts").getAsString();
            String date = DATE_FORMAT.format(Instant.parse(ts));

            String dirPath = basePath + "/" + topic + "/" + ss;
            new File(dirPath).mkdirs();

            String filePath = dirPath + "/" + date + ".events";
            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
                writer.println(eventJson);
            }

            System.out.println("Stored event in: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store event", e);
        }
    }
}
