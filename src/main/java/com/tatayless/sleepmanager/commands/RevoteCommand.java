package com.tatayless.sleepmanager.commands;

import com.tatayless.sleepmanager.SleepManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RevoteCommand extends SubCommand {
    private final SleepManager plugin;

    public RevoteCommand(SleepManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "revote";
    }

    @Override
    public String getDescription() {
        return "Starts a new vote if the cooldown has passed";
    }

    @Override
    public String getPermission() {
        return "sleepmanager.revote";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getLanguageManager().getComponent("commands.player_only"));
            return true;
        }

        plugin.getVoteManager().revote(player);
        return true;
    }
}
