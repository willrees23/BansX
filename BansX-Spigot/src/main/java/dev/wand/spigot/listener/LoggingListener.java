package dev.wand.spigot.listener;

import dev.wand.data.DataPlayer;
import dev.wand.spigot.BansX;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author Salers
 * made on dev.wand.spigot.listener
 */
public class LoggingListener implements Listener {

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DataPlayer DP = BansX.getDataManager().getPlayerData(player.getUniqueId().toString());

        if (DP == null) {
            BansX.getDataManager().savePlayer(
                    player.getUniqueId().toString(),
                    player.getName(),
                    player.getAddress().toString()
            );
        }
    }
}
