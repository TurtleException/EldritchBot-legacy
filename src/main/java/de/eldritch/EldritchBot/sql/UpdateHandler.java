package de.eldritch.EldritchBot.sql;

import de.eldritch.EldritchBot.sql.Connection;

public class UpdateHandler {
    public Connection connection;

    private int updateInterval;
    private Runnable onUpdate;

    public UpdateHandler(int updateIntervalSeconds) {
        updateInterval = updateIntervalSeconds;
    }

    public UpdateHandler(int updateIntervalSeconds, Runnable whenUpdated) {
        updateInterval = updateIntervalSeconds;
        onUpdate = whenUpdated;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }
}
