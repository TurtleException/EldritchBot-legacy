package de.eldritch.EldritchBot.discord.commands;

import de.eldritch.EldritchBot.discord.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static de.eldritch.EldritchBot.EldritchBot.configManager;

public class CommandSay extends Command {
    public CommandSay(String[] commandAliases) {
        super(commandAliases);
    }

    public void onCommandRun(MessageReceivedEvent event) {
        if (event.getAuthor().getId().equals(configManager.getString("discord.ownerId"))) {
            event.getChannel().sendMessage(event.getMessage().getContentRaw().substring(configManager.getString("discord.prefix").length() + 4)).queue();
            event.getMessage().delete().queue();
        }
    }
}
