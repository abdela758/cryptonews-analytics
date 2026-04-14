package org.ulpgc.dacd.news.model;

import java.time.Instant;

public class NewsRecord {

    private final String title;
    private final String description;
    private final String source;
    private final String url;
    private final String publishedAt;
    private final Instant capturedAt;

    public NewsRecord(String title, String description, String source,
                      String url, String publishedAt, Instant capturedAt) {
        this.title = title;
        this.description = description;
        this.source = source;
        this.url = url;
        this.publishedAt = publishedAt;
        this.capturedAt = capturedAt;
    }

    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public String getSource()      { return source; }
    public String getUrl()         { return url; }
    public String getPublishedAt() { return publishedAt; }
    public Instant getCapturedAt() { return capturedAt; }

    @Override
    public String toString() {
        return "NewsRecord{" +
                "title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", capturedAt=" + capturedAt +
                '}';
    }
}