package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.GimmeHCF;
import me.gimme.gimmehcf.command.BaseCommand;
import me.gimme.gimmehcf.language.LanguageManager;
import org.jetbrains.annotations.NotNull;

abstract class FACommand extends BaseCommand {

    protected final LanguageManager languageManager;

    protected FACommand(@NotNull String name, @NotNull LanguageManager languageManager) {
        super(GimmeHCF.COMMAND_FACTION_ADMIN, name);
        this.languageManager = languageManager;
    }

}
