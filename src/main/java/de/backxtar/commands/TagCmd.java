package de.backxtar.commands;

import de.backxtar.OwBot;
import de.backxtar.handlers.CmdInterface;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TagCmd implements CmdInterface {

    @Override
    public void performCmd(SlashCommandInteractionEvent ctx) {
        final String[] param = { "battletag" };
        final Object[] values = { ctx.getUser().getIdLong() };
        ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery(param, "user_battletags", "user_id = ?", values);
        if (rs == null) return;

        final String tag;
        try {
            if (rs.next()) tag = rs.getString("battletag");
            else tag = "DeinBattletag#1234";
        } catch (SQLException ex) {
            return;
        }

        TextInput tagInput = TextInput.create("battletag", "BattleTag", TextInputStyle.SHORT)
                .setPlaceholder(tag)
                .setRequired(true)
                .setMinLength(4)
                .build();

        Modal modal = Modal.create("battletag-modal", "BattleTag speichern oder aktualisieren")
                .addActionRow(tagInput)
                .build();

        ctx.replyModal(modal)
                .queue();
    }
}
