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
        List<OptionMapping> options = ctx.getInteraction().getOptionsByType(OptionType.STRING);
        final Type return_type = new TypeToken<UserProfile>() {}.getType();
        EmbedHelper helper = new EmbedHelper();
        final Gson gson = new Gson();
        UserProfile profile;

        if (options.size() > 0) {
            String tag = options.get(0).getAsString();
            tag = tag.replace("#", "-");

            try {
                final JSONReader reader = new JSONReader("https://owapi.io/profile/pc/eu/" + tag);
                profile = gson.fromJson(reader.getJSON(), return_type);
            } catch (IOException ioe) { return; }
        } else {
            final String[] param = { "battletag" };
            final Object[] values = { ctx.getUser().getIdLong() };
            ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery(param, "user_battletags", "user_id = ?", values);
            if (rs == null) return;

            try {
                EmbedBuilder builder = helper.standardBuilder();
                if (!rs.next()) {
                    builder.setDescription("<:no:1085863681503547432> *Kein Tag hinterlegt!*");
                    ctx.replyEmbeds(builder.build())
                            .queue();
                    return;
                }
                String tag = rs.getString("battletag");
                tag = tag.replace("#", "-");

                final JSONReader reader = new JSONReader("https://owapi.io/profile/pc/eu/" + tag);
                profile = gson.fromJson(reader.getJSON(), return_type);
            } catch (SQLException | IOException ex) { return; }
        }

        if (profile == null || Objects.equals(profile.getMessage(), "Error: Profile not found")) {
            EmbedBuilder builder = helper.standardBuilder()
                    .setDescription("<:no:1085863681503547432> *Kein Profil gefunden!*");
            ctx.replyEmbeds(builder.build())
                    .queue();
            return;
        }

        EmbedBuilder builder = createBaseEmbed(ctx, profile);
        ctx.replyEmbeds(builder.build())
                .setEphemeral(false)
                .queue();
    }

    private EmbedBuilder createBaseEmbed(SlashCommandInteractionEvent ctx,
                                         final UserProfile profile) {
        RankSelector selector = new RankSelector(profile);
        final String[] ranks = selector.selectRanks();

        EmbedHelper helper = new EmbedHelper(ctx.getGuild());
        return helper.standardBuilder()
                .setTitle("Statanfrage von " + ctx.getUser().getName())
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
