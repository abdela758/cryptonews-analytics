package org.ulpgc.dacd.crypto.store;

import org.ulpgc.dacd.crypto.model.CryptoRecord;
import java.util.List;

public interface DataStore {
    void save(List<CryptoRecord> records);
}