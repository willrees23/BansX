package dev.wand.bungee.effect;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import dev.wand.effect.IPunishEffector;
import dev.wand.effect.PunishEffect;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeEffector implements IPunishEffector {

    private ProxiedPlayer player;

    public BungeeEffector(ProxiedPlayer player) {
        this.player = player;
    }

    @Override
    public void show(PunishEffect effect) {
        switch (effect) {
            case MINEPLEX_GWEN -> {
                WrapperPlayServerSpawnEntity entity = new WrapperPlayServerSpawnEntity(1, null, EntityTypes.GUARDIAN, new Location(0, 100, 0, 0, 0), 50, 0, null);
                User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
                user.sendPacket(entity);
            }
        }
    }
}
