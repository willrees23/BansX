package dev.wand.spigot;

import com.github.retrooper.packetevents.PacketEvents;
import dev.wand.DataConnector;
import dev.wand.DataManager;
import dev.wand.punish.PunishManager;
import dev.wand.spigot.command.BanCommand;
import dev.wand.spigot.listener.LoggingListener;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class BansX extends JavaPlugin {

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
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
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
        getServer().getPluginCommand("ban").setExecutor(new BanCommand());

        // Register listeners
        getServer().getPluginManager().registerEvents(new LoggingListener(), this);
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
