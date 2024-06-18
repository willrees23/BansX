package dev.wand.data;

import lombok.Data;

@Data
public class DataPlayer {

    private final String uuid;
    private final String lastKnownName;
    private final String ip;
    private final long lastSeen;
}
