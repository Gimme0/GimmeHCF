package me.gimme.gimmehcf.command;

public enum ArgPlaceholder {

    PLAYERS("%player%"),
    FACTIONS("%faction%"),
    SYSTEM_FACTIONS("%system_faction%"),
    MATERIAL("%material%"),
    WILDCARD(CommandManager.WILDCARD_PLACEHOLDER);

    private String placeholder;

    ArgPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }

}
