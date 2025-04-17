package com.tatayless.sleepmanager.commands;

import com.tatayless.sleepmanager.SleepManager;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager implements TabExecutor {
    private final SleepManager plugin;
    private final List<SubCommand> subCommands = new ArrayList<>();

    public CommandManager(SleepManager plugin) {
        this.plugin = plugin;

        // Register subcommands
        subCommands.add(new VersionCommand(plugin));
        subCommands.add(new RevoteCommand(plugin));
        subCommands.add(new VoteYesCommand(plugin));
        subCommands.add(new VoteNoCommand(plugin));
        subCommands.add(new ToggleCommand(plugin));

        // Register command executor
        PluginCommand command = plugin.getCommand("sleepmanager");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String label,
            String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        // Look for matching subcommand
        for (SubCommand subCmd : subCommands) {
            if (subCmd.getName().equalsIgnoreCase(subcommand)) {
                // Check permissions
                if (subCmd.getPermission() != null && !sender.hasPermission(subCmd.getPermission()) && !sender.isOp()) {
                    sender.sendMessage(plugin.getLanguageManager().getComponent("commands.no_permission"));
                    return true;
                }

                // Execute command
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subCmd.execute(sender, subArgs);
            }
        }

        // No matching subcommand found
        sender.sendMessage(plugin.getLanguageManager().getComponent("commands.unknown_command"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias,
            String[] args) {
        if (args.length == 1) {
            // Return subcommands that the sender has permission for
            return subCommands.stream()
                    .filter(subCmd -> subCmd.getPermission() == null ||
                            sender.hasPermission(subCmd.getPermission()) ||
                            sender.isOp())
                    .map(SubCommand::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length > 1) {
            // Find the subcommand and delegate tab completion to it
            String subcommand = args[0].toLowerCase();
            for (SubCommand subCmd : subCommands) {
                if (subCmd.getName().equalsIgnoreCase(subcommand)) {
                    return subCmd.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
                }
            }
        }

        return new ArrayList<>();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getLanguageManager().getComponent("commands.help_header"));

        // Only show commands the sender has permission for
        for (SubCommand subCmd : subCommands) {
            if (subCmd.getPermission() == null || sender.hasPermission(subCmd.getPermission()) || sender.isOp()) {
                sender.sendMessage(plugin.getLanguageManager().getComponent("commands.help_format",
                        "command", "sleepmanager " + subCmd.getName(),
                        "description", subCmd.getDescription()));
            }
        }

        sender.sendMessage(plugin.getLanguageManager().getComponent("commands.help_footer"));
    }
}
