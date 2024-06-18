package dev.wand.effect.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import dev.wand.effect.IPunishEffector;
import dev.wand.util.WrappedPosition;

/**
 * @author Salers
 * made on dev.wand.effect.impl
 */
public class MineplexGwenEffect implements IPunishEffector {

    @Override
    public void show(WrappedPosition contextPosition, String uuid) {
        WrapperPlayServerSpawnEntity entity = new WrapperPlayServerSpawnEntity(1, null, EntityTypes.GUARDIAN,
                new Location(contextPosition.getX(),contextPosition.getY(), contextPosition.getZ(), 0f, 0f), 50, 0, null);
        User user = PacketEvents.getAPI().getPlayerManager().getUser(uuid);
        user.sendPacket(entity);
    }


    @Override
    public String name() {
        return "MineplexGwen";
    }
}
