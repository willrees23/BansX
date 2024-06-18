package dev.wand.effect;

import dev.wand.effect.impl.MineplexGwenEffect;
import lombok.Getter;

@Getter
public enum PunishEffects {

    MINEPLEX_GWEN(new MineplexGwenEffect());

    final String name;
    final IPunishEffector effector;

    PunishEffects(IPunishEffector effector) {
        this.effector = effector;
        this.name = effector.name();
    }
}
