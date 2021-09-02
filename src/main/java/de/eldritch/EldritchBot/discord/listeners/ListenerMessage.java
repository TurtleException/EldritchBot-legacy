package de.eldritch.EldritchBot.discord.listeners;

import de.eldritch.EldritchBot.discord.Command;
import de.eldritch.EldritchBot.modules.ticket.Ticket;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

import static de.eldritch.EldritchBot.EldritchBot.*;

public class ListenerMessage extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()                                       // ... message is from a bot
                || event.isFromType(ChannelType.PRIVATE)                    // ... message is private
                || !discordConnector.checkGuildWhitelist(event.getGuild())) // ... server is not whitelisted
            return; // return if ...

        try {
            Ticket ticket = ticketModule.isTicketChannel((TextChannel) event.getChannel());
            if (ticket != null)
                ticket.onMessage(event);
        } catch (NullPointerException ignored) {

        }


        // - - - COMMAND

        if (event.getMessage().getContentRaw().startsWith(configManager.getString("discord.prefix", "null"))) {
            if (event.getMessage().getContentRaw().startsWith("null"))
                return; // returns if the message was wrongly identified as a command

            String[] args = event.getMessage().getContentRaw().substring(configManager.getString("discord.prefix").length()).split(" ");  // cmd args without prefix

            ArrayList<Command> commands = discordConnector.getCommands();

            for (Command command : commands) {
                if (command.hasAlias(args[0])) {
                    command.onCommandRun(event);
                    return;
                }
            }
        }
    }
}
