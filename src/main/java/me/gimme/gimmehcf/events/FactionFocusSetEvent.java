package me.gimme.gimmehcf.events;

import me.gimme.gimmehcf.faction.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a player sets a new focus for their faction.
 */
public class FactionFocusSetEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private Faction faction;
    private Faction focus;
    private Faction previousFocus;

    public FactionFocusSetEvent(@Nullable Player player, @NotNull final Faction faction, @Nullable final Faction focus,
                                @Nullable final Faction previousFocus) {
        this.player = player;
        this.faction = faction;
        this.focus = focus;
        this.previousFocus = previousFocus;
    }

    /**
     * @return the player that set the focus
     */
    @NotNull
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the faction that had the focus set
     */
    @NotNull
    public Faction getFaction() {
        return faction;
    }

    /**
     * @return the faction that was focused, or null if previous faction was unfocused
     */
    @Nullable
    public Faction getFocus() {
        return focus;
    }

    /**
     * @return the faction that was previously focused, or null if no faction was focused
     */
    @Nullable
    public Faction getPreviousFocus() {
        return previousFocus;
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
