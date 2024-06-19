package dev.wand.bungee.command;

import dev.wand.bungee.BansX;
import dev.wand.bungee.util.TextUtil;
import dev.wand.data.DataPlayer;
import dev.wand.data.command.CommandParser;
import dev.wand.data.command.ParsedCommand;
import dev.wand.effect.EffectManager;
import dev.wand.punish.Punishment;
import dev.wand.punish.enums.PunishmentType;
import dev.wand.util.TimeUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BanCommand extends Command {
    public BanCommand() {
        super("ban", "bansx.ban", "tempban", "tb", "tban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            TextUtil.sendTranslated(sender, "&cUsage: /ban <player> <reason>");
            return;
        }

        String playerName = args[0];

        String server = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getServer().getInfo().getName() : "CONSOLE";

        ParsedCommand parsedCommand = CommandParser.parsePunishCommand(args);

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
        DataPlayer dataPlayer;
        if (player != null) {
            dataPlayer = BansX.getDataManager().getPlayerData(player.getUniqueId());
        } else {
            dataPlayer = BansX.getDataManager().getPlayerData(playerName);
            if (dataPlayer == null) {
                TextUtil.sendTranslated(sender, "&cPlayer has never joined before.");
                return;
            }

        }

        execute(sender, playerName, server, parsedCommand, dataPlayer);
        EffectManager.INSTANCE.applyToPlayer(null);
    }

    private void execute(CommandSender sender, String playerName, String server, ParsedCommand parsedCommand, DataPlayer dataPlayer) {
        Punishment punishment = Punishment.create(
                dataPlayer.getUuid(),
                sender.getName(),
                server,
                parsedCommand.getReason(),
                System.currentTimeMillis(),
                parsedCommand.getTime(),
                PunishmentType.BAN
        );

        boolean success = BansX.getPunishManager().addPunishment(dataPlayer, punishment);

        if (!success) {
            TextUtil.sendTranslated(sender, "&cAn error occurred while trying to ban the player.");
            return;
        }
        TextUtil.sendHoverable(sender, "&cYou banned &e" + playerName + " &cfor &e" + TimeUtil.millisToTime(parsedCommand.getTime()) + " &cfor &e" + parsedCommand.getReason() + " &c" + (parsedCommand.getModifier() != null ? "with modifier &e" + parsedCommand.getModifier() : "") + "&c.", "&cThey won't be seen for a while.");
    }
}
