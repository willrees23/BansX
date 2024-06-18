package dev.wand.bungee;

import com.github.retrooper.packetevents.PacketEvents;
import dev.wand.DataConnector;
import dev.wand.DataManager;
import dev.wand.bungee.command.BanCommand;
import dev.wand.bungee.event.PlayerListener;
import dev.wand.punish.PunishManager;
import io.github.retrooper.packetevents.bungee.factory.BungeePacketEventsBuilder;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.SQLException;

public class BansX extends Plugin {

    @Getter
    private static BansX instance;

    @Getter
    private static DataConnector dataConnector;
    @Getter
    private static DataManager dataManager;
    @Getter
    private static PunishManager punishManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(BungeePacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("BansX-Bungee has been enabled!");

        // Setup the Database
        dataConnector = new DataConnector();
        try {
            dataConnector.connect(getDataFolder().getAbsolutePath());
        } catch (SQLException e) {
            getLogger().severe("Could not connect to database.");
            getLogger().severe("Database error message: " + e.getMessage());
        }

        if (dataConnector.isConnected()) {
            dataManager = new DataManager(dataConnector);
            dataManager.createTables();
            punishManager = new PunishManager(dataConnector);
        }

        // Initialise packet events
        PacketEvents.getAPI().init();

        // Register commands
        getProxy().getPluginManager().registerCommand(this, new BanCommand());

        // Register listeners
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
    }

    @Override
    public void onDisable() {
        getLogger().info("BansX-Bungee has been disabled!");

        // DC from database
        dataConnector.disconnect();

        // stop packet events
        PacketEvents.getAPI().terminate();
    }
}
