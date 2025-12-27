package com.github.wand.config;

import com.github.wand.enums.StorageType;

public record Config(
    StorageType storageType,
    String basePath, // for FILE
    String mysqlHost,
    String mysqlDatabase,
    String mysqlUser,
    String mysqlPassword,
    String sqlitePath
) {}
