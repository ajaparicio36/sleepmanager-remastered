package com.tatayless.sleepmanager;

import org.bukkit.plugin.java.JavaPlugin;

public class SleepManager extends JavaPlugin {
    public void onEnable() {
        // Code to run when the plugin is enabled
        getLogger().info("SleepManager has been enabled.");
    }

    public void onDisable() {
        // Code to run when the plugin is disabled
        getLogger().info("SleepManager has been disabled.");
    }
}
