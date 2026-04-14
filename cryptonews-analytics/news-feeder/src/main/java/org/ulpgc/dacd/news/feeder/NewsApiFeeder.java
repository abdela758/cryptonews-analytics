package org.ulpgc.dacd.news.feeder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class NewsApiFeeder implements Feeder {

    private static final String API_KEY = "81e338badb1f4776b66bf69b245f0407";
    private static final String URL =
            "https://newsapi.org/v2/top-headlines?category=technology&language=en&pageSize=10&apiKey=" + API_KEY;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public String fetch() {
        Request request = new Request.Builder().url(URL).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
            throw new IOException("Error: " + response.code());
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch NewsAPI data", e);
        }
    }
}