package net.primegames.task;

import net.primegames.PrimeVote;
import net.primegames.data.VoteSite;
import net.primegames.event.VoteClaimedEvent;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class SendClaimedVoteTask extends VoteTask{

    private final String username;
    private final Player player;

    public SendClaimedVoteTask(VoteSite site, Player player) {
        super(site);
        this.player = player;
        this.username = site.correctUserName(player);
    }

    @Override
    protected HttpUriRequest onRun() {
        return new HttpPost(site.getClaimUrl(player));
    }

    @Override
    protected void onResponse(String response, int lineNumber) {
        switch (response) {
            case "0" -> PrimeVote.getInstance().getPlugin().getLogger().log(Level.WARNING, "Error while claiming vote for " + username + " on " + site.getName());
            case "1" -> {
                PrimeVote.getInstance().getPlugin().getLogger().info("Successfully claimed vote for " + username + " on " + site.getName());
                (new VoteClaimedEvent(username, site)).callEvent();
            }
        }
        terminateReader();
    }
}
