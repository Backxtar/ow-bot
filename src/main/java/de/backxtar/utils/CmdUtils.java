package de.backxtar.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.backxtar.OwBot;
import de.backxtar.api.JSONReader;
import de.backxtar.api.UserProfile;
import de.backxtar.api.UserStats;
import de.backxtar.formatting.EmbedHelper;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CmdUtils {
    private SlashCommandInteractionEvent ctx;

    public CmdUtils(SlashCommandInteractionEvent ctx) {
        this.ctx = ctx;
    }

    public String getTag() {
        List<OptionMapping> options = this.ctx.getInteraction().getOptionsByType(OptionType.STRING);
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

    public UserStats getStatsUser(final String tag) {
        final EmbedHelper helper = new EmbedHelper();
        final UserStats stats = getUserStats(tag);

        if (stats == null || Objects.equals(stats.getMessage(), "Error: Profile not found")) {
            this.ctx.getHook().editOriginalEmbeds(helper.standardBuilder().setDescription("<:no:1085863681503547432> *Kein Profil gefunden!*").build()).queue();
            return null;
        }

        if (stats.getIsPrivate()) {
            this.ctx.getHook().editOriginalEmbeds(helper.standardBuilder().setDescription("<:no:1085863681503547432> *Das Profil ist privat!*").build()).queue();
            return null;
        }
        return stats;
    }

    public UserProfile getProfileUser(final String tag) {
        final EmbedHelper helper = new EmbedHelper();
        final UserProfile profile = getUserProfile(tag);

        if (profile == null || Objects.equals(profile.getMessage(), "Error: Profile not found")) {
            this.ctx.getHook().editOriginalEmbeds(helper.standardBuilder().setDescription("<:no:1085863681503547432> *Kein Profil gefunden!*").build()).queue();
            return null;
        }

        if (profile.getIsPrivate()) {
            this.ctx.getHook().editOriginalEmbeds(helper.standardBuilder().setDescription("<:no:1085863681503547432> *Das Profil ist privat!*").build()).queue();
            return null;
        }
        return profile;
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

    public SlashCommandInteractionEvent getCtx() {
        return ctx;
    }
}
