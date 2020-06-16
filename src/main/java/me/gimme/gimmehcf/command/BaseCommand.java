package me.gimme.gimmehcf.command;

import me.gimme.gimmehcf.GimmeHCF;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCommand extends me.gimme.gimmecore.command.BaseCommand {

    protected BaseCommand(@NotNull String parent, @NotNull String name) {
        super(parent, name);
        this.permission = GimmeHCF.PERMISSION_PATH + "." + parent + "." + name;
    }

}
