package de.backxtar.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<String> tags = new ArrayList<>();

        for (Map.Entry<String, UserStats> entry : statCache.entrySet()) {
            if ((System.currentTimeMillis() - 300000) >= entry.getValue().getTimestamp())
                tags.add(entry.getKey());
        }
        tags.forEach(tag -> statCache.remove(tag));
    }
}
