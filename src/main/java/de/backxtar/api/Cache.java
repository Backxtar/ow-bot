package de.backxtar.api;

import java.util.*;

public class Cache {
    private static HashMap<String, UserStats> statCache = new HashMap<>();
    private static HashMap<String, UserProfile> profCache = new HashMap<>();

    public static void addStats(UserStats stats, final String tag) {
        stats.setTimestamp(System.currentTimeMillis());
        statCache.put(tag, stats);
    }

    public static void addStats(UserProfile prof, final String tag) {
        prof.setTimestamp(System.currentTimeMillis());
        profCache.put(tag, prof);
    }

    public static UserStats getStatsByTag(final String tag) {
        return statCache.get(tag);
    }

    public static UserProfile getProfileByTag(final String tag) {
        return profCache.get(tag);
    }

    public static void deleteCache() {
        if (!statCache.isEmpty()) {
            Set<String> statTags = new HashSet<>();
            for (Map.Entry<String, UserStats> entry : statCache.entrySet()) {
                if ((System.currentTimeMillis() - (60000 * 5)) >= entry.getValue().getTimestamp())
                    statTags.add(entry.getKey());
            }
            statCache.keySet().removeAll(statTags);
        }

        if (!profCache.isEmpty()) {
            Set<String> profTags = new HashSet<>();
            for (Map.Entry<String, UserProfile> entry : profCache.entrySet()) {
                if ((System.currentTimeMillis() - (60000 * 5)) >= entry.getValue().getTimestamp())
                    profTags.add(entry.getKey());
            }
            profCache.keySet().removeAll(profTags);
        }
    }

    public static HashMap<String, UserStats> getStatCache() {
        return statCache;
    }

    public static HashMap<String, UserProfile> getProfCache() {
        return profCache;
    }
}
