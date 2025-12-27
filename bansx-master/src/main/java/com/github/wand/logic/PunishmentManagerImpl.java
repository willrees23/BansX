package com.github.wand.logic;

import com.github.wand.api.*;
import com.github.wand.enums.ActionType;
import com.github.wand.model.*;
import com.github.wand.util.DurationUtil;
import java.util.*;

public class PunishmentManagerImpl implements PunishmentManager {
    private Storage storage;
    private List<EscalationTrack> tracks;
    private final Map<String, EscalationTrack> trackMap = new HashMap<>();

    @Override
    public void initialize(Storage storage, List<EscalationTrack> tracks) {
        this.storage = storage;
        this.tracks = tracks;
        updateTrackMap();
    }

    private void updateTrackMap() {
        trackMap.clear();
        for (EscalationTrack track : tracks) {
            trackMap.put(track.name().toLowerCase(), track);
        }
    }

    @Override
    public Punishment applyPunishment(UUID playerUuid, String ip, String reason, UUID staffUuid) {
        EscalationTrack track = trackMap.get(reason.toLowerCase());
        if (track == null) {
            throw new IllegalArgumentException("Unknown reason: " + reason);
        }

        String actualReason = track.name(); // Use the normalized track name

        // Count prior offenses on this track
        List<Punishment> allPunishments = storage.loadAllPunishments(playerUuid);
        long offenseCount = allPunishments.stream()
                .filter(p -> p.reason().equals(actualReason))
                .count();

        int stepNumber = (int) offenseCount + 1;
        EscalationStep step = track.steps().stream()
                .filter(s -> s.stepNumber() == stepNumber)
                .findFirst()
                .orElse(track.steps().getLast()); // stay at last step

        // Check for existing active punishment of same type
        List<Punishment> active = storage.loadActivePunishments(playerUuid);
        Optional<Punishment> existing = active.stream()
                .filter(p -> p.type() == step.type())
                .findFirst();
        if (existing.isPresent()) {
            // Remove existing
            Punishment old = existing.get();
            Punishment updated = new Punishment(old.id(), old.playerUuid(), old.ip(), old.type(), old.reason(), old.durationMs(), old.creationTimestamp(), false);
            storage.savePunishment(updated);
            storage.saveHistoryEvent(new HistoryEvent(old.id(), ActionType.ESCALATION, staffUuid, System.currentTimeMillis(), "Replaced by new punishment"));
        }

        // Create new punishment
        UUID id = UUID.randomUUID();
        long now = System.currentTimeMillis();
        String actualIp = step.ip() ? ip : null;
        Punishment newPunishment = new Punishment(id, playerUuid, actualIp, step.type(), actualReason, step.durationMs(), now, true);
        storage.savePunishment(newPunishment);
        storage.saveHistoryEvent(new HistoryEvent(id, ActionType.CREATION, staffUuid, now, null));

        return newPunishment;
    }

    @Override
    public void removePunishment(UUID punishmentId, UUID staffUuid) {
        Punishment punishment = storage.loadPunishment(punishmentId);
        if (punishment != null && punishment.active()) {
            Punishment updated = new Punishment(punishment.id(), punishment.playerUuid(), punishment.ip(), punishment.type(), punishment.reason(), punishment.durationMs(), punishment.creationTimestamp(), false);
            storage.savePunishment(updated);
            storage.saveHistoryEvent(new HistoryEvent(punishmentId, ActionType.MANUAL_REMOVAL, staffUuid, System.currentTimeMillis(), null));
        }
    }

    @Override
    public void expirePunishments() {
        // This should be called periodically or on player join
        // For simplicity, load all active, check expiration
        // But since no way to load all active, need to add method or iterate players, but since no player list, perhaps on interaction.
        // For now, assume called when needed, but to implement, perhaps add a method to check for a player.
        // But for global, it's hard without player list.
        // Perhaps in spigot, on player join, check for that player.
        // So, move expire to a method that takes playerUuid.
    }

    public void expirePunishmentsForPlayer(UUID playerUuid) {
        List<Punishment> active = storage.loadActivePunishments(playerUuid);
        long now = System.currentTimeMillis();
        for (Punishment p : active) {
            if (p.durationMs() > 0 && p.creationTimestamp() + p.durationMs() <= now) {
                Punishment updated = new Punishment(p.id(), p.playerUuid(), p.ip(), p.type(), p.reason(), p.durationMs(), p.creationTimestamp(), false);
                storage.savePunishment(updated);
                long expirationTime = p.creationTimestamp() + p.durationMs();
                storage.saveHistoryEvent(new HistoryEvent(p.id(), ActionType.EXPIRATION, null, expirationTime, null));
            }
        }
    }

    @Override
    public List<Punishment> getActivePunishments(UUID playerUuid) {
        expirePunishmentsForPlayer(playerUuid); // Ensure expired are updated
        return storage.loadActivePunishments(playerUuid);
    }

    @Override
    public List<HistoryEvent> getHistory(UUID punishmentId) {
        Punishment p = storage.loadPunishment(punishmentId);
        if (p != null) {
            expirePunishmentsForPlayer(p.playerUuid());
        }
        return storage.loadHistory(punishmentId);
    }

    @Override
    public void reloadConfig(List<EscalationTrack> newTracks) {
        this.tracks = newTracks;
        updateTrackMap();
        storage.saveTracks(newTracks);
    }

    public boolean isValidReason(String reason) {
        return trackMap.containsKey(reason.toLowerCase());
    }

    public List<String> getTrackNames() {
        return tracks.stream().map(EscalationTrack::name).toList();
    }

    public List<Punishment> getAllPunishments(UUID playerUuid) {
        expirePunishmentsForPlayer(playerUuid);
        return storage.loadAllPunishments(playerUuid);
    }

    public int getHistoryCount(UUID punishmentId) {
        return storage.loadHistory(punishmentId).size();
    }

    public Punishment getPunishment(UUID id) {
        return storage.loadPunishment(id);
    }
}
