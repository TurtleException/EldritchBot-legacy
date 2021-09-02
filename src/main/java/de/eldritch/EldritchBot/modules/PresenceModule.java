package de.eldritch.EldritchBot.modules;

import de.eldritch.EldritchBot.discord.DiscordConnector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import static de.eldritch.EldritchBot.EldritchBot.configManager;

public class PresenceModule {
    private final JDA jda;

    public PresenceModule(DiscordConnector discordConnector) {
        jda = discordConnector.getJDA();
    }

    public void loadPresence() {
        switch (configManager.getString("discord.presence.OnlineStatus", "ONLINE")) {
            case "IDLE":
                jda.getPresence().setStatus(OnlineStatus.IDLE);
                break;
            case "DO_NOT_DISTURB":
                jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);
                break;
            case "INVISIBLE":
                jda.getPresence().setStatus(OnlineStatus.INVISIBLE);
                break;
            default:
                jda.getPresence().setStatus(OnlineStatus.ONLINE);
                break;
        }
        switch (configManager.getString("discord.presence.Activity", "watching")) {
            case "watching":
                jda.getPresence().setActivity(Activity.watching(configManager.getString("discord.presence.args", "eldritch.de")));
                break;
            case "playing":
                jda.getPresence().setActivity(Activity.playing(configManager.getString("discord.presence.args", "eldritch.de")));
                break;
            case "streaming":
                jda.getPresence().setActivity(Activity.streaming(configManager.getString("discord.presence.args", "eldritch.de"), configManager.getString("discord.presence.url", "https://eldritch.de")));
                break;
            case "listening":
                jda.getPresence().setActivity(Activity.listening(configManager.getString("discord.presence.args", "eldritch.de")));
                break;
            default:
                jda.getPresence().setActivity(Activity.watching("eldritch.de"));
                break;
        }
    }
}
