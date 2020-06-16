package me.gimme.gimmehcf.events;

import me.gimme.gimmehcf.faction.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Called every time a player joins a faction. This includes when a player creates a new faction.
 */
public class PlayerJoinFactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Faction faction;

    public PlayerJoinFactionEvent(@NotNull final Player player, @NotNull final Faction faction) {
        this.player = player;
        this.faction = faction;
    }

    /**
     * @return the player that joined the faction
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the faction that the player joined
     */
    @NotNull
    public Faction getFaction() {
        return faction;
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
