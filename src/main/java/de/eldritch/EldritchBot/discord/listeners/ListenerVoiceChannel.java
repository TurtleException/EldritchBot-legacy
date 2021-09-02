package de.eldritch.EldritchBot.discord.listeners;

import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static de.eldritch.EldritchBot.EldritchBot.discordConnector;
import static de.eldritch.EldritchBot.EldritchBot.voiceChannelModule;

public class ListenerVoiceChannel extends ListenerAdapter {
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        if (!discordConnector.checkGuildWhitelist(event.getGuild()))
            return; // returns if the server is not whitelisted
        voiceChannelModule.channelJoined(event.getChannelJoined());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (!discordConnector.checkGuildWhitelist(event.getGuild()))
            return; // returns if the server is not whitelisted
        voiceChannelModule.channelLeft(event.getChannelLeft());
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (!discordConnector.checkGuildWhitelist(event.getGuild()))
            return; // returns if the server is not whitelisted
        voiceChannelModule.channelJoined(event.getChannelJoined());
        voiceChannelModule.channelLeft(event.getChannelLeft());
    }

    @Override
    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
        if (!discordConnector.checkGuildWhitelist(event.getGuild()))
            return; // returns if the server is not whitelisted
        voiceChannelModule.channelCreated(event.getChannel());
    }

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        if (!discordConnector.checkGuildWhitelist(event.getGuild()))
            return; // returns if the server is not whitelisted
        voiceChannelModule.channelDeleted(event.getChannel());
    }
}
