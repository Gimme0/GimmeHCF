package me.gimme.gimmehcf.command.event;

import me.gimme.gimmehcf.GimmeHCF;
import me.gimme.gimmehcf.command.BaseCommand;
import me.gimme.gimmehcf.gameevent.GameEventManager;
import me.gimme.gimmehcf.language.LanguageManager;
import org.jetbrains.annotations.NotNull;

public abstract class EventCommand extends BaseCommand {

    protected final LanguageManager languageManager;
    protected final GameEventManager gameEventManager;

    protected EventCommand(@NotNull String name, @NotNull LanguageManager languageManager,
                           @NotNull GameEventManager gameEventManager) {
        super(GimmeHCF.COMMAND_EVENT, name);
        this.languageManager = languageManager;
        this.gameEventManager = gameEventManager;
    }

}
