package net.primegames.task;

import net.primegames.data.VoteSite;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

public class CheckVoteTask extends VoteTask {

    private final String username;

    public CheckVoteTask(VoteSite site, String username) {
        super(site);
        this.username = username;
    }

    @Override
    protected HttpUriRequest onRun() {
        return new HttpGet(site.getCheckUrl(username));
    }

    @Override
    protected void onResponse(String response, int lineNumber) {
        site.handleFetchResponse(response, username);
        terminateReader();
    }
}
