package de.backxtar.commands.stats;

import de.backxtar.api.Cache;
import de.backxtar.api.UserStats;
import de.backxtar.formatting.EmbedHelper;
import de.backxtar.handlers.CmdInterface;
import de.backxtar.utils.CmdUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class StatsCmd implements CmdInterface {

    @Override
    public void performCmd(SlashCommandInteractionEvent ctx) {
        final EmbedHelper helper1 = new EmbedHelper(ctx.getGuild());
        final EmbedHelper helper2 = new EmbedHelper();
        final EmbedBuilder builder1 = helper2.standardBuilder()
                .setDescription("<a:loading:1086206483609440286> *- Please wait...*");

        ctx.replyEmbeds(builder1.build())
                .queue();

        CmdUtils cmdUtils = new CmdUtils(ctx);        
        final String tag = cmdUtils.getTag();
        if (tag == null) return;

        UserStats stats;
        if (Cache.getStatsByTag(tag) != null) stats = Cache.getStatsByTag(tag);
        else {
            stats = cmdUtils.getStatsUser(tag);
            if (stats != null) Cache.addStats(stats, tag);
            else return;
        }

        Button topHeroes = Button.secondary("heroes:" + tag + ":" + ctx.getUser().getIdLong(), "TOP HEROES")
                .withEmoji(Emoji.fromFormatted("<:overwatch:1085346684223103106>"));
        Button comTitles = Button.danger("comTitles:" + tag + ":" + ctx.getUser().getIdLong(), "COMBAT TITLES")
                .withEmoji(Emoji.fromFormatted("<:offense_icon:1085339430467948636>"));
        Button assTitles = Button.success("assTitles:" + tag + ":" + ctx.getUser().getIdLong(), "ASSIST TITLES")
                .withEmoji(Emoji.fromFormatted("<:support_icon:1085339429012512788>"));
        Button bestTitles = Button.primary("bestTitles:" + tag + ":" + ctx.getUser().getIdLong(), "BEST PLAYS")
                .withEmoji(Emoji.fromFormatted("⭐"));

        final EmbedBuilder builder2 = helper1.standardBuilder()
                .setTitle("Statanfrage von " + ctx.getUser().getName())
                .setDescription("Hier eine Übersicht über **" + stats.getUsername() + "'s** Stats. Um Informationen zu bekommen, klicke auf die folgenden **Buttons**.")
                .setThumbnail(stats.getPortrait());

        ctx.getHook().editOriginalEmbeds(builder2.build())
                .flatMap(mes -> mes.editMessageComponents(
                        ActionRow.of(topHeroes, bestTitles), 
                        ActionRow.of(comTitles, assTitles)))
                .queue();
    }
}
