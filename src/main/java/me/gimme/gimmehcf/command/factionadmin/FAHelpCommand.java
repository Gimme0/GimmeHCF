package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmecore.command.CommandManager;
import me.gimme.gimmehcf.GimmeHCF;
import me.gimme.gimmehcf.command.BaseHelpCommand;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;

public class FAHelpCommand extends BaseHelpCommand {

    public FAHelpCommand(CommandManager commandManager, LanguageManager languageManager) {
        super(commandManager, GimmeHCF.COMMAND_FACTION_ADMIN, languageManager.get(Message.HEADER_FA_HELP)
                .replace(Placeholder.PAGE, PAGE_PLACEHOLDER).toString());
        this.description = languageManager.get(Message.DESCRIPTION_FA_HELP).toString();
    }

}
