package de.eldritch.EldritchBot.discord.commands.ticket;

import de.eldritch.EldritchBot.discord.CommandTicket;
import de.eldritch.EldritchBot.modules.ticket.Ticket;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandInfo extends CommandTicket {
    public CommandInfo(String[] commandAliases) {
        super(commandAliases);
    }

    public void onCommandRun(MessageReceivedEvent event, Ticket ticket) {
        // TODO
    }
}
