package dev.wand.data;

import dev.wand.punish.Punishment;
import dev.wand.punish.enums.PunishmentType;
import lombok.Data;

@Data
public class DataPlayer {

    private final String uuid;
    private final String lastKnownName;
    private final String ip;
    private final long lastSeen;
}
