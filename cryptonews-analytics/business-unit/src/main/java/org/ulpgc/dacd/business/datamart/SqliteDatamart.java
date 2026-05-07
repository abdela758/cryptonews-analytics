package org.ulpgc.dacd.business.datamart;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteDatamart implements Datamart {

    private final String dbPath;

    public SqliteDatamart(String dbPath) {
        this.dbPath = dbPath;
        createTables();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    private void createTables() {
        String cryptoTable = """
                CREATE TABLE IF NOT EXISTS crypto_latest (
                    coin_id TEXT PRIMARY KEY,
                    symbol TEXT NOT NULL,
                    name TEXT NOT NULL,
                    price_usd REAL NOT NULL,
                    market_cap_usd REAL NOT NULL,
                    volume_24h REAL NOT NULL,
                    last_updated TEXT NOT NULL
                )
                """;
        String cryptoHistoryTable = """
                CREATE TABLE IF NOT EXISTS crypto_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    coin_id TEXT NOT NULL,
                    price_usd REAL NOT NULL,
                    captured_at TEXT NOT NULL
                )
                """;
        String newsTable = """
                CREATE TABLE IF NOT EXISTS news (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    source TEXT NOT NULL,
                    url TEXT NOT NULL,
                    published_at TEXT NOT NULL,
                    captured_at TEXT NOT NULL
                )
                """;
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(cryptoTable);
            stmt.execute(cryptoHistoryTable);
            stmt.execute(newsTable);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create datamart tables", e);
        }
    }

    @Override
    public void upsertCrypto(JsonObject event) {
        String upsert = """
                INSERT INTO crypto_latest (coin_id, symbol, name, price_usd, market_cap_usd, volume_24h, last_updated)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(coin_id) DO UPDATE SET
                    price_usd = excluded.price_usd,
                    market_cap_usd = excluded.market_cap_usd,
                    volume_24h = excluded.volume_24h,
                    last_updated = excluded.last_updated
                """;
        String history = "INSERT INTO crypto_history (coin_id, price_usd, captured_at) VALUES (?, ?, ?)";

        try (Connection conn = connect()) {
            try (PreparedStatement pstmt = conn.prepareStatement(upsert)) {
                pstmt.setString(1, event.get("id").getAsString());
                pstmt.setString(2, event.get("symbol").getAsString());
                pstmt.setString(3, event.get("name").getAsString());
                pstmt.setDouble(4, event.get("priceUsd").getAsDouble());
                pstmt.setDouble(5, event.get("marketCapUsd").getAsDouble());
                pstmt.setDouble(6, event.get("volume24h").getAsDouble());
                pstmt.setString(7, event.get("ts").getAsString());
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(history)) {
                pstmt.setString(1, event.get("id").getAsString());
                pstmt.setDouble(2, event.get("priceUsd").getAsDouble());
                pstmt.setString(3, event.get("ts").getAsString());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Failed to upsert crypto: " + e.getMessage());
        }
    }

    @Override
    public void insertNews(JsonObject event) {
        String sql = "INSERT INTO news (title, description, source, url, published_at, captured_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, event.get("title").getAsString());
            pstmt.setString(2, event.has("description") ? event.get("description").getAsString() : "");
            pstmt.setString(3, event.get("source").getAsString());
            pstmt.setString(4, event.get("url").getAsString());
            pstmt.setString(5, event.get("publishedAt").getAsString());
            pstmt.setString(6, event.get("ts").getAsString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Failed to insert news: " + e.getMessage());
        }
    }

    @Override
    public List<JsonObject> getLatestCryptos() {
        String sql = "SELECT * FROM crypto_latest ORDER BY market_cap_usd DESC";
        return query(sql);
    }

    @Override
    public List<JsonObject> getCryptoHistory(String coinId) {
        String sql = "SELECT * FROM crypto_history WHERE coin_id = ? ORDER BY captured_at DESC LIMIT 100";
        List<JsonObject> results = new ArrayList<>();
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, coinId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("coin_id", rs.getString("coin_id"));
                obj.addProperty("price_usd", rs.getDouble("price_usd"));
                obj.addProperty("captured_at", rs.getString("captured_at"));
                results.add(obj);
            }
        } catch (SQLException e) {
            System.err.println("Failed to get crypto history: " + e.getMessage());
        }
        return results;
    }

    @Override
    public List<JsonObject> getLatestNews() {
        String sql = "SELECT * FROM news ORDER BY captured_at DESC LIMIT 20";
        return query(sql);
    }

    @Override
    public List<JsonObject> getNewsAndPriceAt(String coinId) {
        String sql = """
                SELECT n.title, n.source, n.published_at, c.price_usd, c.captured_at
                FROM news n
                LEFT JOIN crypto_history c ON substr(n.captured_at, 1, 16) = substr(c.captured_at, 1, 16)
                    AND c.coin_id = ?
                ORDER BY n.captured_at DESC
                LIMIT 20
                """;
        List<JsonObject> results = new ArrayList<>();
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, coinId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("title", rs.getString("title"));
                obj.addProperty("source", rs.getString("source"));
                obj.addProperty("published_at", rs.getString("published_at"));
                obj.addProperty("price_usd", rs.getDouble("price_usd"));
                obj.addProperty("captured_at", rs.getString("captured_at"));
                results.add(obj);
            }
        } catch (SQLException e) {
            System.err.println("Failed to get news and price: " + e.getMessage());
        }
        return results;
    }

    private List<JsonObject> query(String sql) {
        List<JsonObject> results = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                for (int i = 1; i <= cols; i++) {
                    obj.addProperty(meta.getColumnName(i), rs.getString(i));
                }
                results.add(obj);
            }
        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }
        return results;
    }
}
