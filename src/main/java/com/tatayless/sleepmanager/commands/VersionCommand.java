package com.tatayless.sleepmanager.commands;

import com.tatayless.sleepmanager.SleepManager;
import org.bukkit.command.CommandSender;

public class VersionCommand extends SubCommand {
    private final SleepManager plugin;

    public VersionCommand(SleepManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Shows the plugin version";
    }

    @Override
    public String getPermission() {
        return "sleepmanager.version";
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(plugin.getLanguageManager().getComponent("commands.version",
                "version", plugin.getDescription().getVersion()));
        return true;
    }
}
