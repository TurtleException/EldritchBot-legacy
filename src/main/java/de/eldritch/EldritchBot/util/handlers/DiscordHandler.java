package de.eldritch.EldritchBot.util.handlers;

import de.eldritch.EldritchBot.util.CustomFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static de.eldritch.EldritchBot.EldritchBot.configManager;
import static de.eldritch.EldritchBot.EldritchBot.discordConnector;

public class DiscordHandler extends Handler {
    public DiscordHandler() {
        this.setFormatter(new CustomFormatter());
    }

    @Override
    public void publish(LogRecord record) {
        if (discordConnector == null || !discordConnector.isOnline()) return;

        if (checkLevel(record.getLevel())) return;

        if (this.getFormatter().format(record).length() + 7 <= 2000) {
            discordConnector.log("```\n" + this.getFormatter().format(record) + "```");
            return;
        }


        int prefixLength = (this.getFormatter().format(record).length() - record.getMessage().length());
        if (prefixLength >= 2000) return; // unable to properly format record
        int msgLength = 2000 - prefixLength - 7 - 7;

        List<String> messages = new ArrayList<>();
        int index = 0;
        while (index < record.getMessage().length()) {
            messages.add(record.getMessage().substring(index, Math.min(index + msgLength, record.getMessage().length())));
            index += msgLength;
        }

        record.setMessage(messages.get(0));
        discordConnector.log("```\n" + this.getFormatter().format(record) + "... ```");

        for (int i = 1; i < messages.size(); i++) {
            if (messages.size() == i + 1) {
                discordConnector.log("```\n... " + messages.get(i) + "```");
            } else {
                discordConnector.log("```\n... " + messages.get(i) + "...```");
            }
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    private boolean checkLevel(Level level) {
        try {
            return (level.intValue() >= Level.parse(configManager.getString("discord.log")).intValue());
        } catch (IllegalArgumentException e) {
            return true;
        }
    }
}
