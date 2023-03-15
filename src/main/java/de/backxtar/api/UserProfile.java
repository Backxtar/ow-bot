package de.backxtar.api;

public class UserProfile {
    private String username;
    private String portrait;
    private String endorsement;
    private Games games;
    private Playtime playtime;
    private Competitive competitive;
    private String message;

    public String getUsername() {
        return username;
    }

    public String getPortrait() {
        return portrait;
    }

    public String getEndorsement() {
        return endorsement;
    }

    public Games getGames() {
        return games;
    }

    public Playtime getPlaytime() {
        return playtime;
    }

    public Competitive getCompetitive() {
        return competitive;
    }

    public String getMessage() {
        return message;
    }

    public class Games {
        private Quickplay quickplay;
        private Competitive competitive;

        public Quickplay getQuickplay() {
            return quickplay;
        }

        public Competitive getCompetitive() {
            return competitive;
        }
    }

    public class Quickplay {
        private int won;
        private int played;

        public int getWon() {
            return won;
        }

        public int getPlayed() {
            return played;
        }
    }

    public class Competitive {
        private int won;
        private int lost;
        private int draw;
        private int played;
        public String getWinRate() {
            String number = String.format("%.2f", (double) won / played * 100);
            return number + "%";
        }
        private Tank tank;
        private Offense offense;
        private Support support;

        public int getWon() {
            return won;
        }

        public int getLost() {
            return lost;
        }

        public int getDraw() {
            return draw;
        }

        public int getPlayed() {
            return played;
        }

        public Tank getTank() {
            return tank;
        }

        public Offense getOffense() {
            return offense;
        }

        public Support getSupport() {
            return support;
        }
    }

    public class Playtime {
        private String quickplay;
        private String competitive;

        public String getQuickplay() {
            return quickplay;
        }

        public String getCompetitive() {
            return competitive;
        }
    }

    public class Tank {
        private String rank;
        private String icon;

        public String getRank() {
            return rank;
        }

        public String getIcon() {
            return icon;
        }
    }

    public class Offense {
        private String rank;
        private String icon;

        public String getRank() {
            return rank;
        }

        public String getIcon() {
            return icon;
        }
    }

    public class Support {
        private String rank;
        private String icon;

        public String getRank() {
            return rank;
        }

        public String getIcon() {
            return icon;
        }
    }
}