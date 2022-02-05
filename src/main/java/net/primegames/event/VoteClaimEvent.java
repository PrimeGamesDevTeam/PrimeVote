package net.primegames.event;

import lombok.Getter;
import net.primegames.data.ClaimStatus;
import net.primegames.data.VoteSite;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player is claiming vote
 */
public class VoteClaimEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final Player player;
    @Getter
    private final VoteSite site;
    @Getter
    private ClaimStatus status = ClaimStatus.AVAILABLE;

    public VoteClaimEvent(Player player, VoteSite site) {
        this.player = player;
        this.site = site;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }
}
