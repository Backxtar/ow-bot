package de.backxtar;

import de.backxtar.handlers.CmdRegister;
import de.backxtar.handlers.EventDistributor;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OwBot {
    public static void main(String[] args) {
        new OwBot();
    }

    /**
     * Global variables
     */
    private static OwBot OwBot;
    private final Logger logger;
    private ShardManager shardManager;
    private CmdRegister cmdRegister;

    /**
     * OW2 Constructor
     */
    public OwBot() {
        OwBot = this;
        logger = LoggerFactory.getLogger(OwBot.class);

        initHandlers();
    }

    /**
     * Init function with error catching
     */
    private void initHandlers() {
        try {
            this.shardManager = initJDA();
            this.logger.info("JDA initialisiert!");
            this.cmdRegister = new CmdRegister();
            this.logger.info("Commands registriert!");
        } catch (IllegalArgumentException iae) {
            this.logger.error("JDA konnte nicht initialisiert werden!");
        }
    }

    /**
     * Init JDA connection
     * @return ShardManager
     * @throws IllegalArgumentException if something fails
     */
    private ShardManager initJDA() throws IllegalArgumentException {
        final DefaultShardManagerBuilder smb = DefaultShardManagerBuilder.create(
                        /* Intents */
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.SCHEDULED_EVENTS)

                /* CONFIG */
                .setToken("MTA4NTIxNDg3MjI3NjU2NjExOA.GSn5TD.R49tfhIc735uJX-JzEnZeNIF0pCVu3A_gF2ong")
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("Overwatch 2"))
                .setAutoReconnect(true)
                .setShardsTotal(-1)

                .addEventListeners(new EventDistributor());

        return smb.build();
    }

    /**
     * Get bot instance
     * @return OwBot as object
     */
    public static OwBot getOwBot() {
        return OwBot;
    }

    /**
     * Get ShardManager
     * @return ShardManager as object
     */
    public ShardManager getShardManager() {
        return shardManager;
    }

    /**
     * Get CommandRegister
     * @return CommandRegister as object
     */
    public CmdRegister getCmdRegister() {
        return cmdRegister;
    }
}