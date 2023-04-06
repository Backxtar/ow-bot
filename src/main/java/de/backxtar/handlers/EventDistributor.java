package de.backxtar.handlers;

import de.backxtar.OwBot;
import de.backxtar.commands.stats.StatsInteraction;
import de.backxtar.threads.UpdateCheck;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventDistributor extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent ctx) {
        if (ctx.getUser().isBot() || ctx.getChannelType() != ChannelType.TEXT) return;
        OwBot.getOwBot().getCmdRegister().executeCmd(ctx);
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent ctx) {
        ModalHandler handler = new ModalHandler(ctx);
        handler.handleModal();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent ctx) {
        StatsInteraction interaction = new StatsInteraction(ctx, ctx.getButton().getId());
        if (interaction.filterAction() && interaction.checkUser()) interaction.runAction();
        else ctx.deferEdit().queue();
    }

    @Override
    public void onReady(ReadyEvent event) {
        UpdateCheck.checkPatchNotes();
    }
}
