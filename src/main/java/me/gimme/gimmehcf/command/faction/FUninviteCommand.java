package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.gimme.gimmehcf.command.ArgPlaceholder.PLAYERS;

/**
 * Uninvites other players from the player's faction.
 */
public class FUninviteCommand extends FCommand {

    public FUninviteCommand(FactionManager factionManager, LanguageManager languageManager) {
        super("uninvite", factionManager, languageManager);
        this.aliases.add("uninv");
        this.argsUsage = "<player/all>";
        this.argsAlternatives.add("all");
        this.argsAlternatives.add(PLAYERS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_UNINVITE).toString();
        this.factionPermission = FactionPermission.OFFICER;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;
        String uninviteeName = args[0];

        if (faction.removeInvite(uninviteeName.toLowerCase()))
            return successMessage(languageManager.get(Message.SUCCESS_UNINVITED_ONE)
                    .replace(Placeholder.PLAYER, uninviteeName).toString());
        else if (args[0].equals("all")) {
            return successMessage(languageManager.get(Message.SUCCESS_UNINVITED_ALL)
                    .replace(Placeholder.N, String.valueOf(faction.removeAllInvites())).toString());
        }
        return errorMessage(languageManager.get(Message.ERROR_PLAYER_WAS_NOT_INVITED).toString());
    }

}
