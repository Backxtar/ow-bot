package de.backxtar.api;

import java.util.*;

public class Cache {
    private static HashMap<String, UserStats> statCache = new HashMap<>();

    public static void addStats(UserStats stats, final String tag) {
        stats.setTimestamp(System.currentTimeMillis());
        statCache.put(tag, stats);
    }

    public static UserStats getStatsByTag(final String tag) {
        return statCache.get(tag);
    }

    public static void deleteCache() {
        Set<String> tags = new HashSet<>();

        for (Map.Entry<String, UserStats> entry : statCache.entrySet()) {
            if ((System.currentTimeMillis() - (60000 * 5)) >= entry.getValue().getTimestamp())
                tags.add(entry.getKey());
        }
        statCache.keySet().removeAll(tags);
    }

    public static HashMap<String, UserStats> getStatCache() {
        return statCache;
    }
}
