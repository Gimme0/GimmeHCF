package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class FACreatesysCommand extends FACommand {

    private FactionManager factionManager;

    public FACreatesysCommand(FactionManager factionManager, LanguageManager languageManager) {
        super("createsys", languageManager);
        this.aliases.add("newsys");
        this.argsUsage = "[color=default] <faction name>";
        this.argsAlternatives.addAll(Arrays.asList("default",
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"));
        this.minArgs = 1;
        this.maxArgs = 2;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_FA_CREATESYS).toString();

        this.factionManager = factionManager;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        String factionName = args[args.length - 1];
        ChatColor color = null;

        if (args.length == 2 && !args[0].equals("default")) {
            ChatColor chatColor = ChatColor.getByChar(args[0]);
            if (chatColor == null)
                return errorMessageWithUsage(CommandError.NOT_A_COLOR, args[0]);
        }

        if (factionManager.createSystemFaction(factionName, color))
            return successMessage(languageManager.get(Message.SUCCESS_CREATED_FACTION).toString());
        return errorMessage(languageManager.get(Message.ERROR_NAME_TAKEN).toString());
    }

}
