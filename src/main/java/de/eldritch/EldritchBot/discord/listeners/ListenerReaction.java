package de.eldritch.EldritchBot.discord.listeners;

import de.eldritch.EldritchBot.modules.ticket.TicketOpenEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static de.eldritch.EldritchBot.EldritchBot.*;

public class ListenerReaction extends ListenerAdapter {
    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (!discordConnector.checkGuildWhitelist(event.getGuild())     // ... the server is not whitelisted
            || event.getUser().isBot())                                 // ... the reaction is added by a bot
            return; // returns when...

        logger.fine("onGuildMessageReactionAdd performed by "
                + event.getMember().getUser().getName()
                + " (" + event.getMember().getId() + ")"
                + " in #" + event.getChannel().getName()
                + " (" + event.getGuild().getId() + "/"
                + event.getChannel().getId() + ")" + ": "
                + event.getReaction());


        // - - - TICKET SYSTEM
        if (!event.getChannel().getId().equals(configManager.getString("modules.ticket.openChannel")))
            return; // returns if the event channel is not the specified openChannel
        if (!event.getMessageId().equals(configManager.getString("modules.ticket.openMessage")))
            return; // returns if the event message is not the specified openMessage
        if (!event.getReactionEmote().getEmoji().equals(configManager.getString("modules.ticket.openEmote")))
            return; // returns if the event emote is not the specified openEmote

        ticketModule.createTicket(new TicketOpenEvent(event.getMember()));

        event.getReaction().removeReaction().queue();   // remove the reaction to allow the user to open another ticket later
    }
}
