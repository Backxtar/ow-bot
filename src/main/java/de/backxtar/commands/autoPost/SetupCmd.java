package de.backxtar.commands.autoPost;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.backxtar.OwBot;
import de.backxtar.formatting.EmbedHelper;
import de.backxtar.handlers.CmdInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class SetupCmd implements CmdInterface {

    @Override
    public void performCmd(SlashCommandInteractionEvent ctx) {
        final String[] param = { "active", "channel_id" };
        final Object[] values = { ctx.getGuild().getIdLong() };
        ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery(param, "news_channels", "guild_id = ?", values);
        if (rs == null) return;

        EmbedHelper helper = new EmbedHelper(ctx.getGuild());
        EmbedBuilder builder = helper.standardBuilder();
        builder.setTitle(ctx.getJDA().getSelfUser().getName() + " Setup")
                .setThumbnail(ctx.getJDA().getSelfUser().getEffectiveAvatarUrl());
        int active;
        long channel_id = 0L;

        try {
            if (!rs.next()) {
                builder.setDescription("*Es ist noch kein `News-Channel` festgelegt. Du benötigst die Channel-ID für diesen Vorgang!*");
                active = -1;
            } else {
                active = rs.getInt("active");
                channel_id = rs.getLong("channel_id");
                TextChannel news = ctx.getGuild().getTextChannelById(channel_id);
                String channelName = news == null ? "Channel nicht gefunden!" : news.getAsMention();
                String status = (active == 1 ? "deaktivieren" : "aktivieren");
                String add = (active == 1 ? "Um keine **automatischen Patchnotes** mehr zu erhalten" : "Um **automatische Patchnotes** zu erhalten");

                builder.setDescription("*" + add + ", benutzte den Button `" + status + "`. Um den **News-Channel** neu festzulegen, benutze den Button `überschreiben`!*")
                        .addField("Aktueller Channel", channelName, false);
            }
        } catch (SQLException e) {
            builder.setDescription("<:no:1085863681503547432> Da ist wohl etwas schief gelaufen!");
                ctx.replyEmbeds(builder.build())
                        .queue();
                return;
        }

        Button override = Button.secondary("write:" + channel_id + ":" + ctx.getUser().getIdLong(), active == -1 ? "Channel festlegen" : "Channel überschreiben").withEmoji(Emoji.fromFormatted("💾"));

        if (active == -1) {
            ctx.replyEmbeds(builder.build())
                    .addActionRow(override)
                    .queue();
            return;
        }

        Button action = active == 0 ? Button.success("activate:" + channel_id + ":" + ctx.getUser().getIdLong(), "News aktivieren").withEmoji(Emoji.fromFormatted("<:yes:1085863679100198942>")) : 
        Button.danger("deactivate:" + channel_id + ":" + ctx.getUser().getIdLong(), "News deaktivieren").withEmoji(Emoji.fromFormatted("<:no:1085863681503547432>"));

        ctx.replyEmbeds(builder.build())
                .addActionRow(action, override)
                .queue();
    }
}
