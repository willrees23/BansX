package dev.wand.punish;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Removal {

    private final String id;
    private final String punishmentId; // The ID of the punishment that was removed
    private final String user;

    private final String server;

    private final String executor;
    private final String reason;

    private final long datetime;
}
