package me.gimme.gimmehcf.command.faction;

import com.google.common.base.Strings;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.gimme.gimmehcf.command.ArgPlaceholder.PLAYERS;

/**
 * Invites other players to the player's faction.
 */
public class FInviteCommand extends FCommand {

    private Server server;

    public FInviteCommand(FactionManager factionManager, LanguageManager languageManager, Server server) {
        super("invite", factionManager, languageManager);
        this.aliases.add("inv");
        this.argsUsage = "<player>";
        this.argsAlternatives.add(PLAYERS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_INVITE).toString();
        this.factionPermission = FactionPermission.OFFICER;

        this.server = server;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;
        String inviteeName = args[0];

        if (!faction.addInvite(inviteeName.toLowerCase()))
            return errorMessage(languageManager.get(Message.ERROR_ALREADY_INVITED).toString());

        Player invitedPlayer = server.getPlayer(inviteeName);
        if (invitedPlayer != null && invitedPlayer.isOnline()) {
            String message = languageManager.get(Message.INFO_YOU_HAVE_BEEN_INVITED)
                    .replace(Placeholder.FACTION, factionManager.getDisplayName(faction, invitedPlayer)).toString();
            if (!Strings.isNullOrEmpty(message)) invitedPlayer.sendMessage(message);
        }
        return successMessage(languageManager.get(Message.SUCCESS_INVITED_PLAYER)
                .replace(Placeholder.PLAYER, inviteeName).toString());
    }

}
