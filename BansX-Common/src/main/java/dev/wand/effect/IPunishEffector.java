package dev.wand.effect;

/**
 * Interface class which other classes implement.
 * Contains important methods such as showing and stopping effects.
 * <p>
 * Required because implementation differs between Spigot and Bungee.
 *
 * @see PunishEffect
 */
public interface IPunishEffector {

    void show(PunishEffect effect);
}
