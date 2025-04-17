package com.tatayless.sleepmanager.vote;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class VoteSession {
    private final UUID worldId;
    private final UUID initiator;
    private final List<UUID> eligibleVoters;
    private final Set<UUID> yesVotes = new HashSet<>();
    private final Set<UUID> noVotes = new HashSet<>();
    private final int requiredPercentage;
    private boolean votePassed = false;
    private BukkitTask task;

    public VoteSession(UUID worldId, UUID initiator, List<Player> voters, int requiredPercentage, int duration) {
        this.worldId = worldId;
        this.initiator = initiator;
        this.eligibleVoters = new ArrayList<>();
        for (Player player : voters) {
            this.eligibleVoters.add(player.getUniqueId());
        }
        this.requiredPercentage = requiredPercentage;
    }

    public boolean castVote(UUID playerId, boolean vote) {
        // Remove existing votes by this player first
        boolean changed = yesVotes.remove(playerId) || noVotes.remove(playerId);

        if (vote) {
            yesVotes.add(playerId);
        } else {
            noVotes.add(playerId);
        }

        return changed || !vote; // Always return true for a new vote
    }

    public boolean isPassed() {
        if (eligibleVoters.isEmpty())
            return false;

        int totalVotes = yesVotes.size() + noVotes.size();
        int totalEligible = eligibleVoters.size();

        // Need at least one vote
        if (totalVotes == 0)
            return false;

        // Check if percentage of yes votes meets required threshold
        double yesPercentage = (double) yesVotes.size() / totalEligible * 100;
        return yesPercentage >= requiredPercentage;
    }

    public boolean shouldPassEarly() {
        // Calculate if there are enough votes to pass early
        int totalEligible = eligibleVoters.size();

        // If all players have voted or enough yes votes to meet the threshold
        return (yesVotes.size() + noVotes.size() >= totalEligible) ||
                (yesVotes.size() * 100 / totalEligible >= requiredPercentage);
    }

    public int getYesVotes() {
        return yesVotes.size();
    }

    public int getNoVotes() {
        return noVotes.size();
    }

    public UUID getWorldId() {
        return worldId;
    }

    public UUID getInitiator() {
        return initiator;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public BukkitTask getTask() {
        return task;
    }

    public boolean isVotePassed() {
        return votePassed;
    }

    public void setVotePassed(boolean votePassed) {
        this.votePassed = votePassed;
    }
}
