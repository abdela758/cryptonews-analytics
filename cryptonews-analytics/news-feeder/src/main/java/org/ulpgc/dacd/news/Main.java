package org.ulpgc.dacd.news;

import org.ulpgc.dacd.news.controller.NewsController;
import org.ulpgc.dacd.news.feeder.NewsApiFeeder;
import org.ulpgc.dacd.news.publisher.ActiveMqPublisher;
import org.ulpgc.dacd.news.serializer.NewsSerializer;
import org.ulpgc.dacd.news.store.SqliteNewsStore;

public class Main {

    public static void main(String[] args) {
        Config config = new Config();

        NewsController controller = new NewsController(
                new NewsApiFeeder(config.get("newsapi.url"), config.get("newsapi.key")),
                new NewsSerializer(),
                new SqliteNewsStore(config.get("database.path")),
                new ActiveMqPublisher(config.get("broker.url"), config.get("broker.topic")),
                config.getInt("capture.interval.minutes")
        );
        controller.start();
    }
}
