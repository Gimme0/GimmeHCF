package me.gimme.gimmehcf.faction;

import me.gimme.gimmehcf.config.Config;

import static me.gimme.gimmehcf.config.Config.*;

public enum Relation {
    YOU(FACTION_COLORS_YOU),
    ALLY(FACTION_COLORS_ALLY),
    FOCUS(FACTION_COLORS_FOCUS),
    ENEMY(FACTION_COLORS_ENEMY),
    NEUTRAL(FACTION_COLORS_NEUTRAL);

    private final Config colorConfig;

    Relation(Config colorConfig) {
        this.colorConfig = colorConfig;
    }

    public String getColorConfigPath() {
        return colorConfig.getPath();
    }
}
