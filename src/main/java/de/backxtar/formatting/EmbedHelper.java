package de.backxtar.formatting;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

import java.time.OffsetDateTime;

public class EmbedHelper {
    private Guild guild;

    public EmbedHelper(final Guild guild) {
        this.guild = guild;
    }

    public EmbedHelper() {

    }

    private EmbedBuilder baseEmbed() {
        return new EmbedBuilder()
                .setFooter("Made by Backxtar", "https://github.com/backxtar.png")
                .setTimestamp(OffsetDateTime.now());
    }

    public EmbedBuilder standardBuilder() {
        EmbedBuilder builder = baseEmbed();
        if (this.guild != null) builder.setAuthor(this.guild.getName(), "https://github.com/backxtar", this.guild.getIconUrl());
        return builder;
    }
}
