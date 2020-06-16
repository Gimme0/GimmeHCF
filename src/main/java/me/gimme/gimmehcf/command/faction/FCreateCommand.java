package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Creates a new faction with the player as the leader.
 */
public class FCreateCommand extends FCommand {

    private FileConfiguration config;

    public FCreateCommand(FactionManager factionManager, LanguageManager languageManager, FileConfiguration config) {
        super("create", factionManager, languageManager);
        this.aliases.add("new");
        this.argsUsage = "<faction name>";
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_CREATE).toString();

        this.config = config;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String factionName = args[0];

        // Validation
        if (factionManager.getFaction(player) != null) return errorMessage(languageManager.get(Message.ERROR_ALREADY_IN_FACTION).toString());
        int nameMin = config.getInt(Config.FACTION_NAME_MIN.getPath());
        int nameMax = config.getInt(Config.FACTION_NAME_MAX.getPath());
        String nameAllowed = config.getString(Config.FACTION_NAME_CHARACTERS.getPath());
        List<String> illegalPhrases = config.getStringList(Config.FACTION_NAME_BLACKLIST.getPath());
        if (factionName.length() < nameMin) {
            return errorMessage(languageManager.get(Message.ERROR_NAME_TOO_SHORT)
                    .replace(Placeholder.N, String.valueOf(nameMin)).toString());
        } else if (factionName.length() > nameMax) {
            return errorMessage(languageManager.get(Message.ERROR_NAME_TOO_LONG)
                    .replace(Placeholder.N, String.valueOf(nameMax)).toString());
        }
        if (nameAllowed != null && !Pattern.compile(nameAllowed).matcher(factionName).matches()) {
            return errorMessage(CommandError.ILLEGAL_CHARACTERS, factionName);
        }
        for (String phrase : illegalPhrases) {
            int length = phrase.length();
            if (factionName.length() >= length && factionName.substring(0, length - 1).equalsIgnoreCase(phrase))
                return errorMessage(languageManager.get(Message.ERROR_NAME_ILLEGAL_PHRASE)
                        .replace(Placeholder.INPUT, factionName).toString());
        }

        if (factionManager.createFaction(factionName, (Player) sender))
            return successMessage(languageManager.get(Message.SUCCESS_CREATED_FACTION).toString());
        return errorMessage(languageManager.get(Message.ERROR_NAME_TAKEN).toString());
    }

}
