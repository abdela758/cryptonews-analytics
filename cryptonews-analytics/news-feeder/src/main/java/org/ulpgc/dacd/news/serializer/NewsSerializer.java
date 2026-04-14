package org.ulpgc.dacd.news.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.news.model.NewsRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NewsSerializer implements Serializer {

    @Override
    public List<NewsRecord> deserialize(String json) {
        List<NewsRecord> records = new ArrayList<>();
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        JsonArray articles = root.getAsJsonArray("articles");
        Instant capturedAt = Instant.now();

        for (JsonElement element : articles) {
            var obj = element.getAsJsonObject();
            records.add(new NewsRecord(
                    obj.get("title").getAsString(),
                    obj.has("description") && !obj.get("description").isJsonNull()
                            ? obj.get("description").getAsString() : "",
                    obj.getAsJsonObject("source").get("name").getAsString(),
                    obj.get("url").getAsString(),
                    obj.get("publishedAt").getAsString(),
                    capturedAt
            ));
        }
        return records;
    }
}