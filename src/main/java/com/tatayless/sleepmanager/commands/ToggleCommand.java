package com.tatayless.sleepmanager.commands;

import com.tatayless.sleepmanager.SleepManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ToggleCommand extends SubCommand {
    private final SleepManager plugin;

    public ToggleCommand(SleepManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "toggle";
    }

    @Override
    public String getDescription() {
        return "Toggle sleep voting for specific world or all worlds";
    }

    @Override
    public String getPermission() {
        return "sleepmanager.toggle";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // Toggle for specific world
        if (args.length > 0) {
            String worldName = args[0];
            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                sender.sendMessage(plugin.getLanguageManager().getComponent("commands.world_not_found",
                        "world", worldName));
                return true;
            }

            boolean currentState = plugin.getConfigManager().isWorldEnabled(world);
            boolean newState = !currentState;
            plugin.getConfigManager().toggleWorld(world, newState);

            String messageKey = newState ? "commands.world_enabled" : "commands.world_disabled";
            sender.sendMessage(plugin.getLanguageManager().getComponent(messageKey,
                    "world", world.getName()));
        } else {
            // Toggle for all worlds
            boolean anyEnabled = Bukkit.getWorlds().stream()
                    .anyMatch(world -> plugin.getConfigManager().isWorldEnabled(world));

            // If any are enabled, disable all; otherwise enable all
            boolean newState = !anyEnabled;
            plugin.getConfigManager().toggleAllWorlds(newState);

            String messageKey = newState ? "commands.all_worlds_enabled" : "commands.all_worlds_disabled";
            sender.sendMessage(plugin.getLanguageManager().getComponent(messageKey));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
