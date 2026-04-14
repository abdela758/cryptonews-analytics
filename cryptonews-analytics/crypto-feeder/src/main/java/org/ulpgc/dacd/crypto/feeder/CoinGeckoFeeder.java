package org.ulpgc.dacd.crypto.feeder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class CoinGeckoFeeder implements Feeder {

    private static final String URL =
            "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10&page=1";

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
            throw new RuntimeException("Failed to fetch CoinGecko data", e);
        }
    }
}