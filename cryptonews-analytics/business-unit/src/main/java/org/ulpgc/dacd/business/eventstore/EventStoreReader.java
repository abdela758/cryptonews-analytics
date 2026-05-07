package org.ulpgc.dacd.business.eventstore;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.business.datamart.Datamart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EventStoreReader {

    private final String basePath;
    private final Datamart datamart;

    public EventStoreReader(String basePath, Datamart datamart) {
        this.basePath = basePath;
        this.datamart = datamart;
    }

    public void loadAll() {
        File base = new File(basePath);
        if (!base.exists()) {
            System.out.println("No event store found at: " + basePath);
            return;
        }

        File[] topics = base.listFiles(File::isDirectory);
        if (topics == null) return;

        int count = 0;
        for (File topicDir : topics) {
            String topicName = topicDir.getName();
            File[] sources = topicDir.listFiles(File::isDirectory);
            if (sources == null) continue;

            for (File sourceDir : sources) {
                File[] eventFiles = sourceDir.listFiles((dir, name) -> name.endsWith(".events"));
                if (eventFiles == null) continue;

                for (File eventFile : eventFiles) {
                    count += loadFile(eventFile, topicName);
                }
            }
        }
        System.out.println("Loaded " + count + " historical events from event store.");
    }

    private int loadFile(File file, String topicName) {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                JsonObject event = JsonParser.parseString(line).getAsJsonObject();

                if (topicName.equals("CryptoPrice")) {
                    datamart.upsertCrypto(event);
                } else {
                    datamart.insertNews(event);
                }
                count++;
            }
        } catch (IOException e) {
            System.err.println("Failed to read file: " + file.getPath());
        }
        return count;
    }
}