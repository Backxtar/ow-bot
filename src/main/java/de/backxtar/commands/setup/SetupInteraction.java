package de.backxtar.commands.setup;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.backxtar.Constants;
import de.backxtar.OwBot;
import de.backxtar.formatting.EmbedHelper;
import net.dv8tion.jda.api.EmbedBuilder;
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
        this.action = this.params[0];
        this.userId = Long.parseLong(this.params[2]);
        return this.action != null && this.userId != 0L;
    }

    public boolean checkUser() {
        return this.userId == this.ctx.getUser().getIdLong() && (this.ctx.getGuild().getOwner().getIdLong() == this.userId || this.ctx.getUser().getIdLong() == Constants.botOwnerId);
    }

    public void runAction() {
        switch (action.toLowerCase()) {
            case "write" : overrideNewsChannel();
                break;
            case "activate" : toggleNews(1);
                break;
            case "deactivate" : toggleNews(0);
                break;
            default: this.ctx.deferEdit().queue();
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

    private void toggleNews(int active) {
        EmbedHelper helper = new EmbedHelper();

        final String[] param = { "active" };
        final Object[] values = { active, this.ctx.getGuild().getIdLong() };
        OwBot.getOwBot().getSqlManager().updateQuery(param, "news_channels", "guild_id = ?", values);

        final String desc = active == 1 ? "`aktiviert`" : "`deaktiviert`";
        EmbedBuilder builder = helper.standardBuilder()
                .setDescription("<:yes:1085863679100198942> Die **automatischen Update-News** wurden " + desc + "!");

        this.ctx.editButton(this.ctx.getButton().asDisabled())
                .flatMap(done -> this.ctx.getHook().getInteraction().getMessageChannel().sendMessageEmbeds(builder.build()))
                .queue();
    }
}
