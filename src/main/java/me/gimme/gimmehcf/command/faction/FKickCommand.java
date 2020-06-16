package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.hooks.GimmeBalanceHook;
import me.gimme.gimmehcf.events.PlayerLeaveFactionEvent;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FKickCommand extends FCommand {

    private GimmeBalanceHook gimmeBalanceHook;
    private Server server;

    public FKickCommand(FactionManager factionManager, LanguageManager languageManager, GimmeBalanceHook gimmeBalanceHook,
                        Server server) {
        super("kick", factionManager, languageManager);
        this.argsUsage = "<player>";
        this.argsAlternatives.add(ArgPlaceholder.PLAYERS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_KICK).toString();
        this.factionPermission = FactionPermission.OFFICER;

        this.gimmeBalanceHook = gimmeBalanceHook;
        this.server = server;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;

        UUID kickPlayer = null;
        String memberName = null;
        for (UUID id : faction.getPlayers()) {
            memberName = server.getOfflinePlayer(id).getName();
            if (memberName != null && memberName.equalsIgnoreCase(args[0])) {
                kickPlayer = id;
                break;
            }
        }
        if (kickPlayer == null) return errorMessage(languageManager.get(Message.ERROR_NOT_IN_YOUR_FACTION)
                .replace(Placeholder.INPUT, args[0]).toString());

        if (!faction.isLeader(player.getUniqueId()) && faction.isOfficer(kickPlayer))
            return errorMessage(CommandError.NO_PERMISSION, null);

        if (gimmeBalanceHook.isInCombat(kickPlayer))
            return errorMessage(languageManager.get(Message.ERROR_THEY_IN_COMBAT).toString());

        factionManager.removePlayerFromFaction(faction, kickPlayer, PlayerLeaveFactionEvent.Reason.KICK);
        return successMessage(languageManager.get(Message.SUCCESS_KICKED_PLAYER)
                .replace(Placeholder.PLAYER, memberName).toString());
    }
}
