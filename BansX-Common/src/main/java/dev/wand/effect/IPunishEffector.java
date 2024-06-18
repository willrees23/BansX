package dev.wand.effect;

import dev.wand.util.WrappedPosition;

/**
 * Interface class which other classes implement.
 * Contains important methods such as showing and stopping effects.
 * <p>
 * Required because implementation differs between Spigot and Bungee.
 *
 * @see my beautiful face
 */
public interface IPunishEffector {

    void show(WrappedPosition contextPosition, String uuid);

    String name();
}
