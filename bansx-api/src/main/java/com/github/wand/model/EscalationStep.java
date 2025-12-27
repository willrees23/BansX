package com.github.wand.model;

import com.github.wand.enums.PunishmentType;

public record EscalationStep(
    int stepNumber,
    PunishmentType type,
    long durationMs,
    boolean ip
) {}
