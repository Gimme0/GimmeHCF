package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.SysFaction;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.command.CommandSender;

import static me.gimme.gimmehcf.command.ArgPlaceholder.FACTIONS;

public class FAClearflagsCommand extends FACommand {

    private FactionManager factionManager;

    public FAClearflagsCommand(FactionManager factionManager, LanguageManager languageManager) {
        super("clearflags",languageManager);
        this.aliases.add("resetflags");
        this.argsUsage = "<faction>";
        this.argsAlternatives.add(FACTIONS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_FA_CLEARFLAGS).toString();

        this.factionManager = factionManager;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        String factionName = args[0];
        SysFaction faction = factionManager.getSystemFaction(factionName);

        if (faction == null) return errorMessage(languageManager.get(Message.ERROR_SYSFACTION_NOT_FOUND)
                .replace(Placeholder.INPUT, factionName).toString());

        faction.clearFlags();
        return successMessage(languageManager.get(Message.SUCCESS_ADMIN_CLEARED_FLAGS)
                .replace(Placeholder.FACTION, faction.getDisplayName()).toString());
    }
}
