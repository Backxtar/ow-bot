package de.backxtar.commands;

import de.backxtar.api.WebCrawler;
import de.backxtar.formatting.EmbedHelper;
import de.backxtar.handlers.CmdInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class UpdateCmd implements CmdInterface {

    @Override
    public void performCmd(SlashCommandInteractionEvent ctx) {
        final EmbedHelper helper = new EmbedHelper();
        final EmbedBuilder builder1 = helper.standardBuilder()
                .setDescription("<a:loading:1086206483609440286> *- Please wait...*");

        ctx.replyEmbeds(builder1.build())
                .queue();

        WebCrawler webCrawler = new WebCrawler("https://overwatch.blizzard.com/de-de/news/patch-notes/live");
        if (!webCrawler.crawlWeb()) return;

        final EmbedBuilder builder2 = helper.standardBuilder()
                .setAuthor("Overwatch 2 Patchnotes - " + webCrawler.getDate(), webCrawler.getUrl(), "http://i.epvpimg.com/ZA7Dfab.png")
                .setTitle(webCrawler.getPatchTitle(), "https://github.com/backxtar")
                .addField(webCrawler.getSectionTitle(), webCrawler.getSectionDesc() + ".. **[LESE MEHR :link:](" + webCrawler.getUrl() + ")**", false);

        ctx.getHook().editOriginalEmbeds(builder2.build()).queue();
    }
}
