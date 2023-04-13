package de.backxtar.commands;

import de.backxtar.formatting.EmbedHelper;
import de.backxtar.handlers.CmdInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class HelpCmd implements CmdInterface {
    @Override
    public void performCmd(SlashCommandInteractionEvent ctx) {
        ctx.replyEmbeds(createEmbed(ctx).build())
                .setEphemeral(false)
                .queue();
    }

    private EmbedBuilder createEmbed(SlashCommandInteractionEvent ctx) {
        EmbedHelper helper = new EmbedHelper(ctx.getGuild());
        return helper.standardBuilder()
                .setTitle(ctx.getJDA().getSelfUser().getName() + " Hilfe")
                .setDescription("Guude, ich bin eine künstliche Intelligenz, um Euch Informationen über **Overwatch 2** auszugeben!")
                .addField("<:overwatch:1085346684223103106> WICHTIG", "Alle meine Commands sind Slash-Commands. Diese werden mit `/` in einem Channel ausgeführt! Mit `*` **gekennzeichnete Variablen** sind Pflicht!", false)
                .addField("`/help`", "*Gibt die Bot-Hilfe aus*", true)
                .addField("`/tag`", "*Speichert/aktualisiert dein Tag*", true)
                .addField("`/profile [tag]`", "*Gibt Profilinformationen aus*", true)
                .addField("`/stats [tag]`", "*Gibt Statinformationen aus*", true)
                .addField("`/update`", "*Gibt OW2 Updateinfos aus*", true)
                .addField("`/setup`", "*Ruft das Setup auf*", true);
    }
}
