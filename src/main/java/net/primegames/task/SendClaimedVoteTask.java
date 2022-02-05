package net.primegames.task;

import net.primegames.PrimeVote;
import net.primegames.data.VoteSite;
import net.primegames.event.VoteClaimedEvent;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.logging.Level;

public class SendClaimedVoteTask extends VoteTask{

    private final String username;

    public SendClaimedVoteTask(VoteSite site, String username) {
        super(site);
        this.username = username;
    }

    @Override
    protected HttpUriRequest onRun() {
        return new HttpPost(site.getClaimUrl(username));
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
