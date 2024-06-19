package dev.wand.effect;

import com.github.retrooper.packetevents.protocol.player.User;
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

    void show(WrappedPosition contextPosition, User user);

    String name();
}
