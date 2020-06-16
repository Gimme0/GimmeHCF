package me.gimme.gimmehcf.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PermissionKey {

    PLUGIN("gimmehcf"),

    EDIT("edit", PLUGIN),

    DEATH_TIMER("deathtimer", PLUGIN),
    DEATH_TIMER_RANK3("3", DEATH_TIMER),
    DEATH_TIMER_RANK2("2", DEATH_TIMER),
    DEATH_TIMER_RANK1("1", DEATH_TIMER),
    DEATH_TIMER_RANK0("0", DEATH_TIMER);


    private final String key;
    private final PermissionKey parent;

    PermissionKey(@NotNull String key) {
        this(key, null);
    }

    PermissionKey(@NotNull String key, @Nullable PermissionKey parent) {
        this.key = key;
        this.parent = parent;
    }

    @NotNull
    public String getPath() {
        return (parent == null ? "" : (parent.getPath() + ".")) + key;
    }

    @NotNull
    public String getChildPath(String key) {
        return getPath() + "." + key;
    }

    @NotNull
    @Override
    public String toString() {
        return getPath();
    }

}
