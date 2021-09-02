package de.eldritch.EldritchBot.modules;

import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.*;

import static de.eldritch.EldritchBot.EldritchBot.*;

public class VoiceChannelModule {
    public static ArrayList<VoiceChannel> observedChannels      = new ArrayList<>();    // empty channels to be cloned at join event
    public static ArrayList<VoiceChannel> observedChannelsUsed  = new ArrayList<>();    // used channels to be deleted at leave event

    public VoiceChannelModule() {
        this.loadObservedChannels();
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    private void loadObservedChannels() {
        ArrayList<VoiceChannel> voiceChannels = new ArrayList<>(discordConnector.getJDA().getVoiceChannels());

        String[] voiceKey = configManager.getString("modules.voiceChannel.key", "").split(",");
        ArrayList<String> allowServers = new ArrayList<>(Arrays.asList(configManager.getString("discord.allowServers", "").split(",")));

        logger.info("Indexing observed channels...");

        observedChannels.clear();
        observedChannelsUsed.clear();

        StringBuilder str = new StringBuilder("VoiceChannels:");

        for (VoiceChannel voiceChannel : voiceChannels) {
            String x = " ";
            for (String s : voiceKey) {
                if (voiceChannel.getName().toLowerCase().contains(s)
                        && allowServers.contains(voiceChannel.getGuild().getId())) {
                    observedChannels.add(voiceChannel);
                    x = "X";
                }
            }
            str.append("\n\t[")
                    .append(x)
                    .append("]  (")
                    .append(voiceChannel.getGuild().getId())
                    .append(" / ")
                    .append(voiceChannel.getId())
                    .append(")  \"")
                    .append(voiceChannel.getGuild().getName())
                    .append("\" / \"")
                    .append(voiceChannel.getName())
                    .append("\"");
        }

        logger.fine(str.toString());

        if (observedChannels.size() == 0) {
            logger.info("No observable channels found.");
        } else {
            logger.info(observedChannels.size() + " observable channels found.");
        }

        for (int i = 0; i < observedChannels.size(); i++) {
            if (!observedChannels.get(i).getMembers().isEmpty()) {
                observedChannelsUsed.add(observedChannels.get(i));
                observedChannels.remove(i);
            }
        }
    }

    public void channelJoined(VoiceChannel channel) {
        if (observedChannels.contains(channel)) {
            observedChannels.remove(channel);
            observedChannelsUsed.add(channel);

            channel.createCopy().queue();

            logger.info("Copied VoiceChannel \"" + channel.getName() + "\" on Guild \"" +  channel.getGuild().getName()
                    + "\"  [" + channel.getGuild().getId() + "/" + channel.getId() + "]");
        }
    }

    public void channelLeft(VoiceChannel channel) {
        if (observedChannelsUsed.contains(channel)) {
            if (channel.getMembers().isEmpty()) {
                observedChannelsUsed.remove(channel);
                channel.delete().queue();

                logger.info("Deleted VoiceChannel \"" + channel.getName() + "\" on Guild \"" +  channel.getGuild().getName()
                        + "\"  [" + channel.getGuild().getId() + "/" + channel.getId() + "]");
            }
        }
    }

    public void channelCreated(VoiceChannel channel) {
        String[] voiceKey = configManager.getString("modules.voiceChannel.key", "").split(",");

        for (String s : voiceKey) {
            if (channel.getName().toLowerCase().contains(s)) {
                observedChannels.add(channel);

                logger.info("Added VoiceChannel \"" + channel.getName() + "\" on Guild \"" +  channel.getGuild().getName()
                        + "\" to observed Channels.  [" + channel.getGuild().getId() + "/" + channel.getId() + "]");
            }
        }
    }

    public void channelDeleted(VoiceChannel channel) {
        String[] voiceKey = configManager.getString("modules.voiceChannel.key", "").split(",");

        for (String s : voiceKey) {
            if (channel.getName().toLowerCase().contains(s)) {
                observedChannels.remove(channel);
                observedChannelsUsed.remove(channel);

                logger.info("Removed VoiceChannel \"" + channel.getName() + "\" on Guild \"" +  channel.getGuild().getName()
                        + "\" from observed Channels.  [" + channel.getGuild().getId() + "/" + channel.getId() + "]");
            }
        }
    }
}
