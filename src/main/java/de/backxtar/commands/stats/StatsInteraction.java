package de.backxtar.commands.stats;

import de.backxtar.api.Cache;
import de.backxtar.api.UserStats;
import de.backxtar.formatting.EmbedHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class StatsInteraction {
    private final ButtonInteractionEvent ctx;
    private final String id;
    private String action;
    private String tag;
    private long userId = 0L;

    public StatsInteraction(ButtonInteractionEvent ctx, final String id) {
        this.ctx = ctx;
        this.id = id;
    }

    public boolean filterAction() {
        String[] params = this.id.split(":");
        if (params.length != 3) return false;
        this.action = params[0];
        this.tag = params[1];
        this.userId = Long.parseLong(params[2]);
        return this.action != null && this.tag != null && this.userId != 0L;
    }

    public void runAction() {
        final UserStats stats = Cache.getStatsByTag(this.tag);
        if (stats == null) {
            ctx.deferEdit().queue();
            return;
        }

        Collection<MessageEmbed> embeds = new ArrayList<>();

        if (Objects.equals(this.action, "heroes")) {
            int sizeQuick = stats.getStats().getTop_heroes().getQuickplay().getPlayed().length;
            int sizeComp = stats.getStats().getTop_heroes().getCompetitive().getPlayed().length;

            if (sizeQuick > 0) embeds.add(getHeroEmbed(stats, 0, false).build());
            if (sizeQuick > 1) embeds.add(getHeroEmbed(stats, 1, false).build());
            if (sizeQuick > 2) embeds.add(getHeroEmbed(stats, 2, false).build());

            if (sizeComp > 0) embeds.add(getHeroEmbed(stats, 0, true).build());
            if (sizeQuick > 1) embeds.add(getHeroEmbed(stats, 1, true).build());
            if (sizeQuick > 2) embeds.add(getHeroEmbed(stats, 2, true).build());
        }

        if (Objects.equals(this.action, "comTitles"))
            embeds.add(getCombatEmbed(stats).build());

        if (Objects.equals(this.action, "assTitles"))
            embeds.add(getAssistEmbed(stats).build());

        this.ctx.replyEmbeds(embeds).queue();
    }

    private EmbedBuilder getAssistEmbed(final UserStats stats) {
        final EmbedHelper helper = new EmbedHelper();
        EmbedBuilder builder = helper.standardBuilder()
                .setThumbnail(stats.getPortrait())
                .setTitle("ASSIST TITLES")
                .setDescription("Diese **ASSIST** Erfolge hat **" + stats.getUsername() + "** bisher erreicht.");

        int compLength = stats.getStats().getAssists().getCompetitive().length;
        int quickLength = stats.getStats().getAssists().getQuickplay().length;

        if (quickLength > 0) builder.addBlankField(false);
        for (int i = 0; i < quickLength; i++) {
            if (i == 0) builder.addField("🔻MODUS: Quickplay", "", false);
            builder.addField(
                    "🔸" + stats.getStats().getAssists().getQuickplay()[i].getTitle(),
                    "\uD83D\uDD39`" + stats.getStats().getAssists().getQuickplay()[i].getValue() + "`",
                    true);
        }
        if (compLength > 0) builder.addBlankField(false);
        for (int i = 0; i < compLength; i++) {
            if (i == 0) builder.addField("🔻MODUS: Competitive", "", false);
            builder.addField(
                    "🔸" + stats.getStats().getAssists().getCompetitive()[i].getTitle(),
                    "\uD83D\uDD39`" + stats.getStats().getAssists().getCompetitive()[i].getValue() + "`",
                    true);
        }
        return builder;
    }

    private EmbedBuilder getCombatEmbed(final UserStats stats) {
        final EmbedHelper helper = new EmbedHelper();
        EmbedBuilder builder = helper.standardBuilder()
                .setThumbnail(stats.getPortrait())
                .setTitle("COMBAT TITLES")
                .setDescription("Diese **COMBAT** Erfolge hat **" + stats.getUsername() + "** bisher erreicht.");

        int compLength = stats.getStats().getCombat().getCompetitive().length;
        int quickLength = stats.getStats().getCombat().getQuickplay().length;

        if (quickLength > 0) builder.addBlankField(false);
        for (int i = 0; i < quickLength; i++) {
            if (i == 0) builder.addField("🔻MODUS: Quickplay", "", false);
            builder.addField(
                    "🔸" + stats.getStats().getCombat().getQuickplay()[i].getTitle(),
                    "\uD83D\uDD39`" + stats.getStats().getCombat().getQuickplay()[i].getValue() + "`",
                    true);
        }
        if (compLength > 0) builder.addBlankField(false);
        for (int i = 0; i < compLength; i++) {
            if (i == 0) builder.addField("🔻MODUS: Competitive", "", false);
            builder.addField(
                    "🔸" + stats.getStats().getCombat().getCompetitive()[i].getTitle(),
                    "\uD83D\uDD39`" + stats.getStats().getCombat().getCompetitive()[i].getValue() + "`",
                    true);
        }
        return builder;
    }

    private EmbedBuilder getHeroEmbed(final UserStats stats,
                                      final int pos,
                                      final boolean competitive) {
        final EmbedHelper helper = new EmbedHelper();

        final String play = switch (pos) {
            case 0 -> "meisten";
            case 1 -> "zweit meisten";
            case 2 -> "dritt meisten";
            default -> "";
        };
        final String mode = competitive ? "🔸Competitive" : "🔸Quickplay";

        final UserStats.Played tmp;
        if (competitive) tmp = stats.getStats().getTop_heroes().getCompetitive().getPlayed()[pos];
        else tmp = stats.getStats().getTop_heroes().getQuickplay().getPlayed()[pos];

        return helper.standardBuilder()
                .setTitle(tmp.getHero())
                .setThumbnail(tmp.getImg())
                .setDescription("Dieser Held wurde von **" + stats.getUsername() + "** am " + play + " gespielt.")
                .addField("🔻MODUS", mode, true)
                .addField("🔻SPIELZEIT", "🔸`" + tmp.getPlayed() + "`", true);
    }

    public ButtonInteractionEvent getCtx() {
        return this.ctx;
    }

    public String getId() {
        return this.id;
    }

    public String getAction() {
        return this.action;
    }

    public String getTag() {
        return this.tag;
    }

    public long getUserId() {
        return this.userId;
    }
}
