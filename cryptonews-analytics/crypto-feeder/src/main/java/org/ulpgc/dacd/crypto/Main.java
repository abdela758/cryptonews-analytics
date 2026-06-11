package org.ulpgc.dacd.crypto;

import org.ulpgc.dacd.crypto.controller.CryptoController;
import org.ulpgc.dacd.crypto.feeder.CoinGeckoFeeder;
import org.ulpgc.dacd.crypto.publisher.ActiveMqPublisher;
import org.ulpgc.dacd.crypto.serializer.CryptoSerializer;
import org.ulpgc.dacd.crypto.store.SqliteCryptoStore;

public class Main {

    public static void main(String[] args) {
        Config config = new Config();

        CryptoController controller = new CryptoController(
                new CoinGeckoFeeder(config.get("coingecko.url")),
                new CryptoSerializer(),
                new SqliteCryptoStore(config.get("database.path")),
                new ActiveMqPublisher(config.get("broker.url"), config.get("broker.topic")),
                config.getInt("capture.interval.minutes")
        );
        controller.start();
    }
}