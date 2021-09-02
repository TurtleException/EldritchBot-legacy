package de.eldritch.EldritchBot.sql;

import net.dv8tion.jda.api.entities.Message;

import java.sql.Timestamp;

public class Statements {
    public Statements() {

    }

    @SuppressWarnings("SameReturnValue")
    public static String openTickets() {
        return "SELECT * FROM tickets WHERE state <> 0";
    }

    public static String createTicket(int ticketId, String ticketAuthorId, Timestamp ticketTimestamp, String ticketChannelId, int ticketState) {
        return "INSERT INTO `tickets`"
                + "(`ID`, `user_opened_id`, `timestamp`, `channel_id`, `state`) VALUES "
                + "('" + ticketId + "','" + ticketAuthorId + "','" + ticketTimestamp + "','" + ticketChannelId + "','" + ticketState + "')";
    }

    public static String logTicketMessage(Message message, int ticketId) {
        return "INSERT INTO `ticket_messages`"
                + "(`ticket_id`, `message_id`, `user_id`, `timestamp`, `message`) VALUES "
                + "('" + ticketId + "','" + message.getId() + "','" + message.getAuthor().getId() + "','" + Timestamp.from(message.getTimeCreated().toInstant()) + "','" + message.getContentRaw() + "')";
    }

    public static String updateTicketState(int state, int ticketId) {
        return "UPDATE `tickets` "
                + "SET `state` = '" + state + "' "
                + "WHERE `ID` = '" + ticketId + "'";
    }
}
