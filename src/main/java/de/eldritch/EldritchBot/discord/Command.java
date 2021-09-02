package de.eldritch.EldritchBot.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class Command {
    final String[] aliases;

    public Command(String[] commandAliases) {
        aliases = commandAliases;
    }

    public abstract void onCommandRun(MessageReceivedEvent event);

    public boolean hasAlias(String checkAlias) {
        for (String alias : aliases)
            if (alias.equalsIgnoreCase(checkAlias))
                return true;
        return false;
    }
}
