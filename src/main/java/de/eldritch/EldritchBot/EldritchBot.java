package de.eldritch.EldritchBot;

import de.eldritch.EldritchBot.config.ConfigManager;
import de.eldritch.EldritchBot.discord.DiscordConnector;
import de.eldritch.EldritchBot.modules.PresenceModule;
import de.eldritch.EldritchBot.modules.TicketModule;
import de.eldritch.EldritchBot.modules.VoiceChannelModule;
import de.eldritch.EldritchBot.sql.Connection;
import de.eldritch.EldritchBot.util.CustomFormatter;
import de.eldritch.EldritchBot.util.IdGenerator;
import de.eldritch.EldritchBot.util.handlers.CustomFileHandler;
import net.dv8tion.jda.api.OnlineStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EldritchBot {
    public static final Logger logger;

    private static CustomFileHandler logFile;

    private static final String logFileName;

    public static final String artifact;
    public static final String version;

    // version.properties
    static {
        Properties versionProperties = new Properties();
        try {
            InputStream in = EldritchBot.class.getClassLoader().getResourceAsStream("version.properties");
            versionProperties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        artifact    = versionProperties.getProperty("project.artifactId", EldritchBot.class.getSimpleName());
        version     = versionProperties.getProperty("project.version", "undefined");
    }

    static {
        File logPath = new File("logs");
        //noinspection ResultOfMethodCallIgnored
        logPath.mkdir();

        LocalDate date = LocalDate.now();

        String logFilePre = date + "-log";
        logFileName = logFilePre + IdGenerator.getLogFileId(logPath, logFilePre) + "-general.log";
        logger = Logger.getLogger(String.valueOf(date));
        logger.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new CustomFormatter());
        logger.addHandler(consoleHandler);

        try {
            // - - - HANDLERS
            logFile = new CustomFileHandler("logs/" + logFileName);
            logFile.setFormatter(new CustomFormatter());

            // logger.addHandler(new StreamHandler(System.out, new CustomFormatter()));     --> using ConsoleHandler instead
            // logger.addHandler(new DiscordHandler());                                     --> moved to DiscordConnector()
            logger.addHandler(logFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.setLevel(Level.ALL);
        logger.info("Starting up " + artifact + " version " + version + "...");
    }

    public static final ConfigManager configManager   = new ConfigManager();  // --> mind custom configuration

    public static final DiscordConnector discordConnector = new DiscordConnector(
            configManager.getString("discord.token"));
    public static final Connection sqlConnector = new Connection(
            configManager.getString("sql.ip", "sql.eldritch.de"),
            configManager.getInt("sql.port", 3306),
            configManager.getString("sql.database", ""),
            configManager.getString("sql.user", ""),
            configManager.getString("sql.pass", ""),
            logger);

    // MODULES
    public static PresenceModule        presenceModule;
    public static TicketModule          ticketModule;
    // public static TimerModule           timerModule;
    public static VoiceChannelModule    voiceChannelModule;

    public static void main(String[] args) {
        // - - - LOG-LEVEL
        try {
            logger.setLevel(Level.parse(configManager.getString("logger.level", "ALL")));
            logger.info("Set logLevel to " + logger.getLevel().getName() + ".");
        } catch (IllegalArgumentException e) {
            logger.setLevel(Level.ALL);
            logger.warning("logLevel is now set to ALL due to a problem with the config.");
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
        }
        // if the log-level specified in config.properties is an illegal argument
        // the logger will print EVERYTHING that is being logged for debugging.


        if (!discordConnector.isOnline()) {
            logger.severe("JDA is not online! Canceling the process...");
            return;
        }


        // - - - MODULES
        presenceModule = new PresenceModule(discordConnector);
        ticketModule = new TicketModule(sqlConnector);
        // timerModule = new TimerModule();
        voiceChannelModule = new VoiceChannelModule();

        presenceModule.loadPresence();
    }

    public static void exit(int exitCode) {
        logger.info("Bot is shutting down with exit code " + exitCode);

        try {
            sqlConnector.getSqlConnection().close();
        } catch (SQLException e) {
            logger.warning("Unable to properly close SQL-Connection!");
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
        }

        discordConnector.getJDA().getPresence().setPresence(OnlineStatus.IDLE, null);

        logFile.close();
        discordConnector.getUpdatedLogChannel().sendFile(new File(System.getProperty("user.dir") + File.separator + "logs" + File.separator + logFileName)).queue();

        discordConnector.getJDA().shutdown();

        System.exit(exitCode);
    }
}
