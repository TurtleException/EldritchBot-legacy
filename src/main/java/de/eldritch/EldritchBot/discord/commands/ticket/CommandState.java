package de.eldritch.EldritchBot.discord.commands.ticket;

import de.eldritch.EldritchBot.discord.CommandTicket;
import de.eldritch.EldritchBot.modules.ticket.Ticket;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static de.eldritch.EldritchBot.EldritchBot.configManager;
import static de.eldritch.EldritchBot.EldritchBot.logger;

public class CommandState extends CommandTicket {
    public CommandState(String[] commandAliases) {
        super(commandAliases);
    }

    public void onCommandRun(MessageReceivedEvent event, Ticket ticket) {
        String cmdPrefix = configManager.getString("discord.prefix");

        if (event.getMessage().getAuthor().getId().equals(configManager.getString("discord.ownerId"))) {
            String[] cmd = event.getMessage().getContentRaw().substring((cmdPrefix + "ticket").length() + 1).split(" ");

            // change ticket state
            if (cmd[0].equals("state") && cmd.length > 1) {
                int newstate = ticket.getTicketState();
                try {
                    newstate = Integer.parseInt(cmd[1]);
                } catch (NumberFormatException e) {
                    logger.warning("Unable to change state of ticket " + ticket.getTicketId() + " due to a NumberFormatException.");
                    logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);

                    event.getMessage().addReaction("U+2705").queue();
                }
                ticket.setTicketState(newstate);
                if (newstate == 0) ticket.close();
            } else if (event.getMessage().getContentRaw().endsWith("state")) {
                event.getMessage().reply("Aktueller Status von Ticket `" + ticket.getTicketId() + "`: **" + ticket.getTicketState() + "**").queue();
            }
        }
    }
}
