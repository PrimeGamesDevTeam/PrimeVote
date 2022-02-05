package net.primegames.listener;

import net.primegames.PrimeVote;
import net.primegames.task.CheckAllVoteTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

public class VoteListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PrimeVote.getInstance().checkVotes(event.getPlayer());
    }

}
