package de.backxtar.api;

import com.google.gson.annotations.SerializedName;

public class UserStats {
    private String tag;
    private String username;
    private String portrait;
    private String endorsement;
    @SerializedName("private")
    private boolean isPrivate;
    private Stats stats;
    private String message;
    private long timestamp;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getUsername() {
        return username;
    }

    public String getPortrait() {
        return portrait;
    }

    public String getEndorsement() {
        return endorsement;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public Stats getStats() {
        return stats;
    }

    public String getMessage() {
        return message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public class Stats {
        private TopHeroes top_heroes;
        private Combat combat;
        private Assists assists;
        private Best best;

        public TopHeroes getTop_heroes() {
            return top_heroes;
        }

        public Combat getCombat() {
            return combat;
        }

        public Assists getAssists() {
            return assists;
        }

        public Best getBest() {
            return best;
        }
    }

    public class TopHeroes {
        private Quickplay quickplay;
        private Competitive competitive;

        public Quickplay getQuickplay() {
            return quickplay;
        }

        public Competitive getCompetitive() {
            return competitive;
        }
    }

    public class Combat {
        private CombatOverview[] quickplay;
        private CombatOverview[] competitive;

        public CombatOverview[] getQuickplay() {
            return quickplay;
        }

        public CombatOverview[] getCompetitive() {
            return competitive;
        }
    }

    public class Assists {
        private CombatOverview[] quickplay;
        private CombatOverview[] competitive;

        public CombatOverview[] getQuickplay() {
            return quickplay;
        }

        public CombatOverview[] getCompetitive() {
            return competitive;
        }
    }

    public class Best {
        private CombatOverview[] quickplay;
        private CombatOverview[] competitive;

        public CombatOverview[] getQuickplay() {
            return quickplay;
        }

        public CombatOverview[] getCompetitive() {
            return competitive;
        }
    }

    public class CombatOverview {
        private String title;
        private String value;

        public String getTitle() {
            return title;
        }

        public String getValue() {
            return value;
        }
    }

    public class Quickplay {
        private Played[] played;
        private GamesWon[] games_won;
        private WeaponAccuracy[] weapon_accuracy;
        private EliminationsPerLife[] eliminations_per_life;
        private MultikillBest[] multikill_best;

        public Played[] getPlayed() {
            return played;
        }

        public GamesWon[] getGames_won() {
            return games_won;
        }

        public WeaponAccuracy[] getWeapon_accuracy() {
            return weapon_accuracy;
        }

        public EliminationsPerLife[] getEliminations_per_life() {
            return eliminations_per_life;
        }

        public MultikillBest[] getMultikill_best() {
            return multikill_best;
        }
    }

    public class Competitive {
        private Played[] played;
        private GamesWon[] games_won;
        private WeaponAccuracy[] weapon_accuracy;
        private EliminationsPerLife[] eliminations_per_life;
        private MultikillBest[] multikill_best;

        public Played[] getPlayed() {
            return played;
        }

        public GamesWon[] getGames_won() {
            return games_won;
        }

        public WeaponAccuracy[] getWeapon_accuracy() {
            return weapon_accuracy;
        }

        public EliminationsPerLife[] getEliminations_per_life() {
            return eliminations_per_life;
        }

        public MultikillBest[] getMultikill_best() {
            return multikill_best;
        }
    }

    public class Played {
        private String hero;
        private String img;
        private String played;

        public String getHero() {
            return hero;
        }

        public String getImg() {
            return img;
        }

        public String getPlayed() {
            return played;
        }
    }

    public class GamesWon {
        private String hero;
        private String img;
        private String games_won;

        public String getHero() {
            return hero;
        }

        public String getImg() {
            return img;
        }

        public int getGames_won() {
            try {
                return Integer.parseInt(games_won);
            } catch (NumberFormatException nfex) {
                return 0;
            }
        }
    }

    public class WeaponAccuracy {
        private String hero;
        private String img;
        private String weapon_accuracy;

        public String getHero() {
            return hero;
        }

        public String getImg() {
            return img;
        }

        public String getWeapon_accuracy() {
            return weapon_accuracy;
        }
    }

    public class EliminationsPerLife {
        private String hero;
        private String img;
        private String eliminations_per_life;

        public String getHero() {
            return hero;
        }

        public String getImg() {
            return img;
        }

        public double getEliminations_per_life() {
            try {
                return Double.parseDouble(eliminations_per_life);
            } catch (NumberFormatException nfex) {
                return 0.0;
            }
        }
    }

    public class MultikillBest {
        private String hero;
        private String img;
        private String multikill_best;

        public String getHero() {
            return hero;
        }

        public String getImg() {
            return img;
        }

        public int getMultikill_best() {
            try {
                return Integer.parseInt(multikill_best);
            } catch (NumberFormatException nfex) {
                return 0;
            }
        }
    }
}
