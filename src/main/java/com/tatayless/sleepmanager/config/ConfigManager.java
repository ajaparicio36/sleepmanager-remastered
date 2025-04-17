package com.tatayless.sleepmanager.config;

import com.tatayless.sleepmanager.SleepManager;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfigManager {
    private final SleepManager plugin;
    private FileConfiguration config;
    private final Map<UUID, Boolean> worldToggles = new HashMap<>();

    public ConfigManager(SleepManager plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();

        // Load world toggles
        if (config.contains("worlds")) {
            for (String worldName : config.getConfigurationSection("worlds").getKeys(false)) {
                World world = plugin.getServer().getWorld(worldName);
                if (world != null) {
                    boolean enabled = config.getBoolean("worlds." + worldName + ".enabled", true);
                    worldToggles.put(world.getUID(), enabled);
                }
            }
        }
    }

    public void saveConfig() {
        // Save world toggles
        for (Map.Entry<UUID, Boolean> entry : worldToggles.entrySet()) {
            World world = plugin.getServer().getWorld(entry.getKey());
            if (world != null) {
                config.set("worlds." + world.getName() + ".enabled", entry.getValue());
            }
        }

        plugin.saveConfig();
    }

    public String getLanguage() {
        return config.getString("language", "en_US");
    }

    public int getVotePercentage() {
        return config.getInt("voting.required_percentage", 50);
    }

    public int getVoteDuration() {
        return config.getInt("voting.duration_seconds", 30);
    }

    public int getRevoteCooldown() {
        return config.getInt("voting.revote_cooldown_seconds", 60);
    }

    public List<String> getEnabledWorlds() {
        return config.getStringList("enabled_worlds");
    }

    public boolean isWorldEnabled(World world) {
        return worldToggles.getOrDefault(world.getUID(), true);
    }

    public void toggleWorld(World world, boolean enabled) {
        worldToggles.put(world.getUID(), enabled);
        saveConfig();
    }

    public void toggleAllWorlds(boolean enabled) {
        for (World world : plugin.getServer().getWorlds()) {
            worldToggles.put(world.getUID(), enabled);
        }
        saveConfig();
    }
}
