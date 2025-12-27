package com.github.wand.storage;

import com.github.wand.api.Storage;
import com.github.wand.config.Config;
import com.github.wand.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileStorage implements Storage {
    private final Gson gson = new Gson();
    private Path basePath;

    @Override
    public void initialize(Config config) {
        this.basePath = Paths.get(config.basePath());
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create storage directory", e);
        }
    }

    @Override
    public void close() {
        // No-op for file storage
    }

    @Override
    public void savePunishment(Punishment punishment) {
        Map<UUID, Punishment> punishments = loadAllPunishmentsMap();
        punishments.put(punishment.id(), punishment);
        saveMapToFile(punishments, "punishments.json");
    }

    @Override
    public Punishment loadPunishment(UUID id) {
        return loadAllPunishmentsMap().get(id);
    }

    @Override
    public List<Punishment> loadActivePunishments(UUID playerUuid) {
        return loadAllPunishmentsMap().values().stream()
                .filter(p -> p.playerUuid().equals(playerUuid) && p.active())
                .toList();
    }

    @Override
    public List<Punishment> loadAllPunishments(UUID playerUuid) {
        return loadAllPunishmentsMap().values().stream()
                .filter(p -> p.playerUuid().equals(playerUuid))
                .toList();
    }

    @Override
    public void saveHistoryEvent(HistoryEvent event) {
        Map<UUID, List<HistoryEvent>> history = loadHistoryMap();
        history.computeIfAbsent(event.punishmentId(), k -> new ArrayList<>()).add(event);
        saveMapToFile(history, "history.json");
    }

    @Override
    public List<HistoryEvent> loadHistory(UUID punishmentId) {
        return loadHistoryMap().getOrDefault(punishmentId, Collections.emptyList());
    }

    @Override
    public void saveTracks(List<EscalationTrack> tracks) {
        saveToFile(tracks, "tracks.json");
    }

    @Override
    public List<EscalationTrack> loadTracks() {
        return loadFromFile("tracks.json", new TypeToken<List<EscalationTrack>>(){}.getType());
    }

    private Map<UUID, Punishment> loadAllPunishmentsMap() {
        return loadFromFile("punishments.json", new TypeToken<Map<UUID, Punishment>>(){}.getType());
    }

    private Map<UUID, List<HistoryEvent>> loadHistoryMap() {
        return loadFromFile("history.json", new TypeToken<Map<UUID, List<HistoryEvent>>>(){}.getType());
    }

    private <T> T loadFromFile(String fileName, java.lang.reflect.Type type) {
        Path filePath = basePath.resolve(fileName);
        if (!Files.exists(filePath)) {
            return (T) (type == new TypeToken<List<EscalationTrack>>(){}.getType() ? new ArrayList<>() : new HashMap<>());
        }
        try (Reader reader = Files.newBufferedReader(filePath)) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + fileName, e);
        }
    }

    private <T> void saveToFile(T data, String fileName) {
        Path filePath = basePath.resolve(fileName);
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save " + fileName, e);
        }
    }

    private <K, V> void saveMapToFile(Map<K, V> map, String fileName) {
        saveToFile(map, fileName);
    }
}
