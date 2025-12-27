package com.github.wand.command;

import com.github.wand.api.PunishmentManager;
import com.github.wand.config.MessagesConfig;
import com.github.wand.model.Punishment;
import com.github.wand.util.SpigotTextBuilder;
import com.github.wand.util.TimeUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class HistoryCommand implements CommandExecutor {
    private final PunishmentManager punishmentManager;
    private final MessagesConfig messagesConfig;
    private static final int PAGE_SIZE = 10;

    public HistoryCommand(PunishmentManager punishmentManager, MessagesConfig messagesConfig) {
        this.punishmentManager = punishmentManager;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bansx.history")) {
            sender.sendMessage(messagesConfig.getPermissionDenied());
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(messagesConfig.getUsageHistory());
            return true;
        }

        String playerName = args[0];
        int page = 1;
        if (args.length == 2) {
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                sender.sendMessage(messagesConfig.getInvalidPage());
                return true;
            }
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(messagesConfig.getPlayerNotFound());
            return true;
        }

        UUID playerUuid = offlinePlayer.getUniqueId();

        List<Punishment> allPunishments = new ArrayList<>(punishmentManager.getAllPunishments(playerUuid));
        if (allPunishments.isEmpty()) {
            sender.sendMessage(messagesConfig.getNoHistory(playerName));
            return true;
        }

        // Sort by creation time descending (most recent first)
        allPunishments.sort(Comparator.comparing(Punishment::creationTimestamp).reversed());

        int totalPunishments = allPunishments.size();
        int totalPages = (totalPunishments + PAGE_SIZE - 1) / PAGE_SIZE;

        if (page > totalPages) {
            sender.sendMessage(messagesConfig.getPageNotExist(page, totalPages));
            return true;
        }

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, totalPunishments);

        sendMessage(sender, messagesConfig.getHistoryListHeader(playerName, page, totalPages));
        for (int i = start; i < end; i++) {
            Punishment p = allPunishments.get(i);
            int historyCount = punishmentManager.getHistoryCount(p.id());
            long agoMs = System.currentTimeMillis() - p.creationTimestamp();
            String ago = TimeUtil.formatTimeAgo(agoMs);
            String line = messagesConfig.getHistoryListEntry(i + 1, p.type().toString(), p.reason(), historyCount, ago);
            if (sender instanceof Player player) {
                List<BaseComponent[]> componentsList = SpigotTextBuilder.create().text(line).hover("Click to see more for #" + (i+1)).command("/infraction " + p.id()).build();
                for (BaseComponent[] comps : componentsList) {
                    player.spigot().sendMessage(comps);
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
        }

        return true;
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
