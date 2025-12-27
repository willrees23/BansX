package com.github.wand.model;

import com.github.wand.enums.ActionType;
import java.util.UUID;

public record HistoryEvent(
    UUID punishmentId,
    ActionType action,
    UUID staffUuid, // nullable
    long timestamp,
    String details // optional
) {}
