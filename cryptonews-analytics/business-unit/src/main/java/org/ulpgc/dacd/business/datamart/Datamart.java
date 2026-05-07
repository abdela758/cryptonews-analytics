package org.ulpgc.dacd.business.datamart;

import com.google.gson.JsonObject;

import java.util.List;

public interface Datamart {
    void upsertCrypto(JsonObject event);
    void insertNews(JsonObject event);
    List<JsonObject> getLatestCryptos();
    List<JsonObject> getCryptoHistory(String coinId);
    List<JsonObject> getLatestNews();
    List<JsonObject> getNewsAndPriceAt(String coinId);
}
