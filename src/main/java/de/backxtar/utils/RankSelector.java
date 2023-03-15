package de.backxtar.utils;

import de.backxtar.api.UserProfile;
import java.util.Objects;

public class RankSelector {
    private final UserProfile profile;
    private String tank_rank;
    private String offense_rank;
    private String support_rank;

    public RankSelector(final UserProfile profile) {
        this.profile = profile;
    }

    public String[] selectRanks() {
        String[] ranks = new String[3];
        try {
            ranks[0] = getEmoji(this.profile.getCompetitive().getTank().getRank());
        } catch (Exception ex) {
            ranks[0] = "*No Rank!*";
        }

        try {
            ranks[1] = getEmoji(this.profile.getCompetitive().getOffense().getRank());
        } catch (Exception ex) {
            ranks[1] = "*No Rank!*";
        }

        try {
            ranks[2] = getEmoji(this.profile.getCompetitive().getSupport().getRank());
        } catch (Exception ex) {
            ranks[2] = "*No Rank!*";
        }

        return ranks;
    }

    private String getEmoji(final String rank) {
        String utf8_emoji;

        switch (rank.toLowerCase()) {
            case "bronze 1" -> utf8_emoji = "<:bronze_1:1085330189917114499>";
            case "bronze 2" -> utf8_emoji = "<:bronze_2:1085330188138725456>";
            case "bronze 3" -> utf8_emoji = "<:bronze_3:1085330185508880575>";
            case "bronze 4" -> utf8_emoji = "<:bronze_4:1085330183537578104>";
            case "bronze 5" -> utf8_emoji = "<:bronze_5:1085330179980791838>";

            case "silver 1" -> utf8_emoji = "<:silver_1:1085329820633796680>";
            case "silver 2" -> utf8_emoji = "<:silver_2:1085329814656925826>";
            case "silver 3" -> utf8_emoji = "<:silver_3:1085329817651654736>";
            case "silver 4" -> utf8_emoji = "<:silver_4:1085329812576555150>";
            case "silver 5" -> utf8_emoji = "<:silver_5:1085329808663265301>";

            case "gold 1" -> utf8_emoji = "<:gold_1:1085329222291161231>";
            case "gold 2" -> utf8_emoji = "<:gold_2:1085329219455832065>";
            case "gold 3" -> utf8_emoji = "<:gold_3:1085329215999721472>";
            case "gold 4" -> utf8_emoji = "<:gold_4:1085329213881594087>";
            case "gold 5" -> utf8_emoji = "<:gold_5:1085329210731675688>";

            case "platinum 1" -> utf8_emoji = "<:platinum_1:1085328682433908826>";
            case "platinum 2" -> utf8_emoji = "<:platinum_2:1085328678906494996>";
            case "platinum 3" -> utf8_emoji = "<:platinum_3:1085328691401338922>";
            case "platinum 4" -> utf8_emoji = "<:platinum_4:1085328688117207091>";
            case "platinum 5" -> utf8_emoji = "<:platinum_5:1085328684463964161>";

            case "diamond 1" -> utf8_emoji = "<:diamond_1:1085328187527012432>";
            case "diamond 2" -> utf8_emoji = "<:diamond_2:1085328185375342632>";
            case "diamond 3" -> utf8_emoji = "<:diamond_3:1085328181940195408>";
            case "diamond 4" -> utf8_emoji = "<:diamond_4:1085328179100659932>";
            case "diamond 5" -> utf8_emoji = "<:diamond_5:1085328177099968622>";

            //TODO: ADD MORE RANKS

            default -> utf8_emoji = "*Keinen Rang gefunden!*";
        }
        utf8_emoji = utf8_emoji + " `" + rank + "`";
        return utf8_emoji;
    }

    public UserProfile getProfile() {
        return this.profile;
    }

    public String getTank_rank() {
        return this.tank_rank;
    }

    public String getOffense_rank() {
        return this.offense_rank;
    }

    public String getSupport_rank() {
        return this.support_rank;
    }
}
