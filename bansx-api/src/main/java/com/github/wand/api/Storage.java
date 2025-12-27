package com.github.wand.api;

import com.github.wand.config.Config;
import com.github.wand.model.*;
import java.util.List;
import java.util.UUID;

public interface Storage {
    void initialize(Config config);

    void close();

    void savePunishment(Punishment punishment);

    Punishment loadPunishment(UUID id);

    List<Punishment> loadActivePunishments(UUID playerUuid);

    List<Punishment> loadAllPunishments(UUID playerUuid);

    void saveHistoryEvent(HistoryEvent event);

    List<HistoryEvent> loadHistory(UUID punishmentId);

    void saveTracks(List<EscalationTrack> tracks);

    List<EscalationTrack> loadTracks();
}
