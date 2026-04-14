package org.ulpgc.dacd.crypto;

import org.ulpgc.dacd.crypto.controller.CryptoController;
import org.ulpgc.dacd.crypto.feeder.CoinGeckoFeeder;
import org.ulpgc.dacd.crypto.serializer.CryptoSerializer;
import org.ulpgc.dacd.crypto.store.SqliteCryptoStore;

public class Main {

    public static void main(String[] args) {
        CryptoController controller = new CryptoController(
                new CoinGeckoFeeder(),
                new CryptoSerializer(),
                new SqliteCryptoStore("crypto.db")
        );
        controller.execute();
    }
}
