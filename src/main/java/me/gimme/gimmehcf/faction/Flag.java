package me.gimme.gimmehcf.faction;

import org.jetbrains.annotations.Nullable;

public enum Flag {

    GRIEF(false), // If terrain can be edited by any means (non-members place/break, explode, mob, fire, etc.).
    EXPLOSION(false), // If blocks can break from explosions (overrides the GRIEF flag).
    INTERACT(false), // If non-members can use buttons, levers, chests etc.
    DAMAGE(true), // If players can take damage from any source.
    HUNGER(true), // If players' food level can go down.
    CLAIM(false), // If faction can be over-claimed.
    MOBS(true), // If mobs can naturally spawn.
    PEARL(true), // If players can teleport to or from here with ender pearls.
    GREETING(true), // Message on enter.
    SYSTEM(false); // If it is a system faction, used where system factions have special rules

    private final boolean defaultValue;

    Flag(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the default boolean value of this flag.
     * Note that this is only a last resort for getting the value of a flag for a faction
     * as normally it would be stored in the faction, or you would get the default value from the config first.
     *
     * @return the default boolean value of this flag
     */
    public boolean getDefault() {
        return defaultValue;
    }

    /**
     * Gets the Flag with the corresponding string flag, or null if not found.
     *
     * @param flag the string flag
     * @return the Flag with the corresponding string flag, or null if not found
     */
    @Nullable
    public static Flag getByString(String flag) {
        for (Flag f : Flag.values()) {
            if (f.toString().equals(flag))
                return f;
        }
        return null;
    }

}
