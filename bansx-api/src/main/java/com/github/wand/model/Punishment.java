package com.github.wand.model;

import com.github.wand.enums.PunishmentType;
import java.util.UUID;

public record Punishment(
    UUID id,
    UUID playerUuid,
    String ip, // nullable
    PunishmentType type,
    String reason,
    long durationMs, // 0 for permanent
    long creationTimestamp,
    boolean active
) {}
