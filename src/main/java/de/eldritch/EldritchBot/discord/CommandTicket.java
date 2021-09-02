package de.eldritch.EldritchBot.discord;

import de.eldritch.EldritchBot.modules.ticket.Ticket;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class CommandTicket extends Command {
    public CommandTicket(String[] commandAliases) {
        super(commandAliases);
    }

    public abstract void onCommandRun(MessageReceivedEvent event, Ticket ticket);

    public void onCommandRun(MessageReceivedEvent event) {
        onCommandRun(event, null);
    }
}
