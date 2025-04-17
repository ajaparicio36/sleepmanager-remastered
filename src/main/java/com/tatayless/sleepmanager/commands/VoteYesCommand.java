package com.tatayless.sleepmanager.commands;

import com.tatayless.sleepmanager.SleepManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteYesCommand extends SubCommand {
    private final SleepManager plugin;

    public VoteYesCommand(SleepManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "yes";
    }

    @Override
    public String getDescription() {
        return "Vote yes for the current sleep vote";
    }

    @Override
    public String getPermission() {
        return "sleepmanager.vote";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLanguageManager().getComponent("commands.player_only"));
            return true;
        }

        plugin.getVoteManager().castVote(player, true);
        return true;
    }
}
