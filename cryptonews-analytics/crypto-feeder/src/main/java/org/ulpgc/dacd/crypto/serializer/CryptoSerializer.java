package org.ulpgc.dacd.crypto.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.ulpgc.dacd.crypto.model.CryptoRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CryptoSerializer implements Serializer {

    @Override
    public List<CryptoRecord> deserialize(String json) {
        List<CryptoRecord> records = new ArrayList<>();
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        Instant capturedAt = Instant.now();

        for (JsonElement element : array) {
            var obj = element.getAsJsonObject();
            records.add(new CryptoRecord(
                    obj.get("id").getAsString(),
                    obj.get("symbol").getAsString(),
                    obj.get("name").getAsString(),
                    obj.get("current_price").getAsDouble(),
                    obj.get("market_cap").getAsDouble(),
                    obj.get("total_volume").getAsDouble(),
                    capturedAt
            ));
        }
        return records;
    }
}