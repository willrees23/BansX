package com.github.wand.api;

import com.github.wand.model.*;
import java.util.List;
import java.util.UUID;

public interface PunishmentManager {
    void initialize(Storage storage, List<EscalationTrack> tracks);

    Punishment applyPunishment(UUID playerUuid, String ip, String reason, UUID staffUuid);

    void removePunishment(UUID punishmentId, UUID staffUuid);

    void expirePunishments();

    List<Punishment> getActivePunishments(UUID playerUuid);

    List<HistoryEvent> getHistory(UUID punishmentId);

    void reloadConfig(List<EscalationTrack> newTracks);

    boolean isValidReason(String reason);

    List<String> getTrackNames();

    List<Punishment> getAllPunishments(UUID playerUuid);

    int getHistoryCount(UUID punishmentId);

    Punishment getPunishment(UUID id);
}
