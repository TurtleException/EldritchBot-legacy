package de.eldritch.EldritchBot.discord;

import de.eldritch.EldritchBot.discord.commands.CommandPing;
import de.eldritch.EldritchBot.discord.commands.CommandSay;
import de.eldritch.EldritchBot.discord.commands.CommandStop;
import de.eldritch.EldritchBot.discord.commands.CommandWednesday;
import de.eldritch.EldritchBot.discord.listeners.ListenerMessage;
import de.eldritch.EldritchBot.discord.listeners.ListenerReaction;
import de.eldritch.EldritchBot.discord.listeners.ListenerVoiceChannel;
import de.eldritch.EldritchBot.util.handlers.DiscordHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;

import static de.eldritch.EldritchBot.EldritchBot.*;

public class DiscordConnector {
    private static JDA jda;

    private final ArrayList<Command> commands = new ArrayList<>();

    private boolean isOnline = false;

    TextChannel logChannel;

    public DiscordConnector(String token) {
        try {
            JDABuilder builder = JDABuilder.createDefault(token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS);
            jda = builder.build();
            jda.awaitReady();
        } catch (Exception e) {
            logger.severe("Unable to implement JDA!");
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
            return;
        }

        logger.addHandler(new DiscordHandler());

        logChannel = getUpdatedLogChannel();

        isOnline = true;
        logger.info("JDA is online!");

        try {
            jda.getTextChannelById(configManager.getString("discord.logChannel", "null")).sendMessage(":green_circle: JDA is online!\nRunning `" + artifact + "` version `" + version + "`.").queue();
        } catch (NullPointerException ignored) {

        }

        logger.info("Registering EventListeners...");
        registerListeners();
        logger.info(jda.getEventManager().getRegisteredListeners().size() + " EventListeners registered.");

        logger.info("Registering commands...");
        registerCommands();
        logger.info(commands.size() + " Commands registered.");
    }

    private void registerListeners() {
        jda.addEventListener(new ListenerMessage());
        jda.addEventListener(new ListenerReaction());
        jda.addEventListener(new ListenerVoiceChannel());
    }

    private void registerCommands() {
        commands.clear();

        commands.add(new CommandPing(new String[] {"ping"}));
        commands.add(new CommandSay(new String[] {"say"}));
        commands.add(new CommandStop(new String[] {"stop", "shutdown", "exit"}));
        commands.add(new CommandWednesday(new String[] {"wednesday", "mittwoch", "wed", "mi"}));
    }

    @SuppressWarnings("ConstantConditions")
    public TextChannel createTicketChannel(int ticketId, Member author) {
        TextChannel ticketOpenChannel = jda.getTextChannelById(configManager.getString("modules.ticket.openChannel"));
        ticketOpenChannel.getParent().createTextChannel("\uD83D\uDCACticket-" + ticketId) // cannot produce NullPointerException because it was checked before
            .addPermissionOverride(author, EnumSet.of(Permission.VIEW_CHANNEL), EnumSet.of(Permission.MESSAGE_MANAGE)).complete();
        return jda.getTextChannelsByName("\uD83D\uDCACticket-" + ticketId, true).get(0);
    }

    public boolean isOnline() {
        return isOnline;
    }

    public JDA getJDA() {
        return jda;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public TextChannel getUpdatedLogChannel() {
        String logChannelId = configManager.getString("discord.logChannel", "null");
        if (logChannelId.equals("null"))
            return null;    // returns if the config is missing a discord.logChannel property

        return jda.getTextChannelById(logChannelId);
    }

    public TextChannel getUpdatedModChannel() {
        String modChannelId = configManager.getString("discord.modChannel", "null");
        if (modChannelId.equals("null"))
            return null;    // returns if the config is missing a discord.modChannel property

        return jda.getTextChannelById(modChannelId);
    }

    public IMentionable getModMention() {
        String modRoleId = configManager.getString("discord.modRole", "null");
        if (modRoleId.equals("null"))
            return null;    // return if the config is missing a discord.modRole property

        return jda.getRoleById(modRoleId);
    }

    public @Nullable User getOwner() {
        String ownerId = configManager.getString("discord.ownerId");
        return ownerId == null ? null : jda.getUserById(ownerId);
    }

    public void log(String msg) {
        if (logChannel == null)
            return; // return if the logChannel has not been specified yet
        logChannel.sendMessage(msg).queue();
    }

    public boolean checkGuildWhitelist(Guild guild) {
        String[] allowServers = configManager.getString("discord.allowServers", "null").split(",");

        for (String allowServer : allowServers) {
            if (allowServer.equals(guild.getId()))
                return true;
        }

        return false;
    }
}
