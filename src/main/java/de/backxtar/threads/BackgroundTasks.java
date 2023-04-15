package de.backxtar.threads;

import de.backxtar.OwBot;
import de.backxtar.api.Cache;

import java.util.ArrayList;
import java.util.List;

public class BackgroundTasks {
    private final List<Thread> threads;

    public BackgroundTasks() {
        this.threads = new ArrayList<>();
        this.threads.add(loop());
    }

    public void runThreads() {
        this.threads.forEach(Thread::start);
    }

    private Thread loop() {
        return new Thread(() -> {
            long fifteenMinutes = System.currentTimeMillis();
            long fiveMinutes = System.currentTimeMillis();
            long oneMinute = System.currentTimeMillis();

            while (OwBot.getOwBot().getShardManager() != null) {
                long current = System.currentTimeMillis();

                if (current >= fifteenMinutes + (1000 * 60 * 15)) {
                    fifteenMinutes = System.currentTimeMillis();
                    UpdateCheck.checkPatchNotes();
                }
                if (current >= fiveMinutes + (1000 * 60 * 5)) {
                    fiveMinutes = System.currentTimeMillis();
                    System.gc();
                }
                if (current >= oneMinute + (1000 * 60)) {
                    oneMinute = System.currentTimeMillis();
                    Cache.deleteCache();
                }
            }
        });
    }
}
