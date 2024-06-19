package dev.wand.effect;

import dev.wand.util.PlayerUtil;
import dev.wand.util.WrappedPosition;
import lombok.Getter;

/**
 * @author Salers
 * made on dev.wand.effect
 */

@Getter
public enum EffectManager {

    INSTANCE;
    private IPunishEffector activeEffect;

    public void setup() {
        // TODO, set with the config
        this.activeEffect = PunishEffects.MINEPLEX_GWEN.getEffector();
    }

    public void applyToPlayer(WrappedPosition wrappedPosition, String uuid) {
        this.activeEffect.show(wrappedPosition, PlayerUtil.getFromUUID(uuid));
    }
}
