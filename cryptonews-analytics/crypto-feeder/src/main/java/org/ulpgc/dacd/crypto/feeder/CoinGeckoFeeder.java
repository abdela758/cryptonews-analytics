package org.ulpgc.dacd.crypto.feeder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class CoinGeckoFeeder implements Feeder {

    private final String url;
    private final OkHttpClient client = new OkHttpClient();

    public CoinGeckoFeeder(String url) {
        this.url = url;
    }

    @Override
    public String fetch() {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            }
            throw new IOException("Error: " + response.code());
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch CoinGecko data", e);
        }
    }
}