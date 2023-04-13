package de.backxtar.commands.autoPost;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.backxtar.Constants;
import de.backxtar.OwBot;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class SetupInteraction {
    private final ButtonInteractionEvent ctx;
    private final String[] params;
    private String action;
    private long userId = 0L;

    public SetupInteraction(ButtonInteractionEvent ctx, final String[] params) {
        this.ctx = ctx;
        this.params = params;
    }

    public boolean filterAction() {
        this.action = params[0];
        this.userId = Long.parseLong(params[2]);
        return this.action != null && this.userId != 0L;
    }

    public boolean checkUser() {
        return this.userId == this.ctx.getUser().getIdLong() && (this.ctx.getGuild().getOwner().getIdLong() == this.userId || this.ctx.getUser().getIdLong() == Constants.botOwnerId);
    }

    public void runAction() {
        switch (action.toLowerCase()) {
            case "write" : overrideNewsChannel();
                break;
            default:
        }
    }

    private void overrideNewsChannel() {
        final String[] param = { "channel_id" };
        final Object[] values = { ctx.getGuild().getIdLong() };
        ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery(param, "news_channels", "guild_id = ?", values);
        if (rs == null) return;

        final String channelName;
        try {
            if (rs.next()) {
                TextChannel channel = ctx.getGuild().getTextChannelById(rs.getLong("channel_id"));
                if (channel != null) channelName = "#" + channel.getName();
                else channelName = "Channel Name nicht gefunden!";
            }
            else channelName = "Kein Channel festgelegt!";
        } catch (SQLException ex) {
            return;
        }

        TextInput chInput = TextInput.create("channel_id", "Channel ID", TextInputStyle.SHORT)
                .setPlaceholder(channelName)
                .setRequired(true)
                .setMinLength(12)
                .build();
        
        Modal modal = Modal.create("newschannel-modal", "News Channel festlegen oder aktualisieren")
                .addActionRow(chInput)
                .build();

        this.ctx.replyModal(modal)
                .queue();
    }
}
