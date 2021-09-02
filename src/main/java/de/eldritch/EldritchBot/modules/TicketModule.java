package de.eldritch.EldritchBot.modules;

import de.eldritch.EldritchBot.modules.ticket.Ticket;
import de.eldritch.EldritchBot.modules.ticket.TicketOpenEvent;
import de.eldritch.EldritchBot.sql.Statements;
import de.eldritch.EldritchBot.util.IdGenerator;
import de.eldritch.EldritchBot.sql.Connection;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;

import static de.eldritch.EldritchBot.EldritchBot.*;

public class TicketModule {
    private final Connection sqlConnector;
    @SuppressWarnings("FieldMayBeFinal")
    private ArrayList<Ticket> tickets;  // OPEN tickets

    public TicketModule(Connection sql) {
        sqlConnector = sql;
        tickets = new ArrayList<>();

        if (!sql.isOnline()) return;

        this.loadOpenTickets();
    }

    private void loadOpenTickets() {
        tickets.clear();
        ResultSet res;

        try {
            res = sqlConnector.execute(Statements.openTickets());

            if (!res.isBeforeFirst()) {
                logger.info("Unable to load any open tickets.");
                return;
            } else {
                res.next();
            }

            while (!res.isAfterLast()) {
                int         ticketId        = res.getInt(1);
                int         ticketState     = res.getInt(5);
                Timestamp ticketTimestamp = res.getTimestamp(3);

                Member ticketAuthor = discordConnector.getJDA().getGuildById(configManager.getString("modules.ticket.server")).getMemberById(res.getLong(2));
                TextChannel ticketChannel = discordConnector.getJDA().getTextChannelById(res.getLong(4));

                if (ticketAuthor != null && ticketTimestamp != null && ticketChannel != null) {
                    tickets.add(new Ticket(ticketId, ticketAuthor, ticketTimestamp, ticketChannel, ticketState));
                } else {
                    logger.warning("Unable to load an open ticket! (At least one parameter is null)"
                        + "\n\t[" + ticketAuthor + ", " + ticketTimestamp + ", " + ticketChannel + "]"
                        + "\n\t[" + res.getLong(2) + "]");
                }
                
                res.next();
            }
            logger.info("Successfully loaded " + tickets.size() + " ticket(s).");
        } catch (SQLException e) {
            logger.warning("Unable to load open tickets due to an SQL-Exception!");
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
        }
    }

    public void createTicket(TicketOpenEvent event) {
        int ticketId = IdGenerator.generateTicketId();
        TextChannel ticketChannel = discordConnector.createTicketChannel(ticketId, event.getAuthor());

        Ticket ticket = new Ticket(ticketId, event.getAuthor(), event.getTimestamp(), ticketChannel, 1);

        try {
            sqlConnector.executeSilent(ticket.sqlInsert());
        } catch (SQLException e) {
            logger.warning("Unable to save ticket \"" + ticketId + "\" due to an SQL-Exception!");
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
        }
        tickets.add(ticket);

        MessageBuilder modMsg = new MessageBuilder();
        modMsg.append("[||")
                .append(discordConnector.getModMention().getAsMention())
                .append(" ");
        try {
            modMsg.append(Objects.requireNonNull(discordConnector.getUpdatedModChannel().getGuild().getMember(discordConnector.getOwner())).getAsMention());
        } catch (NullPointerException e) {
            modMsg.append("@null");
            logger.throwing(e.getClass().getName(), e.getStackTrace()[0].getMethodName(), e);
        }
        modMsg.append("||] ")
                .append(event.getAuthor().getAsMention())
                .append(" hat ")
                .append(ticketChannel.getAsMention())
                .append(" geöffnet.");

        ticket.getTicketChannel().sendMessage("Hey " + ticket.getTicketAuthor().getAsMention() + ", wie können wir dir helfen?").queue();
        discordConnector.getUpdatedModChannel().sendMessage(modMsg.build()).queue();

        logger.info("Successfully created ticket #" + ticketId + "\n\t["
                + "C: " + ticketChannel.getId() + ", "
                + "U: " + event.getAuthor().getId() + ", "
                + "S: " + ticket.getTicketState()
                + "]");
    }

    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
    }

    public Ticket isTicketChannel(TextChannel channel) {
        for (Ticket ticket : tickets) {
            if (ticket.getTicketChannel().equals(channel))
                return ticket;
        }
        return null;
    }
}
