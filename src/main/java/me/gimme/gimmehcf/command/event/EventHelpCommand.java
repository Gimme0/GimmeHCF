package me.gimme.gimmehcf.command.event;

import me.gimme.gimmecore.command.CommandManager;
import me.gimme.gimmehcf.GimmeHCF;
import me.gimme.gimmehcf.command.BaseHelpCommand;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.Placeholder;
import org.jetbrains.annotations.NotNull;

public class EventHelpCommand extends BaseHelpCommand {

    public EventHelpCommand(@NotNull CommandManager commandManager, @NotNull LanguageManager languageManager) {
        super(commandManager, GimmeHCF.COMMAND_EVENT, languageManager.get(Message.HEADER_EVENT_HELP)
                .replace(Placeholder.PAGE, PAGE_PLACEHOLDER).toString());
        this.description = languageManager.get(Message.DESCRIPTION_EVENT_HELP).toString();
    }

}
