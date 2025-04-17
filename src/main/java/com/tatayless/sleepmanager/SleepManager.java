package com.tatayless.sleepmanager;

import com.tatayless.sleepmanager.commands.CommandManager;
import com.tatayless.sleepmanager.config.ConfigManager;
import com.tatayless.sleepmanager.lang.LanguageManager;
import com.tatayless.sleepmanager.listeners.PlayerBedEventListener;
import com.tatayless.sleepmanager.vote.VoteManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SleepManager extends JavaPlugin {
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private VoteManager voteManager;

    @SuppressWarnings("unused")
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        // Initialize config and language systems
        this.configManager = new ConfigManager(this);
        this.languageManager = new LanguageManager(this);

        // Initialize vote manager
        this.voteManager = new VoteManager(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerBedEventListener(this), this);

        // Register commands
        this.commandManager = new CommandManager(this);

        getLogger().info("SleepManager has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SleepManager has been disabled.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }
}
