package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class FASetdtrCommand extends FACommand {

    private FactionManager factionManager;

    public FASetdtrCommand(FactionManager factionManager, LanguageManager languageManager) {
        super("setdtr", languageManager);
        this.argsUsage = "<faction> <dtr>";
        this.argsAlternatives.add(ArgPlaceholder.FACTIONS.getPlaceholder() + " 5.5");
        this.minArgs = 2;
        this.maxArgs = 2;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_FA_SETDTR).toString();

        this.factionManager = factionManager;
    }

    @Override
    protected String execute(@NotNull CommandSender sender, String[] args) {
        String factionName = args[0];
        String dtrArg = args[1];

        Faction faction = factionManager.getFaction(factionName);

        if (faction == null) return errorMessage(languageManager.get(Message.ERROR_FACTION_NOT_FOUND)
                .replace(Placeholder.INPUT, factionName).toString());

        try {
            double newDtr = Double.parseDouble(dtrArg);
            factionManager.setDtr(faction, newDtr);
            return successMessage(languageManager.get(Message.SUCCESS_ADMIN_DTR_SET)
                    .replace(Placeholder.FACTION, faction.getDisplayName())
                    .replace(Placeholder.N, String.valueOf(newDtr)).toString());
        } catch (NumberFormatException e) {
            return errorMessageWithUsage(CommandError.NOT_A_NUMBER, dtrArg);
        }
    }

}
