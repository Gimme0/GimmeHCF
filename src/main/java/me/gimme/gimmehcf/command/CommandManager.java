package me.gimme.gimmehcf.command;

import me.gimme.gimmecore.command.BaseCommand;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager extends me.gimme.gimmecore.command.CommandManager {

    public CommandManager(JavaPlugin plugin, LanguageManager languageManager) {
        super(plugin);
        BaseCommand.CommandError.ILLEGAL_CHARACTERS.setMessage(languageManager.get(Message.ERROR_ILLEGAL_CHARACTERS).toString());
        BaseCommand.CommandError.INVALID_ARGUMENT.setMessage(languageManager.get(Message.ERROR_INVALID_ARGUMENT).toString());
        BaseCommand.CommandError.NO_PERMISSION.setMessage(languageManager.get(Message.ERROR_NO_PERMISSION).toString());
        BaseCommand.CommandError.NOT_A_COLOR.setMessage(languageManager.get(Message.ERROR_NOT_A_COLOR).toString());
        BaseCommand.CommandError.NOT_A_NUMBER.setMessage(languageManager.get(Message.ERROR_NOT_A_NUMBER).toString());
        BaseCommand.CommandError.PLAYER_ONLY.setMessage(languageManager.get(Message.ERROR_PLAYER_ONLY).toString());
        BaseCommand.CommandError.TOO_FEW_ARGUMENTS.setMessage(languageManager.get(Message.ERROR_TOO_FEW_ARGUMENTS).toString());
        BaseCommand.CommandError.TOO_MANY_ARGUMENTS.setMessage(languageManager.get(Message.ERROR_TOO_MANY_ARGUMENTS).toString());
        BaseCommand.CommandError.UNKNOWN.setMessage(languageManager.get(Message.ERROR_UNKNOWN).toString());
    }

}
