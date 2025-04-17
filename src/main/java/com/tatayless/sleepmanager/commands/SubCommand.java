package com.tatayless.sleepmanager.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {
    public abstract String getName();

    public abstract String getDescription();

    public abstract String getPermission();

    public abstract boolean execute(CommandSender sender, String[] args);

    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
