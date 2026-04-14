package org.ulpgc.dacd.news.serializer;

import org.ulpgc.dacd.news.model.NewsRecord;
import java.util.List;

public interface Serializer {
    List<NewsRecord> deserialize(String json);
}