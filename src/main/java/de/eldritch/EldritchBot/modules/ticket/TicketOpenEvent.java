package de.eldritch.EldritchBot.modules.ticket;

import net.dv8tion.jda.api.entities.Member;

import java.sql.Timestamp;

public class TicketOpenEvent {
    private final Member ticketAuthor;
    private final Timestamp ticketTimestamp;

    public TicketOpenEvent(Member author) {
        ticketAuthor = author;

        ticketTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Member getAuthor() {
        return ticketAuthor;
    }

    public Timestamp getTimestamp() {
        return ticketTimestamp;
    }
}
