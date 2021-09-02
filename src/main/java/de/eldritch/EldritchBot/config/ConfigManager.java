package de.eldritch.EldritchBot.config;

import static de.eldritch.EldritchBot.EldritchBot.logger;

public class ConfigManager {
    ConfigWrapper wrapper;

    public ConfigManager() {
        wrapper = new ConfigWrapper();
        logger.info("ConfigManager is online.");
    }

    public String getString(String key) {
        return wrapper.get(key);
    }

    public String getString(String key, String defValue) {
        String ret = wrapper.get(key);
        if (ret == null)
            return defValue;
        else
            return ret;
    }

    public int getInt(String key, int defValue) {
        try {
            return Integer.parseInt(wrapper.get(key));
        } catch (NumberFormatException e) {
            logger.info("Unable to get property with key \"" + key + "\" as int.");
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
            return defValue;
        }
    }

    public boolean isTrue(String key) {
        return "true".equals(wrapper.get(key));
    }

    public boolean isFalse(String key) {
        return "false".equals(wrapper.get(key));
    }
}
