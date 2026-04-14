package org.ulpgc.dacd.news.store;

import org.ulpgc.dacd.news.model.NewsRecord;

import java.sql.*;
import java.util.List;

public class SqliteNewsStore implements DataStore {

    private final String dbPath;

    public SqliteNewsStore(String dbPath) {
        this.dbPath = dbPath;
        createTable();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    private void createTable() {
        String sql = """
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
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create news table", e);
        }
    }

    @Override
    public void save(List<NewsRecord> records) {
        String sql = """
                INSERT INTO news (title, description, source, url, published_at, captured_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (NewsRecord record : records) {
                pstmt.setString(1, record.getTitle());
                pstmt.setString(2, record.getDescription());
                pstmt.setString(3, record.getSource());
                pstmt.setString(4, record.getUrl());
                pstmt.setString(5, record.getPublishedAt());
                pstmt.setString(6, record.getCapturedAt().toString());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save news records", e);
        }
    }
}