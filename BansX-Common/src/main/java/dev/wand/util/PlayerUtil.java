package dev.wand.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * @author Salers
 * made on dev.wand.spigot.util
 */

@UtilityClass
public class PlayerUtil {

    public User getFromUUID(final String uuid) {
        Object channel = PacketEvents.getAPI().getProtocolManager().getChannel(UUID.fromString(uuid));
        return PacketEvents.getAPI().getProtocolManager().getUser(channel);
    }
}
