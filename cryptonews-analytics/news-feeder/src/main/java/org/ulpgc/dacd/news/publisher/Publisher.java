package org.ulpgc.dacd.news.publisher;

public interface Publisher {
    void publish(String json);
    void close();
}
