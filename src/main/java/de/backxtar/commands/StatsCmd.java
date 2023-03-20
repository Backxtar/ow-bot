package de.backxtar.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.backxtar.OwBot;
import de.backxtar.api.JSONReader;
import de.backxtar.api.UserStats;
import de.backxtar.formatting.EmbedHelper;
import de.backxtar.handlers.CmdInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class StatsCmd implements CmdInterface {

    @Override
    public void performCmd(SlashCommandInteractionEvent ctx) {
        final EmbedHelper helper = new EmbedHelper();
        final EmbedBuilder builder = helper.standardBuilder()
                .setDescription("<a:loading:1086206483609440286> *- Please wait...*");

        ctx.replyEmbeds(builder.build())
                .flatMap(mes -> mes.editOriginalEmbeds(makeEmbedChain(checkApi(ctx))))
                .queue();
    }

    private EmbedBuilder[] checkApi(SlashCommandInteractionEvent ctx) {
        List<OptionMapping> options = ctx.getInteraction().getOptionsByType(OptionType.STRING);
        final EmbedHelper helper = new EmbedHelper();
        EmbedBuilder builder = helper.standardBuilder();
        EmbedBuilder[] builders;
        final UserStats stats;

        if (options.size() > 0) {
            String tag = options.get(0).getAsString();
            tag = tag.replace("#", "-");
            stats = getUserStats(tag);
        } else {
            final String[] param = { "battletag" };
            final Object[] values = { ctx.getUser().getIdLong() };
            ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery(param, "user_battletags", "user_id = ?", values);
            if (rs == null) {
                builder.setDescription("<:no:1085863681503547432> *Datenbank ERROR!*");
                builders = new EmbedBuilder[1];
                builders[0] = builder;
                return builders;
            }

            try {
                if (!rs.next()) {
                    builder.setDescription("<:no:1085863681503547432> *Kein Tag hinterlegt!*");
                    builders = new EmbedBuilder[1];
                    builders[0] = builder;
                    return builders;
                }
                String tag = rs.getString("battletag");
                tag = tag.replace("#", "-");
                stats = getUserStats(tag);
            } catch (SQLException sqlEx) {
                builder.setDescription("<:no:1085863681503547432> *Datenbank ERROR!*");
                builders = new EmbedBuilder[1];
                builders[0] = builder;
                return builders;
            }
        }

        if (stats == null || Objects.equals(stats.getMessage(), "Error: Profile not found")) {
            builder.setDescription("<:no:1085863681503547432> *Kein Profil gefunden!*");
            builders = new EmbedBuilder[1];
            builders[0] = builder;
            return builders;
        }

        if (stats.getIsPrivate()) {
            builder.setDescription("<:no:1085863681503547432> *Das Profil ist privat!*");
            builders = new EmbedBuilder[1];
            builders[0] = builder;
            return builders;
        }

        builders = createInfoEmbeds(ctx, stats);
        return builders;
    }

    private UserStats getUserStats(final String tag) {
        final Type return_type = new TypeToken<UserStats>() {}.getType();
        final Gson gson = new Gson();

        try {
            final JSONReader reader = new JSONReader("https://owapi.io/stats/pc/eu/" + tag);
            return gson.fromJson(reader.getJSON(), return_type);
        } catch (IOException ioe) {
            return null;
        }
    }

    private EmbedBuilder[] createInfoEmbeds(SlashCommandInteractionEvent ctx, final UserStats stats) {
        final EmbedHelper helper = new EmbedHelper(ctx.getGuild()), helper2 = new EmbedHelper();
        List<EmbedBuilder> builderList = new ArrayList<>();

        builderList.add(helper.standardBuilder()
                .setTitle("Statanfrage von " + ctx.getUser().getName())
                .setDescription("Hier eine Übersicht über **" + stats.getUsername() + "'s** Stats")
                .setThumbnail(stats.getPortrait()));

        // TOP Heroes played
        int playedSize = stats.getStats().getTop_heroes().getQuickplay().getPlayed().length;
        if (playedSize > 0) {
            builderList.add(helper2.standardBuilder().setTitle("1️⃣ Top Heroes: Quickplay"));
            builderList.add(getHeroEmbed(stats, 0, false));
        }
        if (playedSize > 1) builderList.add(getHeroEmbed(stats, 1, false));
        if (playedSize > 2) builderList.add(getHeroEmbed(stats, 2, false));

        int playedSizeComp = stats.getStats().getTop_heroes().getCompetitive().getPlayed().length;
        if (playedSizeComp > 0) {
            builderList.add(helper2.standardBuilder().setTitle("2️⃣ TOP HEROES: Competitive"));
            builderList.add(getHeroEmbed(stats, 0, true));
        }
        if (playedSizeComp > 1) builderList.add(getHeroEmbed(stats, 1, true));
        if (playedSizeComp > 2) builderList.add(getHeroEmbed(stats, 2, true));

        //
        builderList.add(getCombatEmbed(stats));

        EmbedBuilder[] builders = new EmbedBuilder[builderList.size()];
        builderList.toArray(builders);
        return builders;
    }

    private EmbedBuilder getCombatEmbed(final UserStats stats) {
        final EmbedHelper helper = new EmbedHelper();
        EmbedBuilder builder = helper.standardBuilder()
                .setTitle("3️⃣ COMBAT TITLES")
                .setDescription("Diese Erfolge hat **" + stats.getUsername() + "** bisher erreicht.");

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

    private Collection<MessageEmbed> makeEmbedChain(EmbedBuilder[] builders) {
        MessageEmbed[] embeds = new MessageEmbed[builders.length];
        for (int i = 0; i < embeds.length; i++)
            embeds[i] = builders[i].build();
        return Arrays.stream(embeds).collect(Collectors.toList());
    }
}
