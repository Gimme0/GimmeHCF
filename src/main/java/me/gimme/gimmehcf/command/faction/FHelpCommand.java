package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmecore.command.CommandManager;
import me.gimme.gimmehcf.GimmeHCF;
import me.gimme.gimmehcf.command.BaseHelpCommand;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;

/**
 * Shows a list of all faction commands.
 */
public class FHelpCommand extends BaseHelpCommand {

    public FHelpCommand(CommandManager commandManager, LanguageManager languageManager) {
        super(commandManager, GimmeHCF.COMMAND_FACTION, languageManager.get(Message.HEADER_F_HELP)
                .replace(Placeholder.PAGE, PAGE_PLACEHOLDER).toString());
        this.description = languageManager.get(Message.DESCRIPTION_F_HELP).toString();
    }

}
