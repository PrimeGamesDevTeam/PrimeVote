package net.primegames.data;

import lombok.Getter;
import lombok.NonNull;
import net.primegames.PrimeVote;
import net.primegames.event.VoteClaimEvent;
import net.primegames.event.VoteClaimStatusUpdateEvent;
import net.primegames.task.CheckVoteTask;
import net.primegames.task.SendClaimedVoteTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;


public class VoteSite {

    public static class Builder{
        private String name;
        private String voteLink;
        private String checkUrl;
        private String claimUrl;
        private String checkTopUrl;
        private String apiKey;

        private Builder(){}

        public static Builder create(){
            return new Builder();
        }

        public Builder name(@NonNull String name){
            this.name = name;
            return this;
        }

        public Builder voteLink(@NonNull String voteLink){
            this.voteLink = voteLink;
            return this;
        }

        public Builder checkUrl(@NonNull String checkUrl){
            this.checkUrl = checkUrl;
            return this;
        }

        public Builder claimUrl(@NonNull String claimUrl){
            this.claimUrl = claimUrl;
            return this;
        }

        public Builder checkTopUrl(@NonNull String checkTopUrl){
            this.checkTopUrl = checkTopUrl;
            return this;
        }

        public Builder apiKey(@NonNull String apiKey){
            this.apiKey = apiKey;
            return this;
        }

        public VoteSite build(){
            if (name == null || voteLink == null || checkUrl == null || claimUrl == null || checkTopUrl == null || apiKey == null){
                throw new IllegalArgumentException("All fields must be set!");
            }
            return new VoteSite(name, voteLink, checkUrl, claimUrl, checkTopUrl, apiKey);
        }
    }

    @Getter
    private final String name;
    @Getter
    private final String voteLink;
    private final String checkUrl;
    private final String claimUrl;
    private final String checkTopUrl;
    private final ArrayList<String> availableClaims = new ArrayList<>();

    public VoteSite(String name, String voteLink, String checkUrl, String claimUrl, String checkTopUrl, String apiKey) {
        this.name = name;
        this.voteLink = voteLink;
        this.checkUrl = checkUrl.replace("{ServerKey}", apiKey);
        this.claimUrl = claimUrl.replace("{ServerKey}", apiKey);
        this.checkTopUrl = checkTopUrl.replace("{ServerKey}", apiKey);
    }

    public String getClaimUrl(String username) {
        return claimUrl.replace("{Username}", PrimeVote.niceName(username));
    }

    public String getCheckUrl(String username) {
        return checkUrl.replace("{Username}", PrimeVote.niceName(username));
    }

    public String getCheckTopUrl(Period period) {
        return checkTopUrl.replace("{Period}", period.toString());
    }

    public boolean canClaim(String username) {
        return availableClaims.contains(username.toLowerCase());
    }

    public void addAvailableClaim(String username) {
        availableClaims.add(username.toLowerCase());
    }

    public boolean claimVote(String username) {
        Player player = Bukkit.getPlayer(username);
        if (player != null && availableClaims.contains(username.toLowerCase())) {
            VoteClaimEvent event = new VoteClaimEvent(player, this);
            event.callEvent();
            if(PrimeVote.getInstance().getReward().sendReward(player)){
                event.setStatus(ClaimStatus.CLAIMED);
            }
            if (event.getStatus().equals(ClaimStatus.CLAIMED)){
                availableClaims.remove(username.toLowerCase());
                CompletableFuture.runAsync(new SendClaimedVoteTask(this, username));
            }
            return true;
        }
        return false;
    }

    public boolean claimVote(Player player) {
        return claimVote(player.getName());
    }

    public void checkVote(Player player) {
        CompletableFuture.runAsync(new CheckVoteTask(this, player.getName()));
    }

    public ClaimStatus handleFetchResponse(String response, String username) {
        switch (response) {
            case "1" -> {
                addAvailableClaim(username);
                Player player = Bukkit.getPlayer(username);
                if (player != null) {
                    player.sendMessage("§aYou have an unclaimed vote from " + getVoteLink() + ".Type §c/vote claim §ato claim it.");
                }
                return ClaimStatus.AVAILABLE;
            }
            case "0" -> {
                return ClaimStatus.NOT_VOTED;
            }
            case "2" -> {
                return ClaimStatus.CLAIMED;
            }
        }
        return ClaimStatus.UNKNOWN;
    }

}
