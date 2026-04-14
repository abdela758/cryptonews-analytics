package org.ulpgc.dacd.crypto.store;

import org.ulpgc.dacd.crypto.model.CryptoRecord;

import java.sql.*;
import java.util.List;

public class SqliteCryptoStore implements DataStore {

    private final String dbPath;

    public SqliteCryptoStore(String dbPath) {
        this.dbPath = dbPath;
        createTable();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    private void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS crypto (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    coin_id TEXT NOT NULL,
                    symbol TEXT NOT NULL,
                    name TEXT NOT NULL,
                    price_usd REAL NOT NULL,
                    market_cap_usd REAL NOT NULL,
                    volume_24h REAL NOT NULL,
                    captured_at TEXT NOT NULL
                )
                """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create crypto table", e);
        }
    }

    @Override
    public void save(List<CryptoRecord> records) {
        String sql = """
                INSERT INTO crypto (coin_id, symbol, name, price_usd, market_cap_usd, volume_24h, captured_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (CryptoRecord record : records) {
                pstmt.setString(1, record.getId());
                pstmt.setString(2, record.getSymbol());
                pstmt.setString(3, record.getName());
                pstmt.setDouble(4, record.getPriceUsd());
                pstmt.setDouble(5, record.getMarketCapUsd());
                pstmt.setDouble(6, record.getVolume24h());
                pstmt.setString(7, record.getCapturedAt().toString());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save crypto records", e);
        }
    }
}