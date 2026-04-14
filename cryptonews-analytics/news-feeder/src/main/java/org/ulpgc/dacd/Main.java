package org.ulpgc.dacd.news;

import org.ulpgc.dacd.news.controller.NewsController;
import org.ulpgc.dacd.news.feeder.NewsApiFeeder;
import org.ulpgc.dacd.news.serializer.NewsSerializer;
import org.ulpgc.dacd.news.store.SqliteNewsStore;

public class Main {

    public static void main(String[] args) {
        NewsController controller = new NewsController(
                new NewsApiFeeder(),
                new NewsSerializer(),
                new SqliteNewsStore("news.db")
        );
        controller.execute();
    }
}
