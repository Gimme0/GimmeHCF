package me.gimme.gimmehcf.events;

import me.gimme.gimmehcf.faction.Faction;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Called every time a player leaves a faction. This includes players getting kicked and all players in a faction
 * that gets disbanded.
 */
public class PlayerLeaveFactionEvent extends Event {

    public enum Reason {
        LEAVE,
        KICK,
        DISBAND
    }

    private static final HandlerList handlers = new HandlerList();
    private OfflinePlayer player;
    private Faction faction;
    private Reason reason;

    public PlayerLeaveFactionEvent(@NotNull final OfflinePlayer player, @NotNull final Faction faction, @NotNull Reason reason) {
        this.player = player;
        this.faction = faction;
        this.reason = reason;
    }

    /**
     * @return the player that left the faction
     */
    @NotNull
    public OfflinePlayer getPlayer() {
        return player;
    }

    /**
     * @return the faction that the player left
     */
    @NotNull
    public Faction getFaction() {
        return faction;
    }

    @NotNull
    public Reason getReason() {
        return reason;
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
