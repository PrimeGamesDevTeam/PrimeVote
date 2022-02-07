package net.primegames.task;

import net.primegames.data.VoteSite;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CheckVoteTask extends VoteTask {

    private final UUID uuid;

    public CheckVoteTask(VoteSite site, Player player) {
        super(site);
        this.uuid = player.getUniqueId();
    }

    @Override
    protected HttpUriRequest onRun() {
        String checkUrl = site.getCheckUrl(uuid);
        return checkUrl != null ? new HttpGet(checkUrl) : null;
    }

    @Override
    protected void onResponse(String response, int lineNumber) {
        site.handleFetchResponse(response, uuid);
        terminateReader();
    }
}
