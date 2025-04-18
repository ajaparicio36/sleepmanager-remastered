package com.tatayless.sleepmanager.vote;

import com.tatayless.sleepmanager.SleepManager;
import net.kyori.adventure.text.Component;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class VoteManager {
    private final SleepManager plugin;
    private final Map<UUID, VoteSession> activeVotes = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastVoteTime = new ConcurrentHashMap<>();

    public VoteManager(SleepManager plugin) {
        this.plugin = plugin;
    }

    public boolean startVote(Player initiator) {
        World world = initiator.getWorld();
        UUID worldId = world.getUID();

        // Check if voting is enabled for this world
        if (!plugin.getConfigManager().isWorldEnabled(world)) {
            initiator.sendMessage(plugin.getLanguageManager().getComponent("voting.disabled_world"));
            return false;
        }

        // Check if this is a valid world for voting (not nether or end)
        if (world.getEnvironment() != World.Environment.NORMAL) {
            initiator.sendMessage(plugin.getLanguageManager().getComponent("voting.invalid_world"));
            return false;
        }

        // Check if it's night time
        long time = world.getTime();
        if (time < 12500 || time > 23500) {
            initiator.sendMessage(plugin.getLanguageManager().getComponent("voting.not_night_time"));
            return false;
        }

        // Check if a vote is already in progress
        if (activeVotes.containsKey(worldId)) {
            initiator.sendMessage(plugin.getLanguageManager().getComponent("voting.already_active"));
            return false;
        }

        // Get eligible voters (players in the same world)
        List<Player> eligibleVoters = new ArrayList<>();
        for (Player player : world.getPlayers()) {
            eligibleVoters.add(player);
        }

        if (eligibleVoters.size() <= 1) {
            // Only one player, auto-pass
            plugin.getServer().sendMessage(plugin.getLanguageManager().getComponent("voting.single_player_pass"));
            return false;
        }

        // Create new vote session
        int requiredPercentage = plugin.getConfigManager().getVotePercentage();
        int voteDuration = plugin.getConfigManager().getVoteDuration();
        VoteSession voteSession = new VoteSession(worldId, initiator.getUniqueId(), eligibleVoters, requiredPercentage,
                voteDuration);

        // Broadcast vote message to all players in the world
        Component voteMessage = plugin.getLanguageManager().createVoteMessage(initiator.getName(), requiredPercentage);
        for (Player player : eligibleVoters) {
            player.sendMessage(voteMessage);
        }

        // Add initiator's vote as YES
        voteSession.castVote(initiator.getUniqueId(), true);

        // Schedule vote end
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            endVote(worldId);
        }, voteDuration * 20L);
        voteSession.setTask(task);

        // Save vote session
        activeVotes.put(worldId, voteSession);
        lastVoteTime.put(worldId, Instant.now().getEpochSecond());

        return true;
    }

    public void endVote(UUID worldId) {
        VoteSession session = activeVotes.remove(worldId);
        if (session == null)
            return;

        // Cancel task if it's still running
        if (session.getTask() != null) {
            session.getTask().cancel();
        }

        World world = plugin.getServer().getWorld(worldId);
        if (world == null)
            return;

        boolean passed = session.isPassed();

        // Broadcast results
        String resultKey = passed ? "voting.passed" : "voting.failed";
        Component resultMessage = plugin.getLanguageManager().getComponent(resultKey,
                "yes", session.getYesVotes(),
                "no", session.getNoVotes());

        for (Player player : world.getPlayers()) {
            player.sendMessage(resultMessage);
        }

        if (passed) {
            // Mark the vote as passed so the next sleep attempt will succeed
            session.setVotePassed(true);

            // Put the session back in activeVotes so handleSleep can find it
            activeVotes.put(worldId, session);
        }
    }

    public boolean handleSleep(Player player) {
        World world = player.getWorld();
        UUID worldId = world.getUID();

        VoteSession lastSession = activeVotes.get(worldId);

        // If no active vote or vote didn't pass
        if (lastSession == null || !lastSession.isVotePassed()) {
            // Start a new vote if no vote is active
            if (lastSession == null) {
                startVote(player);
            }
            return false;
        }

        // Clear phantom spawn timer for all players in the world
        for (Player worldPlayer : world.getPlayers()) {
            worldPlayer.setStatistic(org.bukkit.Statistic.TIME_SINCE_REST, 0);
        }

        // Send phantom reset message to all players
        Component phantomMessage = plugin.getLanguageManager().getComponent("sleep.phantom_reset");
        for (Player worldPlayer : world.getPlayers()) {
            worldPlayer.sendMessage(phantomMessage);
        }

        // Skip night (only if doWeatherCycle is true)
        if (Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) {
            world.setTime(0); // Set time to morning

            // Clear weather if storming (only if doWeatherCycle is true)
            if (Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_WEATHER_CYCLE)) && world.isThundering()) {
                world.setStorm(false);
                world.setThundering(false);
            }

            // Broadcast message
            Component successMessage = plugin.getLanguageManager().getComponent("sleep.night_skipped",
                    "player", player.getName());

            for (Player worldPlayer : world.getPlayers()) {
                worldPlayer.sendMessage(successMessage);
            }

            // Remove the vote session
            activeVotes.remove(worldId);
            return true;
        }

        return false;
    }

    public boolean castVote(Player player, boolean vote) {
        UUID worldId = player.getWorld().getUID();
        VoteSession session = activeVotes.get(worldId);

        if (session == null) {
            player.sendMessage(plugin.getLanguageManager().getComponent("voting.no_active_vote"));
            return false;
        }

        boolean changed = session.castVote(player.getUniqueId(), vote);
        String messageKey = vote ? "voting.voted_yes" : "voting.voted_no";

        if (changed) {
            player.sendMessage(plugin.getLanguageManager().getComponent(messageKey));
        } else {
            player.sendMessage(plugin.getLanguageManager().getComponent("voting.already_voted"));
        }

        // Check if vote should pass early
        if (session.shouldPassEarly()) {
            // End vote but don't remove the session completely yet
            endVote(worldId);
        }

        return true;
    }

    public boolean canRevote(UUID worldId) {
        Long lastVote = lastVoteTime.get(worldId);
        if (lastVote == null)
            return true;

        long now = Instant.now().getEpochSecond();
        int cooldown = plugin.getConfigManager().getRevoteCooldown();

        return now - lastVote >= cooldown;
    }

    public boolean revote(Player player) {
        UUID worldId = player.getWorld().getUID();

        // Check if there's an active vote
        if (activeVotes.containsKey(worldId)) {
            player.sendMessage(plugin.getLanguageManager().getComponent("voting.already_active"));
            return false;
        }

        // Check for cooldown
        if (!canRevote(worldId)) {
            int cooldown = plugin.getConfigManager().getRevoteCooldown();
            Long lastVote = lastVoteTime.get(worldId);
            long now = Instant.now().getEpochSecond();
            long remainingTime = cooldown - (now - lastVote);

            player.sendMessage(plugin.getLanguageManager().getComponent("voting.cooldown",
                    "time", remainingTime));
            return false;
        }

        return startVote(player);
    }

    public boolean hasActiveVote(World world) {
        return activeVotes.containsKey(world.getUID());

    }
}
