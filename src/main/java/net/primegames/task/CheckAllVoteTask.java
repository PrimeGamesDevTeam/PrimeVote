package net.primegames.task;

import com.google.common.net.HttpHeaders;
import net.primegames.data.ClaimStatus;
import net.primegames.data.VoteSite;
import net.primegames.utils.LoggerUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CheckAllVoteTask implements Runnable {

    private final ArrayList<VoteSite> sites = new ArrayList<>();
    private final UUID uuid;
    private final String name;

    public CheckAllVoteTask(HashMap<String, VoteSite> sites, Player player){
        this.uuid = player.getUniqueId();
        sites.forEach((key, value) -> this.sites.add(value));
        this.name = player.getName();
    }

    @Override
    public void run() {
        HashMap<VoteSite, String> responses = new HashMap<>();
        try {
            final CloseableHttpClient httpClient = HttpClients.createDefault();
            for (VoteSite site : sites) {
                String checkUrl = site.getCheckUrl(uuid);
                if (checkUrl == null){
                    LoggerUtils.info("Player " +  this.name +" logged out while checking vote sites.");
                    continue;
                }
                HttpUriRequest request = new HttpGet(checkUrl);
                request.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9) Gecko/2008052906 Firefox/3.0");
                CloseableHttpResponse response = httpClient.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                responses.put(site, reader.readLine());
            }
            handleResponses(responses);
            httpClient.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleResponses(HashMap<VoteSite, String> responses){
        Player player = Bukkit.getPlayer(uuid);
        if (player == null){
            LoggerUtils.info("player: " + name + " logged out while checking vote sites.");
            return;
        }
        ArrayList<VoteSite> unclaimed = new ArrayList<>();
        ArrayList<VoteSite> notVoted = new ArrayList<>();
        if (responses.isEmpty()){
            player.sendMessage("§cNo vote sites found.");
        }
        responses.forEach((site, response) -> {
            ClaimStatus status = site.handleFetchResponse(response, player.getUniqueId());
            switch (status) {
                case AVAILABLE -> unclaimed.add(site);
                case NOT_VOTED -> notVoted.add(site);
            }
        });
        if (!unclaimed.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You have " + unclaimed.size() + " unclaimed vote. Type" + ChatColor.RED + " /vote claim" + ChatColor.YELLOW + " to claim them.");
        }
        if (!notVoted.isEmpty()){
            for (VoteSite site : notVoted){
                player.sendMessage(ChatColor.YELLOW + "You have not voted on "+ ChatColor.RED + site.getVoteLink() + ChatColor.YELLOW +" yet.");
            }
        }
    }
}
