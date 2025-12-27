package com.github.wand.config;

import com.github.wand.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessagesConfig {
    private YamlConfiguration config;
    private final File file;

    public MessagesConfig(File dataFolder) {
        this.file = new File(dataFolder, "messages.yml");
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            // If not exists, create with defaults? But since in resources, it should be copied.
            // For now, assume it exists.
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    private String getMessage(String key) {
        String msg = config.getString(key, "&cMissing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public String getPermissionDenied() {
        return getMessage("permission-denied");
    }

    public String getUsagePunish() {
        return getMessage("usage-punish");
    }

    public String getUsageBansx() {
        return getMessage("usage-bansx");
    }

    public String getPlayerNotFound() {
        return getMessage("player-not-found");
    }

    public String getPunishmentApplied(String player, String reason, String type) {
        return getMessage("punishment-applied")
                .replace("%player%", player)
                .replace("%reason%", reason)
                .replace("%type%", type);
    }

    public String getConfigReloaded() {
        return getMessage("config-reloaded");
    }

    public String getReloadFailed(String error) {
        return getMessage("reload-failed").replace("%error%", error);
    }

    public String getUnknownSubcommand() {
        return getMessage("unknown-subcommand");
    }

    public String getBannedKick(String reason, long timeLeftMs) {
        String timeStr = TimeUtil.formatRemainingTime(timeLeftMs);
        return getMessage("banned-kick").replace("%reason%", reason).replace("%timeleft%", timeStr);
    }

    public String getMutedChat(String reason, long timeLeftMs) {
        String timeStr = TimeUtil.formatRemainingTime(timeLeftMs);
        return getMessage("muted-chat").replace("%reason%", reason).replace("%timeleft%", timeStr);
    }

    public String getInvalidReason(String reason) {
        return getMessage("invalid-reason").replace("%reason%", reason);
    }

    public String getWarningReceived(String reason) {
        return getMessage("warning-received").replace("%reason%", reason);
    }

    public String getUsageHistory() {
        return getMessage("usage-history");
    }

    public String getInvalidPage() {
        return getMessage("invalid-page");
    }

    public String getPageNotExist(int page, int total) {
        return getMessage("page-not-exist").replace("%page%", String.valueOf(page)).replace("%total%", String.valueOf(total));
    }

    public String getNoHistory(String player) {
        return getMessage("no-history").replace("%player%", player);
    }

    public String getUsageInfraction() {
        return getMessage("usage-infraction");
    }

    public String getInvalidId() {
        return getMessage("invalid-id");
    }

    public String getPunishmentNotFound() {
        return getMessage("punishment-not-found");
    }

    public String getPunishmentDetails() {
        return getMessage("punishment-details");
    }

    public String getHistoryHeader(int count) {
        return getMessage("history-header").replace("%count%", String.valueOf(count));
    }

    public String getHistoryEntry(int num, String action, String staff, String ago, String details) {
        return getMessage("history-entry")
                .replace("%num%", String.valueOf(num))
                .replace("%action%", action)
                .replace("%staff%", staff)
                .replace("%ago%", ago)
                .replace("%details%", details);
    }

    public String getUnexpectedError(String error) {
        return getMessage("unexpected-error").replace("%error%", error);
    }

    public String getHistoryListHeader(String player, int page, int total) {
        return getMessage("history-list-header")
                .replace("%player%", player)
                .replace("%page%", String.valueOf(page))
                .replace("%total%", String.valueOf(total));
    }

    public String getHistoryListEntry(int num, String type, String reason, int updates, String ago) {
        return getMessage("history-list-entry")
                .replace("%num%", String.valueOf(num))
                .replace("%type%", type)
                .replace("%reason%", reason)
                .replace("%updates%", String.valueOf(updates))
                .replace("%ago%", ago);
    }
}
