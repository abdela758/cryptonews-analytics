package org.ulpgc.dacd.crypto.model;

import java.time.Instant;

public class CryptoRecord {

    private final String id;          // Ej: "bitcoin"
    private final String symbol;      // Ej: "btc"
    private final String name;        // Ej: "Bitcoin"
    private final double priceUsd;    // Precio actual en USD
    private final double marketCapUsd;
    private final double volume24h;   // Volumen últimas 24h
    private final Instant capturedAt; // Timestamp de la captura (UTC)

    public CryptoRecord(String id, String symbol, String name,
                        double priceUsd, double marketCapUsd,
                        double volume24h, Instant capturedAt) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.priceUsd = priceUsd;
        this.marketCapUsd = marketCapUsd;
        this.volume24h = volume24h;
        this.capturedAt = capturedAt;
    }

    public String getId()           { return id; }
    public String getSymbol()       { return symbol; }
    public String getName()         { return name; }
    public double getPriceUsd()     { return priceUsd; }
    public double getMarketCapUsd() { return marketCapUsd; }
    public double getVolume24h()    { return volume24h; }
    public Instant getCapturedAt()  { return capturedAt; }

    @Override
    public String toString() {
        return "CryptoRecord{" +
                "id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", priceUsd=" + priceUsd +
                ", capturedAt=" + capturedAt +
                '}';
    }
}
