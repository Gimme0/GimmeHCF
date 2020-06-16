package me.gimme.gimmehcf.events;

import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.Relation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when the relation between two factions changes.
 */
public class FactionRelationChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Faction faction1;
    private Faction faction2;
    private Relation fromRelation;
    private Relation toRelation;

    public FactionRelationChangeEvent(@NotNull final Faction faction1, @NotNull final Faction faction2,
                                      @Nullable final Relation fromRelation, @NotNull final Relation toRelation) {
        this.faction1 = faction1;
        this.faction2 = faction2;
        this.fromRelation = fromRelation;
        this.toRelation = toRelation;
    }

    /**
     * @return one of the two factions that the relation changed between
     */
    @NotNull
    public Faction getFaction1() {
        return faction1;
    }

    /**
     * @return one of the two factions that the relation changed between
     */
    @NotNull
    public Faction getFaction2() {
        return faction2;
    }

    /**
     * @return the old relation between the factions, or null if that information is not available
     */
    @Nullable
    public Relation getFromRelation() {
        return fromRelation;
    }

    /**
     * @return the new relation between the factions
     */
    @NotNull
    public Relation getToRelation() {
        return toRelation;
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
