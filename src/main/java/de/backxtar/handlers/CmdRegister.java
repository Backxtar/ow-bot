package de.backxtar.handlers;

import de.backxtar.OwBot;
import de.backxtar.commands.HelpCmd;
import de.backxtar.commands.ProfileCmd;
import de.backxtar.commands.StatsCmd;
import de.backxtar.commands.TagCmd;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CmdRegister {
    private final ConcurrentHashMap<String, CmdInterface> cmds;
    private final ConcurrentHashMap<String, String> cmdDesc;

    public CmdRegister() {
        this.cmds = new ConcurrentHashMap<>();
        this.cmdDesc = new ConcurrentHashMap<>();

        registerCmds();
        formatCmds();
    }

    private void registerCmds() {
        saveToHashmap("help", "Zeigt die Hilfe an.", new HelpCmd());
        saveToHashmap("profile", "Zeigt Infos über den Account an./%t%", new ProfileCmd());
        saveToHashmap("stats", "Zeigt Infos über die Account-Stats an./%t%", new StatsCmd());
        saveToHashmap("tag", "Speichert/aktualisiert dein Battletag.", new TagCmd());
    }

    private void saveToHashmap(
            final String name,
            final String desc,
            final CmdInterface cmd) {
        this.cmds.put(name, cmd);
        this.cmdDesc.put(name, desc);
    }

    private void formatCmds() {
        OwBot.getOwBot().getShardManager().getShards()
                .forEach(shard -> {
                    CommandListUpdateAction action = shard.updateCommands();

                    for (String key : cmdDesc.keySet()) {
                        final String[] param = cmdDesc.get(key).split("/");

                        SlashCommandData data;
                        if (param.length > 1) {
                            data = Commands.slash(key, param[0]);
                            final String[] paramArray = param[1].split(",");

                            for (String value : paramArray) {
                                if (Objects.equals(value, "%t%"))
                                    data.addOption(OptionType.STRING,
                                            "gamertag",
                                            "ID des Spielers",
                                            false);
                            }
                        } else data = Commands.slash(key, cmdDesc.get(key));
                        data.setGuildOnly(true);
                        action.addCommands(data);
                    }
                    action.queue();
                });
    }

    public void executeCmd(SlashCommandInteractionEvent ctx) {
        final CmdInterface cmdInterface;

        if ((cmdInterface = this.cmds.get(ctx.getInteraction().getName().toLowerCase())) == null) return;
        cmdInterface.performCmd(ctx);
    }
}
