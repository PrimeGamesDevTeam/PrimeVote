package net.primegames.task;

import com.google.common.net.HttpHeaders;
import net.primegames.data.VoteSite;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class VoteTask implements Runnable {

    protected VoteSite site;
    protected boolean terminateRead = false;

    public VoteTask(VoteSite site){
        this.site = site;
    }

    @Override
    public void run() {
        try {
            final CloseableHttpClient httpClient = HttpClients.createDefault();
            final HttpUriRequest request = onRun();
            if (onRun() == null) {
                return;
            }
            request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9) Gecko/2008052906 Firefox/3.0");
            CloseableHttpResponse response = httpClient.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null && !terminateRead) {
                onResponse(line, lineNumber++);
            }
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract HttpUriRequest onRun();

    protected void onResponse(String response, int lineNumber){
    }

    /**
     * Terminates reading of the response lines.
     */
    protected void terminateReader() {
        this.terminateRead = true;
    }

}
