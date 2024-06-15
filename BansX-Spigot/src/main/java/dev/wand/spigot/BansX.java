package dev.wand.spigot;

import org.bukkit.plugin.java.JavaPlugin;

public class BansX extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("BansX-Spigot has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("BansX-Spigot has been disabled!");
    }
}
