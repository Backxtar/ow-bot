package de.backxtar.handlers;

import de.backxtar.OwBot;
import de.backxtar.formatting.EmbedHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ModalHandler {
    private ModalInteractionEvent ctx;

    public ModalHandler(ModalInteractionEvent ctx) {
        this.ctx = ctx;
    }

    public void handleModal() {
        if (this.ctx.getModalId().equals("battletag-modal")) handleBattletag();
        if (this.ctx.getModalId().equals("newschannel-modal")) handleChannelRegister();
    }

    private void handleBattletag() {
        final String tag = this.ctx.getValue("battletag").getAsString();

        final String[] param = { "battletag" };
        final Object[] values = { this.ctx.getUser().getIdLong() };
        ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery(param, "user_battletags", "user_id = ?", values);
        if (rs == null) return;
        boolean exist;

        try {
            exist = rs.next();
        } catch (SQLException ex) { return; }

        EmbedHelper helper = new EmbedHelper();
        EmbedBuilder builder = helper.standardBuilder();

        if (!tag.contains("#")) builder.setDescription("<:no:1085863681503547432> *Dein Tag `" + tag + "` muss ein `#` enthalten!*");
        else {
            builder.setDescription("<:yes:1085863679100198942> *Dein Tag `" + tag + "` wurde erfolgreich " + (exist ? "aktualisiert" : "gespeichert") + "!*");

            if (exist) {
                final String[] param2 = {"battletag"};
                final Object[] values2 = {tag, this.ctx.getUser().getIdLong()};
                OwBot.getOwBot().getSqlManager().updateQuery(param2, "user_battletags", "user_id = ?", values2);
            } else {
                final String[] param2 = {"user_id", "battletag"};
                final Object[] values2 = {this.ctx.getUser().getIdLong(), tag};
                OwBot.getOwBot().getSqlManager().insertQuery("user_battletags", param2, values2);
            }
        }
        this.ctx.replyEmbeds(builder.build())
                .queue();
    }

    private void handleChannelRegister() {
        EmbedHelper helper = new EmbedHelper();
        EmbedBuilder builder = helper.standardBuilder();
        final String handle = this.ctx.getValue("channel_id").getAsString();
        final long channelId;

        try {
            channelId = Long.parseLong(handle);
        } catch (NumberFormatException nfe) { 
            builder.setDescription("<:no:1085863681503547432> `" + handle + "` ist keine Zahlenfolge!");
            this.ctx.replyEmbeds(builder.build())
                    .queue();
            return; 
        }
        final Guild guild = this.ctx.getGuild();
        final TextChannel channel = guild.getTextChannelById(channelId);

        if (channel == null) {
            builder.setDescription("<:no:1085863681503547432> `" + handle + "` ist keine Channel ID!");
            this.ctx.replyEmbeds(builder.build())
                    .queue();
            return;
        }

        final String[] param = { "channel_id" };
        final Object[] values = { guild.getIdLong() };
        ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery(param, "news_channels", "guild_id = ?", values);
        if (rs == null) return;

        try {
            if (!rs.next()) {
                final String[] param2 = { "guild_id", "channel_id" };
                final Object[] values2 = { guild.getIdLong(), channelId };
                OwBot.getOwBot().getSqlManager().insertQuery("news_channels", param2, values2);
            } else {
                final String[] param2 = { "channel_id" };
                final Object[] values2 = { channelId, guild.getIdLong() };
                OwBot.getOwBot().getSqlManager().updateQuery(param2, "news_channels", "guild_id = ?", values2);
            }
            builder.setDescription("<:yes:1085863679100198942> " + channel.getAsMention() + " wurde als `News-Channel` festgelegt!");
            this.ctx.replyEmbeds(builder.build())
                    .queue();
        } catch (SQLException sql) {
            builder.setDescription("<:no:1085863681503547432> Da ist wohl etwas schief gelaufen!");
            this.ctx.replyEmbeds(builder.build())
                    .queue();
        }
    }

    public ModalInteractionEvent getCtx() {
        return this.ctx;
    }
}
