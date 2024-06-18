package dev.wand.spigot.command;

import dev.wand.data.DataPlayer;
import dev.wand.data.command.CommandParser;
import dev.wand.data.command.ParsedCommand;
import dev.wand.effect.EffectManager;
import dev.wand.punish.Punishment;
import dev.wand.punish.enums.PunishmentType;
import dev.wand.spigot.BansX;
import dev.wand.spigot.util.TextUtil;
import dev.wand.util.TimeUtil;
import dev.wand.util.WrappedPosition;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Salers
 * made on dev.wand.spigot.command
 */
public class BanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            TextUtil.sendTranslated(sender, "&cUsage: /ban <player> <reason>");
            return false;
        }

        String playerName = args[0];

        String server = sender instanceof Player ? ((Player) sender).getDisplayName() : "CONSOLE";

        ParsedCommand parsedCommand = CommandParser.parsePunishCommand(args);

        Player player = Bukkit.getPlayer(playerName);
        DataPlayer dataPlayer;
        if (player != null) {
            dataPlayer = BansX.getDataManager().getPlayerData(player.getUniqueId());
        } else {
            dataPlayer = BansX.getDataManager().getPlayerData(playerName);
            if (dataPlayer == null) {
                TextUtil.sendTranslated(sender, "&cPlayer has never joined before.");
                return true;
            }

        }


        execute(sender, playerName, server, parsedCommand, dataPlayer);
        EffectManager.INSTANCE.applyToPlayer(new WrappedPosition(
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ(),
                player.getWorld().getName()

        ));
        return false;
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
