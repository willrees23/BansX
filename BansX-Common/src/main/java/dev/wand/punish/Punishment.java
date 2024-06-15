package dev.wand.punish;

import dev.wand.punish.enums.PunishmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class Punishment {

    private String id;

    private final String user;
    private final String executor;

    private final String server;

    private final String reason;

    private final long datetime;
    private final long expiry;
    private final long duration;

    private final PunishmentType type;

    @Setter
    private Removal removal;

    public Punishment(String id, String user, String executor, String server, String reason, long datetime, long expiry, long duration, PunishmentType type) {
        this.id = id;
        this.user = user;
        this.executor = executor;
        this.server = server;
        this.reason = reason;
        this.datetime = datetime;
        this.expiry = expiry;
        this.duration = duration;
        this.type = type;
    }

    public Punishment(String user, String executor, String server, String reason, long datetime, long expiry, long duration, PunishmentType type) {
        this.user = user;
        this.executor = executor;
        this.server = server;
        this.reason = reason;
        this.datetime = datetime;
        this.expiry = expiry;
        this.duration = duration;
        this.type = type;
    }

    public static Punishment create(String user, String executor, String server, String reason, long datetime, long duration, PunishmentType type) {
        return new Punishment(user, executor, server, reason, datetime, datetime + duration, duration, type);
    }

    public boolean isSaved() {
        return id != null;
    }

    public boolean isActive() {
        // if removal is not null, then the punishment is not active
        // if removal is null, and the punishment is permanent, then the punishment is active
        // if removal is null, and the punishment is not permanent, then check if the punishment has expired
        if (removal != null) {
            return false;
        }
        if (isPermanent()) {
            return true;
        }
        return !isExpired();
    }

    public boolean isPermanent() {
        return duration == -1;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() < expiry;
    }
}
