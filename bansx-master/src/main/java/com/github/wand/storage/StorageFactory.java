package com.github.wand.storage;

import com.github.wand.api.Storage;
import com.github.wand.enums.StorageType;

public class StorageFactory {
    public static Storage create(StorageType type) {
        return switch (type) {
            case FILE -> new FileStorage();
            case MYSQL -> new MySqlStorage();
            case SQLITE -> new SqliteStorage();
        };
    }
}
