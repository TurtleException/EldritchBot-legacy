package de.eldritch.EldritchBot.sql;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class Connection {
    private java.sql.Connection connection;

    private final String sqlDatabase;
    private final String sqlUser;
    private final String sqlPass;
    private final String sqlIP;
    private final int sqlPort;

    private boolean isOnline = false;

    private final Logger logger;

    public Connection(String ip, int port, String database, String user, String pass, Logger loggerDest) {
        sqlDatabase = database;
        sqlUser = user;
        sqlPass = pass;
        sqlIP = ip;
        sqlPort = port;

        logger = loggerDest;

        buildConnection();
    }

    private void buildConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + sqlIP + ":" + sqlPort + "/" + sqlDatabase + "?autoReconnect=true", sqlUser, sqlPass);
            isOnline = true;

            try {
                logger.info("Successfully established SQL-Connection.");
            } catch (Exception ignored) {
                System.out.println("Successfully established SQL-Connection.");
            }
        } catch (SQLException e) {
            try {
                logger.warning("Unable to establish SQL-Connection!");
                logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
            } catch (Exception ignored) {
                System.out.println("Unable to establish SQL-Connection!");
                e.printStackTrace();
            }
            isOnline = false;
        }
    }

    private void checkOnline() {
        try {
            isOnline = connection.isValid(0);
        } catch (SQLException e) {
            isOnline = false;
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
        }

        if (!isOnline) {
            logger.warning("SQL-Connection is not online! Attempting to reconnect...");
            buildConnection();
        }
    }

    public ResultSet execute(String sql) throws SQLException {
        checkOnline();
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return statement.executeQuery(sql);
    }

    public void executeSilent(String sql) throws SQLException {
        checkOnline();
        Statement statement = connection.createStatement();
        statement.execute(sql);
    }

    public boolean isOnline() {
        checkOnline();
        return isOnline;
    }

    public java.sql.Connection getSqlConnection() {
        return connection;
    }
}
