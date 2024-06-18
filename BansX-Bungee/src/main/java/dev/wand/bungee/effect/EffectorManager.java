package dev.wand.bungee.effect;

import dev.wand.effect.PunishEffect;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@UtilityClass
public class EffectorManager {

    public void showEffect(ProxiedPlayer player, PunishEffect effect) {
        BungeeEffector effector = new BungeeEffector(player);
        effector.show(effect);
    }
}
