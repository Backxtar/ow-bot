package de.backxtar.threads;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import de.backxtar.OwBot;
import de.backxtar.api.WebCrawler;
import de.backxtar.formatting.EmbedHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class UpdateCheck {

    public static void checkPatchNotes() {
        WebCrawler webCrawler = new WebCrawler("https://overwatch.blizzard.com/de-de/news/patch-notes/live");
        if (!webCrawler.crawlWeb()) return;
        final String date = webCrawler.getDate();
        String sqlDate;

        final String[] param = { "value" };
        final Object[] values = { "patchDate" };
        ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery(param, "hashmap", "kei = ?", values);
        if (rs == null) return;

        try {
            if (!rs.next()) {
                sendNotification(webCrawler);
                insert(date);
                return;
            }
            sqlDate = rs.getString("value");
        } catch (SQLException ex) { return; }

        if (Objects.equals(sqlDate, date)) return;
        sendNotification(webCrawler);
        update(date);
    }

    private static void insert(final String date) {
        final String[] param = { "kei", "value" };
        final Object[] values = { "patchDate", date };
        OwBot.getOwBot().getSqlManager().insertQuery("hashmap", param, values);
    }

    private static void update(final String date) {
        final String[] param = { "value" };
        final Object[] values = { date, "patchDate" };
        OwBot.getOwBot().getSqlManager().updateQuery(param, "hashmap", "kei = ?", values);
    }

    private static void sendNotification(WebCrawler webCrawler) {
        ResultSet rs = OwBot.getOwBot().getSqlManager().selectQuery("news_channels");
        if (rs == null) return;

        try {
            while (rs.next()) {
                long guild_id = rs.getLong("guild_id");
                long channel_id = rs.getLong("channel_id");
                boolean active = rs.getInt("active") == 1;

                if (!active) continue;
                Guild guild = OwBot.getOwBot().getShardManager().getGuildById(guild_id);
                if (guild == null) continue;
                TextChannel channel = guild.getTextChannelById(channel_id);
                if (channel == null) continue;

                final EmbedHelper helper = new EmbedHelper();
                final EmbedBuilder builder = helper.standardBuilder()
                        .setAuthor("Overwatch 2 Patchnotes - " + webCrawler.getDate(), webCrawler.getUrl(), "http://i.epvpimg.com/ZA7Dfab.png")
                        .setTitle(webCrawler.getPatchTitle(), "https://github.com/backxtar")
                        .addField(webCrawler.getSectionTitle(), webCrawler.getSectionDesc() + ".. **[LESE MEHR :link:](" + webCrawler.getUrl() + ")**", false);

                channel.sendMessageEmbeds(builder.build())
                        .queue();
            }
        } catch (SQLException sql) {
            return;
        }
    }
}
