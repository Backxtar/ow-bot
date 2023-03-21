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
}
