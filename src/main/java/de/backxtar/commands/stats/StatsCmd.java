package de.backxtar.commands.stats;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.backxtar.OwBot;
import de.backxtar.api.JSONReader;
import de.backxtar.api.Cache;
import de.backxtar.api.UserStats;
import de.backxtar.formatting.EmbedHelper;
import de.backxtar.handlers.CmdInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class StatsCmd implements CmdInterface {

    @Override
    public void performCmd(SlashCommandInteractionEvent ctx) {
        final EmbedHelper helper1 = new EmbedHelper(ctx.getGuild());
        final EmbedHelper helper2 = new EmbedHelper();
        final EmbedBuilder builder1 = helper2.standardBuilder()
                .setDescription("<a:loading:1086206483609440286> *- Please wait...*");

        ctx.replyEmbeds(builder1.build())
                .queue();

        final String tag = getTag(ctx);
        if (tag == null) return;

        UserStats stats;
        if (Cache.getStatsByTag(tag) != null) stats = Cache.getStatsByTag(tag);
        else {
            stats = getStats(ctx, tag);
            if (stats != null) Cache.addStats(stats, tag);
            else return;
        }

        Button topHeroes = Button.secondary("heroes:" + tag + ":" + ctx.getUser().getIdLong(), "TOP HEROES");
        Button comTitles = Button.secondary("comTitles:" + tag + ":" + ctx.getUser().getIdLong(), "COMBAT TITLES");
        Button assTitles = Button.secondary("assTitles:" + tag + ":" + ctx.getUser().getIdLong(), "ASSIST TITLES");

        final EmbedBuilder builder2 = helper1.standardBuilder()
                .setTitle("Statanfrage von " + ctx.getUser().getName())
                .setDescription("Hier eine Übersicht über **" + stats.getUsername() + "'s** Stats. Um Informationen zu bekommen, klicke auf die folgenden **Buttons**.")
                .setThumbnail(stats.getPortrait());

        ctx.getHook().editOriginalEmbeds(builder2.build())
                .flatMap(mes -> mes.editMessageComponents(ActionRow.of(topHeroes, comTitles, assTitles)))
                .queue();

        /*Collection<MessageEmbed> embedsTotal = makeEmbedChain(checkApi(ctx));
        int size = embedsTotal.size();

        Collection<MessageEmbed> embedsPost1 = embedsTotal.stream().toList().subList(0, 10);
        Collection<MessageEmbed> embedsPost2 = embedsTotal.stream().toList().subList(10, size);

        ctx.getInteraction().getHook().editOriginalEmbeds(embedsPost1)
                .flatMap(mes -> mes.getChannel().asTextChannel().sendMessageEmbeds(embedsPost2))
                .queue();*/
    }

    private String getTag(SlashCommandInteractionEvent ctx) {
        List<OptionMapping> options = ctx.getInteraction().getOptionsByType(OptionType.STRING);
        final EmbedHelper helper = new EmbedHelper();
        String tag;

        if (options.size() > 0) {
            String tagTemp = options.get(0).getAsString();
            tag = tagTemp.replace("#", "-");
        } else {
            final String[] param = {"battletag"};
            final Object[] values = {ctx.getUser().getIdLong()};
            ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery(param, "user_battletags", "user_id = ?", values);

            if (rs == null) {
                ctx.getHook().editOriginalEmbeds(helper.standardBuilder().setDescription("<:no:1085863681503547432> *Datenbank ERROR!*").build()).queue();
                return null;
            }

            try {
                if (!rs.next()) {
                    ctx.getHook().editOriginalEmbeds(helper.standardBuilder().setDescription("<:no:1085863681503547432> *Kein Tag hinterlegt!*").build()).queue();
                    return null;
                }
                String tagTemp = rs.getString("battletag");
                tag = tagTemp.replace("#", "-");
            } catch (SQLException sqlEx) {
                ctx.getHook().editOriginalEmbeds(helper.standardBuilder().setDescription("<:no:1085863681503547432> *Datenbank ERROR!*").build()).queue();
                return null;
            }
        }
        return tag;
    }

    private UserStats getStats(SlashCommandInteractionEvent ctx, final String tag) {
        final EmbedHelper helper = new EmbedHelper();
        final UserStats stats = getUserStats(tag);

        if (stats == null || Objects.equals(stats.getMessage(), "Error: Profile not found")) {
            ctx.getHook().editOriginalEmbeds(helper.standardBuilder().setDescription("<:no:1085863681503547432> *Kein Profil gefunden!*").build()).queue();
            return null;
        }

        if (stats.getIsPrivate()) {
            ctx.getHook().editOriginalEmbeds(helper.standardBuilder().setDescription("<:no:1085863681503547432> *Das Profil ist privat!*").build()).queue();
            return null;
        }
        return stats;
    }

    private UserStats getUserStats(final String tag) {
        final Type return_type = new TypeToken<UserStats>() {
        }.getType();
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

        // Combat Titles
        builderList.add(getCombatEmbed(stats));
        // Assist Titles
        builderList.add(getAssistEmbed(stats));

        EmbedBuilder[] builders = new EmbedBuilder[builderList.size()];
        builderList.toArray(builders);
        return builders;
    }

    private EmbedBuilder getCombatEmbed(final UserStats stats) {
        final EmbedHelper helper = new EmbedHelper();
        EmbedBuilder builder = helper.standardBuilder()
                .setTitle("3️⃣ COMBAT TITLES")
                .setDescription("Diese `COMBAT` Erfolge hat **" + stats.getUsername() + "** bisher erreicht.");

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

    private EmbedBuilder getAssistEmbed(final UserStats stats) {
        final EmbedHelper helper = new EmbedHelper();
        EmbedBuilder builder = helper.standardBuilder()
                .setTitle("4️⃣ ASSIST TITLES")
                .setDescription("Diese `ASSIST` Erfolge hat **" + stats.getUsername() + "** bisher erreicht.");

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
