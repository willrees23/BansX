package com.github.wand.listener;

import com.github.wand.api.PunishmentManager;
import com.github.wand.config.MessagesConfig;
import com.github.wand.logic.PunishmentManagerImpl;
import com.github.wand.model.Punishment;
import com.github.wand.enums.PunishmentType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;
import java.util.UUID;

public record PunishmentListener(PunishmentManager punishmentManager, MessagesConfig messagesConfig) implements Listener {

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerUuid = event.getUniqueId();

        // Check active punishments
        List<Punishment> active = punishmentManager.getActivePunishments(playerUuid);
        for (Punishment p : active) {
            if (p.type() == PunishmentType.BAN) {
                long remainingMs = p.creationTimestamp() + p.durationMs() - System.currentTimeMillis();
                if (remainingMs < 0) remainingMs = 0;
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, messagesConfig.getBannedKick(p.reason(), remainingMs));
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        List<Punishment> active = punishmentManager.getActivePunishments(playerUuid);
        for (Punishment p : active) {
            if (p.type() == PunishmentType.MUTE) {
                event.setCancelled(true);
                long remainingMs = p.creationTimestamp() + p.durationMs() - System.currentTimeMillis();
                if (remainingMs < 0) remainingMs = 0;
                event.getPlayer().sendMessage(messagesConfig.getMutedChat(p.reason(), remainingMs));
                break;
            }
        }
    }
}
