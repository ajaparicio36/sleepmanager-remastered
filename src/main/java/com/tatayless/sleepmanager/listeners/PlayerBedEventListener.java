package com.tatayless.sleepmanager.listeners;

import com.tatayless.sleepmanager.SleepManager;
import io.papermc.paper.event.player.PlayerDeepSleepEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class PlayerBedEventListener implements Listener {
    private final SleepManager plugin;

    public PlayerBedEventListener(SleepManager plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Player player = event.getPlayer();

        // Check if sleep vote is enabled for this world
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld())) {
            return;
        }

        // If sleep vote was successful, handle sleep action
        if (plugin.getVoteManager().handleSleep(player)) {
            // Night was skipped, cancel the normal sleep behavior
            event.setCancelled(true);
            // Force player to wake up immediately after the night is skipped
            plugin.getServer().getScheduler().runTask(plugin, () -> player.wakeup(true));
            return;
        }

        // If no vote in progress, startVote was already called in handleSleep
    }

    // Handle deep sleep separately - this fires after player sleeps for a short
    // time
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeepSleep(PlayerDeepSleepEvent event) {
        Player player = event.getPlayer();

        // Check if sleep vote is enabled for this world
        if (!plugin.getConfigManager().isWorldEnabled(player.getWorld())) {
            return;
        }

        // If sleep vote was successful, handle sleep action
        if (plugin.getVoteManager().handleSleep(player)) {
            // Just in case, we'll make the player wake up
            player.wakeup(true);
        }
    }
}
