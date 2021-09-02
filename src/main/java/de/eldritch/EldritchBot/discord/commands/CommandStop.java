package de.eldritch.EldritchBot.discord.commands;

import de.eldritch.EldritchBot.EldritchBot;
import de.eldritch.EldritchBot.discord.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static de.eldritch.EldritchBot.EldritchBot.configManager;
import static de.eldritch.EldritchBot.EldritchBot.logger;

public class CommandStop extends Command {
    public CommandStop(String[] commandAliases) {
        super(commandAliases);
    }

    public void onCommandRun(MessageReceivedEvent event) {
        if (!event.getAuthor().getId().equals(configManager.getString("discord.ownerId")))
            return;

        logger.info("Received stop command from user \"" + event.getAuthor().getName() + "\" (" + event.getAuthor().getId() + ")");

        EldritchBot.exit(0);
    }
}
