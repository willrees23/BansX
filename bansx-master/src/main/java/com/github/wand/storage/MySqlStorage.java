package com.github.wand.storage;

import com.github.wand.api.Storage;
import com.github.wand.config.Config;
import com.github.wand.enums.ActionType;
import com.github.wand.enums.PunishmentType;
import com.github.wand.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.*;
import java.util.*;

public class MySqlStorage implements Storage {
    private final Gson gson = new Gson();
    private Connection connection;

    @Override
    public void initialize(Config config) {
        try {
            String url = "jdbc:mysql://" + config.mysqlHost() + "/" + config.mysqlDatabase();
            connection = DriverManager.getConnection(url, config.mysqlUser(), config.mysqlPassword());
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize MySQL storage", e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            // Ignore
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS punishments (
                    id VARCHAR(36) PRIMARY KEY,
                    player_uuid VARCHAR(36),
                    ip VARCHAR(45),
                    type VARCHAR(10),
                    reason VARCHAR(255),
                    duration_ms BIGINT,
                    creation_timestamp BIGINT,
                    active BOOLEAN
                )
                """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS history (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    punishment_id VARCHAR(36),
                    action VARCHAR(20),
                    staff_uuid VARCHAR(36),
                    timestamp BIGINT,
                    details TEXT
                )
                """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tracks (
                    name VARCHAR(255) PRIMARY KEY
                )
                """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS steps (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    track_name VARCHAR(255),
                    step_number INT,
                    type VARCHAR(10),
                    duration_ms BIGINT,
                    ip BOOLEAN,
                    FOREIGN KEY (track_name) REFERENCES tracks(name) ON DELETE CASCADE
                )
                """);
        }
    }

    @Override
    public void savePunishment(Punishment punishment) {
        String sql = "INSERT INTO punishments (id, player_uuid, ip, type, reason, duration_ms, creation_timestamp, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE player_uuid=VALUES(player_uuid), ip=VALUES(ip), type=VALUES(type), reason=VALUES(reason), duration_ms=VALUES(duration_ms), creation_timestamp=VALUES(creation_timestamp), active=VALUES(active)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, punishment.id().toString());
            stmt.setString(2, punishment.playerUuid().toString());
            stmt.setString(3, punishment.ip());
            stmt.setString(4, punishment.type().name());
            stmt.setString(5, punishment.reason());
            stmt.setLong(6, punishment.durationMs());
            stmt.setLong(7, punishment.creationTimestamp());
            stmt.setBoolean(8, punishment.active());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save punishment", e);
        }
    }

    @Override
    public Punishment loadPunishment(UUID id) {
        String sql = "SELECT * FROM punishments WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Punishment(
                    UUID.fromString(rs.getString("id")),
                    UUID.fromString(rs.getString("player_uuid")),
                    rs.getString("ip"),
                    PunishmentType.valueOf(rs.getString("type")),
                    rs.getString("reason"),
                    rs.getLong("duration_ms"),
                    rs.getLong("creation_timestamp"),
                    rs.getBoolean("active")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load punishment", e);
        }
        return null;
    }

    @Override
    public List<Punishment> loadActivePunishments(UUID playerUuid) {
        String sql = "SELECT * FROM punishments WHERE player_uuid = ? AND active = true";
        return loadPunishments(sql, playerUuid.toString());
    }

    @Override
    public List<Punishment> loadAllPunishments(UUID playerUuid) {
        String sql = "SELECT * FROM punishments WHERE player_uuid = ?";
        return loadPunishments(sql, playerUuid.toString());
    }

    private List<Punishment> loadPunishments(String sql, String playerUuid) {
        List<Punishment> punishments = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerUuid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                punishments.add(new Punishment(
                    UUID.fromString(rs.getString("id")),
                    UUID.fromString(rs.getString("player_uuid")),
                    rs.getString("ip"),
                    PunishmentType.valueOf(rs.getString("type")),
                    rs.getString("reason"),
                    rs.getLong("duration_ms"),
                    rs.getLong("creation_timestamp"),
                    rs.getBoolean("active")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load punishments", e);
        }
        return punishments;
    }

    @Override
    public void saveHistoryEvent(HistoryEvent event) {
        String sql = "INSERT INTO history (punishment_id, action, staff_uuid, timestamp, details) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, event.punishmentId().toString());
            stmt.setString(2, event.action().name());
            stmt.setString(3, event.staffUuid() != null ? event.staffUuid().toString() : null);
            stmt.setLong(4, event.timestamp());
            stmt.setString(5, event.details());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save history event", e);
        }
    }

    @Override
    public List<HistoryEvent> loadHistory(UUID punishmentId) {
        String sql = "SELECT * FROM history WHERE punishment_id = ? ORDER BY timestamp";
        List<HistoryEvent> events = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, punishmentId.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(new HistoryEvent(
                    UUID.fromString(rs.getString("punishment_id")),
                    ActionType.valueOf(rs.getString("action")),
                    rs.getString("staff_uuid") != null ? UUID.fromString(rs.getString("staff_uuid")) : null,
                    rs.getLong("timestamp"),
                    rs.getString("details")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load history", e);
        }
        return events;
    }

    @Override
    public void saveTracks(List<EscalationTrack> tracks) {
        try {
            connection.setAutoCommit(false);
            // Clear existing
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DELETE FROM steps");
                stmt.execute("DELETE FROM tracks");
            }
            // Insert new
            String trackSql = "INSERT INTO tracks (name) VALUES (?)";
            String stepSql = "INSERT INTO steps (track_name, step_number, type, duration_ms, ip) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement trackStmt = connection.prepareStatement(trackSql);
                 PreparedStatement stepStmt = connection.prepareStatement(stepSql)) {
                for (EscalationTrack track : tracks) {
                    trackStmt.setString(1, track.name());
                    trackStmt.executeUpdate();
                    for (EscalationStep step : track.steps()) {
                        stepStmt.setString(1, track.name());
                        stepStmt.setInt(2, step.stepNumber());
                        stepStmt.setString(3, step.type().name());
                        stepStmt.setLong(4, step.durationMs());
                        stepStmt.setBoolean(5, step.ip());
                        stepStmt.executeUpdate();
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                // Ignore
            }
            throw new RuntimeException("Failed to save tracks", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    @Override
    public List<EscalationTrack> loadTracks() {
        Map<String, List<EscalationStep>> trackMap = new HashMap<>();
        String sql = "SELECT t.name, s.step_number, s.type, s.duration_ms, s.ip FROM tracks t LEFT JOIN steps s ON t.name = s.track_name ORDER BY t.name, s.step_number";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                int stepNumber = rs.getInt("step_number");
                if (stepNumber > 0) { // has steps
                    PunishmentType type = PunishmentType.valueOf(rs.getString("type"));
                    long durationMs = rs.getLong("duration_ms");
                    boolean ip = rs.getBoolean("ip");
                    trackMap.computeIfAbsent(name, k -> new ArrayList<>()).add(new EscalationStep(stepNumber, type, durationMs, ip));
                } else {
                    trackMap.putIfAbsent(name, new ArrayList<>());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load tracks", e);
        }
        return trackMap.entrySet().stream()
                .map(e -> new EscalationTrack(e.getKey(), e.getValue()))
                .toList();
    }
}
