package net.primegames.event;

import lombok.Getter;
import net.primegames.data.ClaimStatus;
import net.primegames.data.VoteSite;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VoteClaimStatusUpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final VoteSite site;
    @Getter
    private final String player;
    private ClaimStatus claimStatus = ClaimStatus.AVAILABLE;

    public VoteClaimStatusUpdateEvent(@NotNull String player, @NotNull VoteSite site) {
        super(true);
        this.site = site;
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void setClaimStatus(ClaimStatus claimStatus) {
        this.claimStatus = claimStatus;
    }

    public ClaimStatus getClaimStatus() {
        return claimStatus;
    }
}
