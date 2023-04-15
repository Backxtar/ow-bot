package de.backxtar.commands.stats;

import de.backxtar.api.Cache;
import de.backxtar.api.UserStats;
import de.backxtar.formatting.EmbedHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StatsInteraction {
    private final ButtonInteractionEvent ctx;
    private final String[] params;
    private String action;
    private String tag;
    private long userId = 0L;

    enum Destination {
        COMBAT,
        ASSIST,
        BEST
    }

    enum Mode {
        QUICKPLAY,
        COMPETITIVE
    }

    public StatsInteraction(ButtonInteractionEvent ctx, final String[] params) {
        this.ctx = ctx;
        this.params = params;
    }

    public boolean filterAction() {
        this.action = this.params[0];
        this.tag = this.params[1];
        this.userId = Long.parseLong(this.params[2]);
        return this.action != null && this.tag != null && this.userId != 0L;
    }

    public boolean checkUser() {
        return this.userId == this.ctx.getUser().getIdLong();
    }

    public void runAction() {
        final UserStats stats = Cache.getStatsByTag(this.tag);
        if (stats == null) {
            this.ctx.editButton(ctx.getButton().asDisabled())
                            .queue();
            return;
        }

        Collection<MessageEmbed> embeds = new ArrayList<>();

        if (Objects.equals(this.action, "heroes")) {
            int sizeQuick = stats.getStats().getTop_heroes().getQuickplay().getPlayed().length;
            int sizeComp = stats.getStats().getTop_heroes().getCompetitive().getPlayed().length;

            if (sizeQuick > 0) embeds.add(getHeroEmbed(stats, 0, Mode.QUICKPLAY).build());
            if (sizeQuick > 1) embeds.add(getHeroEmbed(stats, 1, Mode.QUICKPLAY).build());
            if (sizeQuick > 2) embeds.add(getHeroEmbed(stats, 2, Mode.QUICKPLAY).build());

            if (sizeComp > 0) embeds.add(getHeroEmbed(stats, 0, Mode.COMPETITIVE).build());
            if (sizeQuick > 1) embeds.add(getHeroEmbed(stats, 1, Mode.COMPETITIVE).build());
            if (sizeQuick > 2) embeds.add(getHeroEmbed(stats, 2, Mode.COMPETITIVE).build());
        }

        if (Objects.equals(this.action, "comTitles"))
            embeds.add(buildEmbed(stats, 
                                  "COMBAT TITLES", 
                                  "Diese **COMBAT** Erfolge hat **" + stats.getUsername() + "** bisher erreicht.", 
                                  Destination.COMBAT).build());

        if (Objects.equals(this.action, "assTitles"))
            embeds.add(buildEmbed(stats, 
                                  "ASSIST TITLES", 
                                  "Diese **ASSIST** Erfolge hat **" + stats.getUsername() + "** bisher erreicht.", 
                                  Destination.ASSIST).build());

        if (Objects.equals(this.action, "bestTitles"))
            embeds.add(buildEmbed(stats, 
                                  "AVERAGE STATS", 
                                  "Das sind die **besten Stats**, die " + stats.getUsername() + " bisher erreicht hat.", 
                                  Destination.BEST).build());

        this.ctx.editButton(this.ctx.getButton().asDisabled())
                .flatMap(done -> this.ctx.getHook().getInteraction().getMessageChannel().sendMessageEmbeds(embeds))
                .queue();
    }

    private EmbedBuilder buildEmbed(final UserStats stats, 
                                    final String title,
                                    final String desc, 
                                    final Destination dest) {
        final EmbedHelper helper = new EmbedHelper();
        EmbedBuilder builder = helper.standardBuilder()
                .setThumbnail(stats.getPortrait())
                .setTitle(title)
                .setDescription(desc);

        int quickLength = 0, compLength = 0;
        if (dest == Destination.BEST) {
            quickLength = stats.getStats().getBest().getQuickplay().length;
            compLength = stats.getStats().getBest().getCompetitive().length;
        }

        if (dest == Destination.COMBAT) {
            quickLength = stats.getStats().getCombat().getQuickplay().length;
            compLength = stats.getStats().getCombat().getCompetitive().length;
        }

        if (dest == Destination.ASSIST) {
            quickLength = stats.getStats().getAssists().getQuickplay().length;
            compLength = stats.getStats().getAssists().getCompetitive().length;
        }

        HashMap<String, String> quickplay = new HashMap<>();
        HashMap<String, String> competitive = new HashMap<>();
        if (quickLength > 0) quickplay = buildMap(stats, quickLength, dest, Mode.QUICKPLAY);
        if (compLength > 0) competitive = buildMap(stats, compLength, dest, Mode.COMPETITIVE);

        if (quickplay.size() > 0) {
            builder.addBlankField(false);
            builder.addField("🔻Quickplay", "", false);

            for (Map.Entry<String, String> entry : quickplay.entrySet())
                builder.addField(entry.getKey(), entry.getValue(), true);
        }

        if (competitive.size() > 0) {
            builder.addBlankField(false);
            builder.addField("🔻Competitive", "", false);

            for (Map.Entry<String, String> entry : competitive.entrySet())
                builder.addField(entry.getKey(), entry.getValue(), true);
        }

        if (builder.getFields().size() > 0) return builder;
        else return null;
    }

    private HashMap<String, String> buildMap(final UserStats stats, 
                                             final int length, 
                                             Destination dest, 
                                             Mode mode) {
        HashMap<String, String> keyValue = new HashMap<>();

        for (int i = 0; i < length; i++) {
            if (dest == Destination.BEST) {
                if (mode == Mode.QUICKPLAY) 
                    keyValue.put("🔸" + stats.getStats().getBest().getQuickplay()[i].getTitle(), 
                    "\uD83D\uDD39`" + stats.getStats().getBest().getQuickplay()[i].getValue() + "`");
                else keyValue.put(stats.getStats().getBest().getCompetitive()[i].getTitle(), 
                    "\uD83D\uDD39`" + stats.getStats().getBest().getCompetitive()[i].getValue() + "`");
            }
            if (dest == Destination.COMBAT) {
                if (mode == Mode.QUICKPLAY) 
                    keyValue.put("🔸" + stats.getStats().getCombat().getQuickplay()[i].getTitle(), 
                    "\uD83D\uDD39`" + stats.getStats().getCombat().getQuickplay()[i].getValue() + "`");
                else keyValue.put(stats.getStats().getCombat().getCompetitive()[i].getTitle(), 
                    "\uD83D\uDD39`" + stats.getStats().getCombat().getCompetitive()[i].getValue() + "`");
            }
            if (dest == Destination.ASSIST) {
                if (mode == Mode.QUICKPLAY) 
                    keyValue.put("🔸" + stats.getStats().getAssists().getQuickplay()[i].getTitle(), 
                    "\uD83D\uDD39`" + stats.getStats().getAssists().getQuickplay()[i].getValue() + "`");
                else keyValue.put(stats.getStats().getAssists().getCompetitive()[i].getTitle(), 
                    "\uD83D\uDD39`" + stats.getStats().getAssists().getCompetitive()[i].getValue() + "`");
            }
        }
        return keyValue;
    }

    //TODO: Make Hero Embeds variable
    private EmbedBuilder getHeroEmbed(final UserStats stats,
                                      final int pos,
                                      final Mode m) {
        final EmbedHelper helper = new EmbedHelper();

        final String play = switch (pos) {
            case 0 -> "meisten";
            case 1 -> "zweit meisten";
            case 2 -> "dritt meisten";
            default -> "";
        };
        final String mode = m == Mode.COMPETITIVE ? "🔸Competitive" : "🔸Quickplay";

        final UserStats.Played tmp;
        if (m == Mode.COMPETITIVE) tmp = stats.getStats().getTop_heroes().getCompetitive().getPlayed()[pos];
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

    public String[] getParams() {
        return params;
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
