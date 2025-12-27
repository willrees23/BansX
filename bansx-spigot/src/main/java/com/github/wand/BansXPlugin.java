package com.github.wand;

import com.github.wand.api.PunishmentManager;
import com.github.wand.api.Storage;
import com.github.wand.command.BansXCommand;
import com.github.wand.command.PunishCommand;
import com.github.wand.command.HistoryCommand;
import com.github.wand.command.InfractionCommand;
import com.github.wand.config.Config;
import com.github.wand.config.ConfigLoader;
import com.github.wand.config.MessagesConfig;
import com.github.wand.listener.PunishmentListener;
import com.github.wand.logic.PunishmentManagerImpl;
import com.github.wand.model.EscalationTrack;
import com.github.wand.storage.StorageFactory;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class BansXPlugin extends JavaPlugin {

    private PunishmentManager punishmentManager;
    private MessagesConfig messagesConfig;

    @Override
    public void onLoad() {
        // setup API
        saveDefaultConfig();
        saveResource("messages.yml", false);
    }

    @Override
    public void onEnable() {
        Config config = ConfigLoader.loadConfig(getConfigFile());
        List<EscalationTrack> tracks = ConfigLoader.loadTracksFromBukkit(getConfig().getConfigurationSection("tracks"));

        Storage storage = StorageFactory.create(config.storageType());
        storage.initialize(config);

        punishmentManager = new PunishmentManagerImpl();
        punishmentManager.initialize(storage, tracks);

        messagesConfig = new MessagesConfig(getDataFolder());

        List<String> trackNames = tracks.stream().map(EscalationTrack::name).toList();

        // Register command
        PluginCommand punishCmd = getCommand("punish");
        if (punishCmd != null) {
            PunishCommand punishCommand = new PunishCommand(punishmentManager, messagesConfig, trackNames);
            punishCmd.setExecutor(punishCommand);
            punishCmd.setTabCompleter(punishCommand);
        }

        PluginCommand bansxCmd = getCommand("bansx");
        if (bansxCmd != null) {
            bansxCmd.setExecutor(new BansXCommand(this, punishmentManager, messagesConfig));
        }

        PluginCommand historyCmd = getCommand("history");
        if (historyCmd != null) {
            historyCmd.setExecutor(new HistoryCommand(punishmentManager, messagesConfig));
        }

        PluginCommand infractionCmd = getCommand("infraction");
        if (infractionCmd != null) {
            infractionCmd.setExecutor(new InfractionCommand(punishmentManager, messagesConfig));
        }

        // Register events
        getServer().getPluginManager().registerEvents(new PunishmentListener(punishmentManager, messagesConfig), this);
    }

    @Override
    public void onDisable() {
        if (punishmentManager != null) {
            // close storage if needed
        }
    }

    private File getConfigFile() {
        return new File(getDataFolder(), "config.yml");
    }
}
