package de.eldritch.EldritchBot.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static de.eldritch.EldritchBot.EldritchBot.logger;

public class ConfigWrapper {
    private static final Properties properties = new Properties();

    public ConfigWrapper() {
        try {
            properties.load(new FileReader("config.properties"));
            logger.info("Successfully loaded config.properties.");
        } catch (IOException e) {
            logger.severe("Unable to load config.properties!");
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
        }
    }

    public String get(String key) {
        if (!key.equals("discord.log"))
            logger.finest("Requested property with key \"" + key + "\" from ConfigWrapper.  -> [" + properties.getProperty(key, "null") + "]");
        return properties.getProperty(key);
    }
}
