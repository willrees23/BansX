package com.github.wand.command;

import com.github.wand.BansXPlugin;
import com.github.wand.api.PunishmentManager;
import com.github.wand.config.ConfigLoader;
import com.github.wand.config.MessagesConfig;
import com.github.wand.model.EscalationTrack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BansXCommand implements CommandExecutor {
    private final BansXPlugin plugin;
    private final PunishmentManager punishmentManager;
    private final MessagesConfig messagesConfig;

    public BansXCommand(BansXPlugin plugin, PunishmentManager punishmentManager, MessagesConfig messagesConfig) {
        this.plugin = plugin;
        this.punishmentManager = punishmentManager;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bansx.admin")) {
            sender.sendMessage(messagesConfig.getPermissionDenied());
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(messagesConfig.getUsageBansx());
            return true;
        }

        if ("reload".equalsIgnoreCase(args[0])) {
            try {
                plugin.reloadConfig();
                messagesConfig.reload();
                List<EscalationTrack> newTracks = ConfigLoader.loadTracksFromBukkit(plugin.getConfig().getConfigurationSection("tracks"));
                punishmentManager.reloadConfig(newTracks);
                sender.sendMessage(messagesConfig.getConfigReloaded());
            } catch (Exception e) {
                sender.sendMessage(messagesConfig.getReloadFailed(e.getMessage()));
            }
            return true;
        }

        sender.sendMessage(messagesConfig.getUnknownSubcommand());
        return true;
    }
}
