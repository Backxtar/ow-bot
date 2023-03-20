package de.backxtar.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.backxtar.OwBot;
import de.backxtar.api.JSONReader;
import de.backxtar.api.UserProfile;
import de.backxtar.formatting.EmbedHelper;
import de.backxtar.handlers.CmdInterface;
import de.backxtar.utils.RankSelector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ProfileCmd implements CmdInterface {

    @Override
    public void performCmd(SlashCommandInteractionEvent ctx) {
        final EmbedHelper helper = new EmbedHelper();
        final EmbedBuilder builder = helper.standardBuilder()
                .setDescription("<a:loading:1086206483609440286> *- Please wait...*");

        ctx.replyEmbeds(builder.build())
                .flatMap(mes -> mes.editOriginalEmbeds(checkApi(ctx).build()))
                .queue();
    }

    private EmbedBuilder checkApi(SlashCommandInteractionEvent ctx) {
        List<OptionMapping> options = ctx.getInteraction().getOptionsByType(OptionType.STRING);
        final EmbedHelper helper = new EmbedHelper();
        EmbedBuilder builder = helper.standardBuilder();
        final UserProfile profile;

        if (options.size() > 0) {
            String tag = options.get(0).getAsString();
            tag = tag.replace("#", "-");
            profile = getUserProfile(tag);
        } else {
            final String[] param = { "battletag" };
            final Object[] values = { ctx.getUser().getIdLong() };
            ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery(param, "user_battletags", "user_id = ?", values);
            if (rs == null) {
                builder.setDescription("<:no:1085863681503547432> *Datenbank ERROR!*");
                return builder;
            }

            try {
                if (!rs.next()) {
                    builder.setDescription("<:no:1085863681503547432> *Kein Tag hinterlegt!*");
                    return builder;
                }
                String tag = rs.getString("battletag");
                tag = tag.replace("#", "-");
                profile = getUserProfile(tag);
            } catch (SQLException sqlEx) {
                builder.setDescription("<:no:1085863681503547432> *Datenbank ERROR!*");
                return builder;
            }
        }

        if (profile == null || Objects.equals(profile.getMessage(), "Error: Profile not found")) {
            builder.setDescription("<:no:1085863681503547432> *Kein Profil gefunden!*");
            return builder;
        }

        if (profile.getIsPrivate()) {
            builder.setDescription("<:no:1085863681503547432> *Das Profil ist privat!*");
            return builder;
        }

        builder = createInfoEmbed(ctx, profile);
        return builder;
    }

    private UserProfile getUserProfile(final String tag) {
        final Type return_type = new TypeToken<UserProfile>() {}.getType();
        final Gson gson = new Gson();

        try {
            final JSONReader reader = new JSONReader("https://owapi.io/profile/pc/eu/" + tag);
            return gson.fromJson(reader.getJSON(), return_type);
        } catch (IOException ioe) {
            return null;
        }
    }

    private EmbedBuilder createInfoEmbed(SlashCommandInteractionEvent ctx, final UserProfile profile) {
        RankSelector selector = new RankSelector(profile);
        final String[] ranks = selector.selectRanks();

        EmbedHelper helper = new EmbedHelper(ctx.getGuild());
        return helper.standardBuilder()
                .setTitle("Profilanfrage von " + ctx.getUser().getName())
                .setDescription("Hier eine Übersicht über **" + profile.getUsername() + "'s** Account.")
                .setThumbnail(profile.getPortrait())
                .addField("🔻QUICKPLAY", "🔸Wins: `" + profile.getGames().getQuickplay().getWon() + "`\n" +
                        "🔸Played: `" + profile.getGames().getQuickplay().getPlayed() + "`", true)
                .addField("🔻COMPETITIVE", "🔸Wins: `" + profile.getGames().getCompetitive().getWon() + "`\n" +
                        "🔸Lost: `" + profile.getGames().getCompetitive().getLost() + "`\n" +
                        "🔸Draw: `" + profile.getGames().getCompetitive().getDraw() + "`\n" +
                        "🔸Played: `" + profile.getGames().getCompetitive().getPlayed() + "`\n" +
                        "🔸Winrate: `" + profile.getGames().getCompetitive().getWinRate() + "`", true)
                .addField("🔻PLAYTIME", "🔸Quickplay: `" + profile.getPlaytime().getQuickplay() + "`\n" +
                        "🔸Competitive: `" + profile.getPlaytime().getCompetitive() + "`", true)
                .addField("<:tank_icon:1085339432762232853> TANK", ranks[0], true)
                .addField("<:offense_icon:1085339430467948636> DPS", ranks[1], true)
                .addField("<:support_icon:1085339429012512788> SUPPORT", ranks[2], true);

    }
}
