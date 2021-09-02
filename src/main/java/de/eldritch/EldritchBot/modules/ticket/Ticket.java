package de.eldritch.EldritchBot.modules.ticket;

import de.eldritch.EldritchBot.discord.CommandTicket;
import de.eldritch.EldritchBot.discord.commands.ticket.CommandState;
import de.eldritch.EldritchBot.sql.Statements;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

import static de.eldritch.EldritchBot.EldritchBot.*;

public class Ticket {
    private int ticketState;

    private final int ticketId;
    private final Member ticketAuthor;
    private final Timestamp ticketTimestamp;
    private final TextChannel ticketChannel;

    private final ArrayList<CommandTicket> commands = new ArrayList<>();

    public Ticket(int id, @Nullable Member author, Timestamp timestamp, @Nullable TextChannel channel, int state) {
        ticketState = state;

        ticketId = id;
        ticketAuthor = author;
        ticketTimestamp = timestamp;
        ticketChannel = channel;

        registerCommands();
    }

    private void registerCommands() {
        commands.clear();

        commands.add(new CommandState(new String[] {"state"}));
    }

    public void setTicketState(int newstate) {
        ticketState = newstate;
        try {
            sqlConnector.executeSilent(Statements.updateTicketState(newstate, ticketId));
        } catch (SQLException e) {
            logger.warning("Unable to change ticketState in SQL database!");
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
        }
    }

    public int getTicketState() {
        return ticketState;
    }

    public int getTicketId() {
        return ticketId;
    }

    public Member getTicketAuthor() {
        return ticketAuthor;
    }

    public Timestamp getTicketTimestamp() {
        return ticketTimestamp;
    }

    public TextChannel getTicketChannel() {
        return ticketChannel;
    }

    public String sqlInsert() {
        String ticketAuthorId = Objects.requireNonNull(ticketAuthor).getId();
        String ticketChannelId = Objects.requireNonNull(ticketChannel).getId();

        return Statements.createTicket(ticketId, ticketAuthorId, ticketTimestamp, ticketChannelId, ticketState);
    }


    public void onMessage(MessageReceivedEvent event) {
        // - - - LOG
        try {
            sqlConnector.executeSilent(Statements.logTicketMessage(event.getMessage(), ticketId));
        } catch (SQLException e) {
            logger.warning("Unable to log Message from ticket " + ticketId);
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
        }


        // - - - COMMANDS
        if (event.getMessage().getContentRaw().startsWith(configManager.getString("discord.prefix", "null") + "ticket")) {
            if (event.getMessage().getContentRaw().startsWith("null"))
                return; // returns if the message was wrongly identified as a command

            String[] args = event.getMessage().getContentRaw().substring(configManager.getString("discord.prefix").length()).split(" ");  // cmd args without prefix

            if (args.length <= 1) return; // return if the command has no arguments except for "ticket"

            for (CommandTicket command : commands) {
                if (command.hasAlias(args[1])) {
                    command.onCommandRun(event, this);
                    return;
                }
            }
        }
    }

    public void close() {
        if (ticketState != 0) this.setTicketState(0);               // set ticket state to 0
        if (ticketChannel != null) ticketChannel.delete().queue();  // delete ticket Channel

        ticketModule.removeTicket(this);    // remove ticket from list
        /*
            There should not be any references of this ticket anywhere
            so JGC should delete the object soon.
         */
    }
}
