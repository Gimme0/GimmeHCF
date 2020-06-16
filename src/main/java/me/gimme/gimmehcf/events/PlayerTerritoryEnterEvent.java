package me.gimme.gimmehcf.events;

import me.gimme.gimmehcf.faction.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player enters new faction territory.
 */
public class PlayerTerritoryEnterEvent extends PlayerEvent implements Cancellable {

    public enum EnterReason {
        MOVE,
        TELEPORT;
    }

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private Faction from;
    private Faction to;
    private EnterReason enterReason;

    public PlayerTerritoryEnterEvent(@NotNull final Player player, @NotNull final Faction from,
                                     @NotNull final Faction to, @NotNull final EnterReason enterReason) {
        super(player);
        this.from = from;
        this.to = to;
        this.enterReason = enterReason;
    }

    /**
     * @return the faction owning the territory that this player is leaving
     */
    @NotNull
    public Faction getFrom() {
        return from;
    }

    /**
     * @return the faction owning the territory that this player is entering
     */
    @NotNull
    public Faction getTo() {
        return to;
    }

    /**
     * @return the reason for the player entering the territory
     */
    public EnterReason getEnterReason() {
        return enterReason;
    }

    /**
     * If the event is cancelled, the player will not enter the territory.
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * If the event is cancelled, the player will not enter the territory.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
