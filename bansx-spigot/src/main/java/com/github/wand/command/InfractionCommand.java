package com.github.wand.command;

import com.github.wand.api.PunishmentManager;
import com.github.wand.config.MessagesConfig;
import com.github.wand.model.HistoryEvent;
import com.github.wand.model.Punishment;
import com.github.wand.util.SpigotTextBuilder;
import com.github.wand.util.TimeUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class InfractionCommand implements CommandExecutor {
    private final PunishmentManager punishmentManager;
    private final MessagesConfig messagesConfig;

    public InfractionCommand(PunishmentManager punishmentManager, MessagesConfig messagesConfig) {
        this.punishmentManager = punishmentManager;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bansx.history")) {
            sender.sendMessage(messagesConfig.getPermissionDenied());
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(messagesConfig.getUsageInfraction());
            return true;
        }

        UUID id;
        try {
            id = UUID.fromString(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(messagesConfig.getInvalidId());
            return true;
        }

        Punishment p = punishmentManager.getPunishment(id);
        if (p == null) {
            sender.sendMessage(messagesConfig.getPunishmentNotFound());
            return true;
        }

        // Display punishment details
        sendMessage(sender, messagesConfig.getPunishmentDetails());
        if (sender instanceof Player player) {
            SpigotTextBuilder builder = SpigotTextBuilder.create()
                .text("&7ID: &f" + p.id()).copy(p.id().toString()).hover("&eCopy punishment UUID").newline()
                .text("&7Player: &f" + getPlayerName(p.playerUuid())).copy(p.playerUuid().toString()).hover("&eCopy player UUID").newline()
                .text("&7Type: &f" + p.type()).newline()
                .text("&7Reason: &f" + p.reason()).newline()
                .text("&7Created: &f" + TimeUtil.formatTimeAgo(System.currentTimeMillis() - p.creationTimestamp())).newline()
                .text("&7Duration: &f" + (p.durationMs() == 0 ? "Permanent" : TimeUtil.formatDuration(p.durationMs()))).newline()
                .text("&7Active: &f" + (p.active() ? "Yes" : "No"));
            if (p.ip() != null) {
                builder.newline().text("&7IP: &f" + p.ip());
            }
            List<BaseComponent[]> details = builder.build();
            for (BaseComponent[] line : details) {
                player.spigot().sendMessage(line);
            }
        } else {
            sendMessage(sender, "&7ID: &f" + p.id());
            sendMessage(sender, "&7Player: &f" + getPlayerName(p.playerUuid()));
            sendMessage(sender, "&7Type: &f" + p.type());
            sendMessage(sender, "&7Reason: &f" + p.reason());
            sendMessage(sender, "&7Created: &f" + TimeUtil.formatTimeAgo(System.currentTimeMillis() - p.creationTimestamp()));
            sendMessage(sender, "&7Duration: &f" + (p.durationMs() == 0 ? "Permanent" : TimeUtil.formatDuration(p.durationMs())));
            sendMessage(sender, "&7Active: &f" + (p.active() ? "Yes" : "No"));
            if (p.ip() != null) {
                sendMessage(sender, "&7IP: &f" + p.ip());
            }
        }

        // Display history
        List<HistoryEvent> history = punishmentManager.getHistory(id);
        history.sort(Comparator.comparing(HistoryEvent::timestamp).reversed());

        sendMessage(sender, messagesConfig.getHistoryHeader(history.size()));
        for (int i = 0; i < history.size(); i++) {
            HistoryEvent e = history.get(i);
            String staff = e.staffUuid() != null ? getPlayerName(e.staffUuid()) : "Console";
            String ago = TimeUtil.formatTimeAgo(System.currentTimeMillis() - e.timestamp());
            String details = e.details() != null ? " &7- &f" + e.details() : "";
            sendMessage(sender, messagesConfig.getHistoryEntry(i + 1, e.action().toString(), staff, ago, details));
        }

        return true;
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    private String getPlayerName(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }
}
