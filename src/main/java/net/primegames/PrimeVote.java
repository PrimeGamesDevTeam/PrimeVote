package net.primegames;

import lombok.Getter;
import net.primegames.commands.PrimeVoteCommand;
import net.primegames.data.VoteReward;
import net.primegames.data.VoteSite;
import net.primegames.listener.VoteListener;
import net.primegames.task.CheckAllVoteTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public final class PrimeVote {

    @Getter
    private static PrimeVote instance;
    @Getter
    private final JavaPlugin plugin;
    @Getter
    private final HashMap<String, VoteSite> voteSites = new HashMap<>();
    @Getter
    private final VoteReward reward;

    public PrimeVote(JavaPlugin plugin, VoteReward reward) {
        instance = this;
        this.plugin = plugin;
        this.reward = reward;
        Bukkit.getPluginManager().registerEvents(new VoteListener(), plugin);
        Bukkit.getCommandMap().register("primevote", new PrimeVoteCommand());
    }

    public PrimeVote(JavaPlugin plugin, VoteReward reward, List<VoteSite> voteSites) {
        instance = this;
        this.plugin = plugin;
        this.reward = reward;
        Bukkit.getPluginManager().registerEvents(new VoteListener(), plugin);
        Bukkit.getCommandMap().register("primevote", new PrimeVoteCommand());
        registerVoteSite(voteSites);
    }

    public void registerVoteSite(VoteSite ...voteSite) {
        for (VoteSite site : voteSite) {
            voteSites.put(site.getName(), site);
        }
    }

    public void registerVoteSite(List<VoteSite> voteSite) {
        for (VoteSite site : voteSite) {
            voteSites.put(site.getName(), site);
        }
    }

    public void attemptClaimAllVotes(Player player) {
        AtomicInteger claimed = new AtomicInteger();
        voteSites.forEach((key, value) -> {
            if (value.claimVote(player)){
                claimed.getAndIncrement();
            }
        });
        if (claimed.get() < 1) {
            player.sendMessage(ChatColor.RED + "You have no unclaimed votes! Type " + ChatColor.YELLOW + "/vote check " + ChatColor.RED + " after voting.");
        }
    }

    public void checkVotes(Player player) {
        CompletableFuture.runAsync(new CheckAllVoteTask(voteSites, player));
    }

    public void listVotesSites(Player player) {
        StringBuilder builder = new StringBuilder();
        AtomicInteger i = new AtomicInteger();
        i.set(1);
        voteSites.forEach((key, value) -> builder.append(ChatColor.RESET).append(i.getAndIncrement()).append(": ").append(ChatColor.YELLOW).append(value.getVoteLink()).append(" \n"));
        player.sendMessage(builder.toString());
    }

    public boolean hasUnclaimedVotes(Player player) {
        for (VoteSite site : voteSites.values()) {
            if (site.canClaim(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }
}
