package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.command.CommandSender;

public class FADisbandCommand extends FACommand {

    private FactionManager factionManager;

    public FADisbandCommand(FactionManager factionManager, LanguageManager languageManager) {
        super("disband", languageManager);
        this.aliases.add("remove");
        this.argsUsage = "<faction>";
        this.argsAlternatives.add(ArgPlaceholder.FACTIONS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_FA_DISBAND).toString();

        this.factionManager = factionManager;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        String factionName = args[0];

        Faction faction = factionManager.getFaction(factionName);
        if (faction == null) return errorMessage(languageManager.get(Message.ERROR_FACTION_NOT_FOUND)
                .replace(Placeholder.INPUT, factionName).toString());

        factionManager.removeFaction(faction, null);
        return successMessage(languageManager.get(Message.SUCCESS_DISBANDED_FACTION)
                .replace(Placeholder.FACTION, faction.getDisplayName()).toString());
    }

}
