package de.eldritch.EldritchBot.discord.commands;

import de.eldritch.EldritchBot.discord.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.LocalDate;

public class CommandWednesday extends Command {
    public CommandWednesday(String[] commandAliases) {
        super(commandAliases);
    }

    public void onCommandRun(MessageReceivedEvent event) {
        if (LocalDate.now().getDayOfWeek().getValue() == 3) {
            event.getChannel().sendMessage("Es ist Mittwoch meine Kerle :frog:").queue();
        } else {
            event.getChannel().sendMessage("Es ist nicht Mittwoch meine Kerle... :pensive:").queue();
        }
    }
}
