package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import me.gimme.gimmehcf.faction.SysFaction;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.command.CommandSender;

public class FASetflagCommand extends FACommand {

    private FactionManager factionManager;

    public FASetflagCommand(FactionManager factionManager, LanguageManager languageManager) {
        super("setflag", languageManager);
        this.aliases.add("flag");
        this.argsUsage = "<faction> <flag> <true|false|default>";
        for (Flag f : Flag.values()) {
            this.argsAlternatives.add(ArgPlaceholder.SYSTEM_FACTIONS.getPlaceholder() + " " + f.toString() + " true");
            this.argsAlternatives.add(ArgPlaceholder.SYSTEM_FACTIONS.getPlaceholder() + " " + f.toString() + " false");
            this.argsAlternatives.add(ArgPlaceholder.SYSTEM_FACTIONS.getPlaceholder() + " " + f.toString() + " default");
        }
        this.minArgs = 3;
        this.maxArgs = 3;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_FA_SETFLAG).toString();

        this.factionManager = factionManager;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        String factionName = args[0];
        Flag flag = Flag.getByString(args[1]);
        String value = args[2];
        SysFaction faction = factionManager.getSystemFaction(factionName);

        if (flag == null) return errorMessage(languageManager.get(Message.ERROR_INVALID_FLAG)
                .replace(Placeholder.INPUT, value).toString());
        if (faction == null) return errorMessage(languageManager.get(Message.ERROR_SYSFACTION_NOT_FOUND)
                .replace(Placeholder.INPUT, factionName).toString());

        if (value.equals("true")) {

            faction.setFlag(flag, true);
            return successMessage(flag.toString() + ": true");

        } else if (value.equals("false")) {

            faction.setFlag(flag, false);
            return successMessage(flag.toString() + ": false");

        } else if (value.equals("default")) {

            faction.clearFlag(flag);
            return successMessage(flag.toString() + ": default");

        }
        return errorMessage(CommandError.INVALID_ARGUMENT, value);
    }

}
