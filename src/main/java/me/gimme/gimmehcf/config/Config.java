package me.gimme.gimmehcf.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Config {

    TELEPORT_DELAY("teleport-delay"),

    BLOCK_PROTECTION_DELAY("block-protection-delay"),

    COMBAT("combat"),
    COMBAT_FACTION_BLACKLIST("faction-blacklist", COMBAT),

    DEATH("death"),
    KILL_CREDIT_TIMER("kill-credit-timer", DEATH),
    DEATH_MESSAGES("death-messages", DEATH),
    DEATH_RAIDABLE_LIGHTNING_PLAYER("raidable-lightning-player", DEATH),
    DEATH_RAIDABLE_LIGHTNING_FACTION("raidable-lightning-faction", DEATH),
    DEATH_FACTION_WHITELIST("faction-whitelist", DEATH),
    DEATH_TIMER_DEFAULT("death-timer-default", DEATH),
    DEATH_TIMER_RANKS("death-timer-ranks", DEATH),
    DEATH_COMMAND_WHITELIST("command-whitelist", DEATH),
    DEATH_COMMAND_BLACKLIST("command-blacklist", DEATH),

    DEFAULT_FACTION("default-faction"),
    DEFAULT_FACTION_BY_WORLD("default-faction-by-world"),

    FLAGS("flags"),
    FLAGS_FACTION_DEFAULT("faction-default", FLAGS),
    FLAGS_FACTION_RAIDABLE("faction-raidable", FLAGS),
    FLAGS_INTERACT_WHITELIST("interact-whitelist", FLAGS),
    FLAGS_INTERACT_BLACKLIST("interact-blacklist", FLAGS),
    FLAGS_MOBS_EXCLUDED_WORLDS("mobs-excluded-worlds", FLAGS),
    FLAGS_GREETING("greeting", FLAGS),
    FLAGS_GREETING_ON_SCREEN("on-screen", FLAGS_GREETING),
    FLAGS_GREETING_FADE_IN("fade-in", FLAGS_GREETING),
    FLAGS_GREETING_STAY("stay", FLAGS_GREETING),
    FLAGS_GREETING_FADE_OUT("fade-out", FLAGS_GREETING),

    FACTION_DTR("faction-dtr"),
    FACTION_DTR_STARTING("starting", FACTION_DTR),
    FACTION_DTR_BASE("base", FACTION_DTR),
    FACTION_DTR_BONUS_PER_PLAYER("bonus-per-player", FACTION_DTR),
    FACTION_DTR_MAX("max", FACTION_DTR),
    FACTION_DTR_REGEN_PERIOD("regen-period", FACTION_DTR),
    FACTION_DTR_REGEN("regen", FACTION_DTR),
    FACTION_DTR_FREEZE_DURATION("freeze-duration", FACTION_DTR),
    FACTION_DTR_WORLD_DEATH_LOSS("world-death-loss", FACTION_DTR),

    FACTION_LAND("faction-land"),
    FACTION_LAND_BASE("base", FACTION_LAND),
    FACTION_LAND_BONUS_PER_PLAYER("bonus-per-player", FACTION_LAND),
    FACTION_LAND_MAX("max", FACTION_LAND),
    FACTION_LAND_ADJACENT("adjacent", FACTION_LAND),
    FACTION_LAND_GAP("faction-gap", FACTION_LAND),
    FACTION_LAND_GAP_SYSTEM_INCLUDED("faction-gap-system", FACTION_LAND),

    FACTION_PLAYER("faction-player"),
    FACTION_PLAYER_FACTION_LIMIT("faction-limit", FACTION_PLAYER),
    FACTION_PLAYER_FRIENDLY_FIRE("friendly-fire", FACTION_PLAYER),
    FACTION_PLAYER_FRIENDLY_FIRE_ALLY("friendly-fire-ally", FACTION_PLAYER),
    FACTION_PLAYER_UNCLAIM_RAIDABLE("unclaim-raidable", FACTION_PLAYER),
    FACTION_PLAYER_DISBAND_RAIDABLE("disband-raidable", FACTION_PLAYER),
    FACTION_PLAYER_JOIN_RAIDABLE("join-raidable", FACTION_PLAYER),
    FACTION_PLAYER_LEAVE_RAIDABLE("leave-raidable", FACTION_PLAYER),
    FACTION_PLAYER_LEAVE_WHILE_HOME("leave-while-home", FACTION_PLAYER),

    FACTION_NAME("faction-name"),
    FACTION_NAME_MIN("min", FACTION_NAME),
    FACTION_NAME_MAX("max", FACTION_NAME),
    FACTION_NAME_CHARACTERS("characters", FACTION_NAME),
    FACTION_NAME_BLACKLIST("blacklist", FACTION_NAME),

    FACTION_COLORS("faction-colors"),
    FACTION_COLORS_YOU("you", FACTION_COLORS),
    FACTION_COLORS_ALLY("ally", FACTION_COLORS),
    FACTION_COLORS_FOCUS("focus", FACTION_COLORS),
    FACTION_COLORS_ENEMY("enemy", FACTION_COLORS),
    FACTION_COLORS_NEUTRAL("neutral", FACTION_COLORS),

    FACTION_PREFIX("faction-prefix"),
    FACTION_PREFIX_LEADER("leader", FACTION_PREFIX),
    FACTION_PREFIX_OFFICER("officer", FACTION_PREFIX),

    EVENT_SCORE("event-score"),
    EVENT("event"),
    SOTW("sotw", EVENT),
    SOTW_PVP("pvp", SOTW),
    SOTW_DISABLE_END_PORTAL("disable-end-portal", SOTW),
    SOTW_DISABLE_NETHER_PORTAL("disable-nether-portal", SOTW),
    SOTW_LOCKOUT_FACTION_WHITELIST("lockout-faction-whitelist", SOTW),

    AUTO_SAVE_PERIOD("auto-save-period"),
    AUTO_SAVE_LOGGING("auto-save-logging");


    private final String key;
    private final Config parent;

    Config(@NotNull String key) {
        this(key, null);
    }

    Config(@NotNull String key, @Nullable Config parent) {
        this.key = key;
        this.parent = parent;
    }

    @NotNull
    public String getPath() {
        return (parent == null ? "" : (parent.getPath() + ".")) + key;
    }

    @NotNull
    @Override
    public String toString() {
        return getPath();
    }

}
