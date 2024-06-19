package dev.wand.bungee.event;

import dev.wand.bungee.BansX;
import dev.wand.data.DataPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * PlayerListener
 * Listens to player events such as logins and disconnections to save player data and restrict access to the server.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        DataPlayer DP = BansX.getDataManager().getPlayerData(player.getUniqueId().toString());

        if (DP == null) {
            BansX.getDataManager().savePlayer(
                    player.getUniqueId().toString(),
                    player.getName(),
                    player.getSocketAddress().toString()
            );
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        // Save player data
        BansX.getDataManager().savePlayer(
                event.getPlayer().getUniqueId().toString(),
                event.getPlayer().getName(),
                event.getPlayer().getSocketAddress().toString()
        );
    }
}
