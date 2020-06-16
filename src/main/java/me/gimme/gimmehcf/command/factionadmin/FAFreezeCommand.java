package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.faction.DtrRegenManager;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import me.gimme.gimmehcf.util.TimeFormat;
import org.bukkit.command.CommandSender;

public class FAFreezeCommand extends FACommand {

    private FactionManager factionManager;
    private DtrRegenManager dtrRegenManager;

    public FAFreezeCommand(FactionManager factionManager, LanguageManager languageManager, DtrRegenManager dtrRegenManager) {
        super("freeze", languageManager);
        this.argsUsage = "<faction> <seconds>";
        this.argsAlternatives.add(ArgPlaceholder.FACTIONS.getPlaceholder());
        this.minArgs = 2;
        this.maxArgs = 2;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_FA_FREEZE).toString();

        this.factionManager = factionManager;
        this.dtrRegenManager = dtrRegenManager;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        String factionName = args[0];
        String secondsInput = args[1];

        Faction faction = factionManager.getFaction(factionName);

        if (faction == null) return errorMessage(languageManager.get(Message.ERROR_FACTION_NOT_FOUND)
                .replace(Placeholder.INPUT, factionName).toString());

        try {
            int seconds = Integer.parseInt(secondsInput);
            dtrRegenManager.freezeDtr(faction, seconds);

            return successMessage(languageManager.get(Message.SUCCESS_ADMIN_FREEZE_SET)
                    .replace(Placeholder.FACTION, faction.getDisplayName())
                    .replace(Placeholder.TIME, TimeFormat.wordsTime(seconds)).toString());
        } catch (NumberFormatException e) {
            return errorMessage(CommandError.NOT_A_NUMBER, secondsInput);
        }
    }

}
