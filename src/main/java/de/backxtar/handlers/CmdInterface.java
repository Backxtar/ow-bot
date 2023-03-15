package de.backxtar.handlers;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CmdInterface {

    /**
     * CommandInterface blueprint
     * @param ctx as JDA event
     */
    void performCmd(SlashCommandInteractionEvent ctx);
}
