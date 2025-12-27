package com.github.wand.command;

import com.github.wand.api.PunishmentManager;
import com.github.wand.config.MessagesConfig;
import com.github.wand.enums.PunishmentType;
import com.github.wand.model.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishCommand implements CommandExecutor, TabCompleter {
    private final PunishmentManager punishmentManager;
    private final MessagesConfig messagesConfig;
    private final List<String> trackNames;

    public PunishCommand(PunishmentManager punishmentManager, MessagesConfig messagesConfig, List<String> trackNames) {
        this.punishmentManager = punishmentManager;
        this.messagesConfig = messagesConfig;
        this.trackNames = trackNames;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bansx.punish")) {
            sender.sendMessage(messagesConfig.getPermissionDenied());
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(messagesConfig.getUsagePunish());
            return true;
        }

        String playerName = args[0];
        String reason = args[1];

        if (playerName.trim().isEmpty()) {
            sender.sendMessage(messagesConfig.getPlayerNotFound());
            return true;
        }

        if (reason.trim().isEmpty() || !punishmentManager.isValidReason(reason)) {
            sender.sendMessage(messagesConfig.getInvalidReason(reason));
            return true;
        }

        UUID playerUuid;
        String ip = null;

        Player onlinePlayer = Bukkit.getPlayer(playerName);
        if (onlinePlayer != null) {
            playerUuid = onlinePlayer.getUniqueId();
            ip = onlinePlayer.getAddress().getAddress().getHostAddress();
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (offlinePlayer.hasPlayedBefore()) {
                playerUuid = offlinePlayer.getUniqueId();
                // ip remains null for offline
            } else {
                sender.sendMessage(messagesConfig.getPlayerNotFound());
                return true;
            }
        }

        UUID staffUuid = (sender instanceof Player) ? ((Player) sender).getUniqueId() : null;

        try {
            Punishment punishment = punishmentManager.applyPunishment(playerUuid, ip, reason, staffUuid);
            sender.sendMessage(messagesConfig.getPunishmentApplied(playerName, reason, punishment.type().toString()));
            // Notify online player if warned
            if (punishment.type() == PunishmentType.WARN && onlinePlayer != null) {
                onlinePlayer.sendMessage(messagesConfig.getWarningReceived(punishment.reason()));
            }
            // Ban enforcement now handled on login attempt
        } catch (Exception e) {
            sender.sendMessage(messagesConfig.getUnexpectedError(e.getMessage()));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            // Completing player names
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        } else if (args.length == 2) {
            // Completing reasons or track names
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("bansx.track")) {
                for (String trackName : trackNames) {
                    if (trackName.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(trackName);
                    }
                }
            }
            return completions;
        }
        return null;
    }
}
