package me.gimme.gimmehcf.language;

import me.gimme.gimmecore.language.PlaceholderString;

public enum Placeholder implements PlaceholderString {

    PLAYER,
    FACTION,
    N,
    INPUT,
    COORDINATES,
    PAGE,
    KILLER,
    TIME,
    MATERIAL;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @Override
    public String getPlaceholder() {
        return name().toLowerCase();
    }

}
