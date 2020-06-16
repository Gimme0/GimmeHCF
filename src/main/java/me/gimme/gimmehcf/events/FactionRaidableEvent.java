package me.gimme.gimmehcf.events;

import me.gimme.gimmehcf.faction.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called whenever a faction becomes raidable.
 */
public class FactionRaidableEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private Faction faction;
    private Player player;

    public FactionRaidableEvent(@NotNull final Faction faction, @Nullable Player player) {
        this.faction = faction;
        this.player = player;
    }

    /**
     * @return the faction that became raidable
     */
    @NotNull
    public Faction getFaction() {
        return faction;
    }

    /**
     * @return the player that died to cause the faction to become raidable, or null if another cause
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * If the event is cancelled, the faction will stay just above 0 DTR and not go raidable.
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * If the event is cancelled, the faction will stay just above 0 DTR and not go raidable.
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
