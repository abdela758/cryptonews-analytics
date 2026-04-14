package org.ulpgc.dacd.news.store;

import org.ulpgc.dacd.news.model.NewsRecord;
import java.util.List;

public interface DataStore {
    void save(List<NewsRecord> records);
}
