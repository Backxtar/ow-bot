package de.backxtar.commands;

import de.backxtar.api.Cache;
import de.backxtar.api.UserProfile;
import de.backxtar.formatting.EmbedHelper;
import de.backxtar.handlers.CmdInterface;
import de.backxtar.utils.CmdUtils;
import de.backxtar.utils.RankSelector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ProfileCmd implements CmdInterface {

    @Override
    public void performCmd(SlashCommandInteractionEvent ctx) {
        final EmbedHelper helper = new EmbedHelper();
        final EmbedBuilder builder = helper.standardBuilder()
                .setDescription("<a:loading:1086206483609440286> *- Please wait...*");

        ctx.replyEmbeds(builder.build())
                .queue();

        CmdUtils cmdUtils = new CmdUtils(ctx);
        final String tag = cmdUtils.getTag();
        if (tag == null) return;

        UserProfile profile;
        if (Cache.getProfileByTag(tag) != null) profile = Cache.getProfileByTag(tag);
        else {
            profile = cmdUtils.getProfileUser(tag);
            if (profile != null) Cache.addStats(profile, tag);
            else return;
        }

        final EmbedBuilder builder2 = createInfoEmbed(ctx, profile);
        ctx.getHook().editOriginalEmbeds(builder2.build())
                .queue();
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
