package com.github.wand.model;

import java.util.List;

public record EscalationTrack(
    String name, // reason
    List<EscalationStep> steps
) {}
