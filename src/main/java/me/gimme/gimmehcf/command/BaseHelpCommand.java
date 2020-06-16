package me.gimme.gimmehcf.command;

import me.gimme.gimmecore.command.CommandManager;
import org.jetbrains.annotations.Nullable;

public abstract class BaseHelpCommand extends me.gimme.gimmecore.command.BaseHelpCommand {
    protected BaseHelpCommand(CommandManager commandManager, String parent, @Nullable String header) {
        super(commandManager, parent, header, 9, true);
    }
}
