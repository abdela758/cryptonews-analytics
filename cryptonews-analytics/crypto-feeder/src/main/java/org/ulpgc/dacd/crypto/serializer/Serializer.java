package org.ulpgc.dacd.crypto.serializer;

import org.ulpgc.dacd.crypto.model.CryptoRecord;
import java.util.List;

public interface Serializer {
    List<CryptoRecord> deserialize(String json);
}