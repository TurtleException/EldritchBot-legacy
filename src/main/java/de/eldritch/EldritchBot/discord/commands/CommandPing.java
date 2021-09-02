package de.eldritch.EldritchBot.discord.commands;

import de.eldritch.EldritchBot.discord.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandPing extends Command {
    public CommandPing(String[] commandAliases) {
        super(commandAliases);
    }

    public void onCommandRun(MessageReceivedEvent event) {
        event.getChannel().sendMessage("Pong!").queue();
    }
}
