package dev.wand.bungee.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@UtilityClass
public class TextUtil {

    public void sendHoverable(CommandSender player, String message, String hoverText) {
        TextComponent component = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.translateAlternateColorCodes('&', hoverText))));
        player.sendMessage(component);
    }

    public TextComponent getTranslated(String message) {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void sendTranslated(CommandSender player, String message) {
        TextComponent component = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        player.sendMessage(component);
    }

    public TextComponent getBanMessage(String reason, long expiry) {
        TextComponent component = new TextComponent(ChatColor.RED + "You are permanently banned from this server.\n\n");
        component.addExtra(ChatColor.RED + "Reason: " + ChatColor.WHITE + reason + "\n");
        component.addExtra(ChatColor.RED + "Expires: " + ChatColor.WHITE + (expiry == -1 ? "Never" : expiry));
        return component;
    }
}
