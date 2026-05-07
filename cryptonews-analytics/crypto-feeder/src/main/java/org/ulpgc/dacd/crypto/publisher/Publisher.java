package org.ulpgc.dacd.crypto.publisher;

public interface Publisher {
    void publish(String json);
    void close();
}