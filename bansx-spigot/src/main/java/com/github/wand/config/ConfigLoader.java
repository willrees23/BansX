package com.github.wand.config;

import com.github.wand.enums.StorageType;
import com.github.wand.model.EscalationStep;
import com.github.wand.model.EscalationTrack;
import com.github.wand.enums.PunishmentType;
import com.github.wand.util.DurationUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigLoader {
    public static Config loadConfig(File configFile) {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(configFile);

        StorageType storageType = StorageType.valueOf(yml.getString("storage.type", "FILE"));
        String basePath = yml.getString("storage.base-path", "plugins/BansX");
        String mysqlHost = yml.getString("mysql.host", "localhost:3306");
        String mysqlDatabase = yml.getString("mysql.database", "bansx");
        String mysqlUser = yml.getString("mysql.user", "user");
        String mysqlPassword = yml.getString("mysql.password", "password");
        String sqlitePath = yml.getString("sqlite.path", "plugins/BansX/bansx.db");

        return new Config(storageType, basePath, mysqlHost, mysqlDatabase, mysqlUser, mysqlPassword, sqlitePath);
    }

    public static List<EscalationTrack> loadTracksFromBukkit(ConfigurationSection tracksSection) {
        if (tracksSection == null) return new ArrayList<>();

        List<EscalationTrack> tracks = new ArrayList<>();
        for (String trackName : tracksSection.getKeys(false)) {
            List<Map<?, ?>> stepsMaps = tracksSection.getMapList(trackName);
            List<EscalationStep> steps = new ArrayList<>();
            for (Map<?, ?> stepMap : stepsMaps) {
                int step = ((Number) stepMap.get("step")).intValue();
                PunishmentType type = PunishmentType.valueOf((String) stepMap.get("type"));
                String durationStr = (String) stepMap.get("duration");
                long durationMs = DurationUtil.parseDuration(durationStr);
                boolean ip = (Boolean) stepMap.get("ip");
                steps.add(new EscalationStep(step, type, durationMs, ip));
            }
            tracks.add(new EscalationTrack(trackName, steps));
        }
        return tracks;
    }
}
