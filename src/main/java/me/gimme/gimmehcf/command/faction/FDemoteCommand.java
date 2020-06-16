package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FDemoteCommand extends FCommand {

    private Server server;

    public FDemoteCommand(FactionManager factionManager, LanguageManager languageManager, Server server) {
        super("demote", factionManager, languageManager);
        this.argsUsage = "<player>";
        this.argsAlternatives.add(ArgPlaceholder.PLAYERS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_DEMOTE).toString();
        this.factionPermission = FactionPermission.LEADER;

        this.server = server;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;

        UUID demoteMember = null;
        String memberName = null;
        for (UUID id : faction.getPlayers()) {
            memberName = server.getOfflinePlayer(id).getName();
            if (memberName != null && memberName.equalsIgnoreCase(args[0])) {
                demoteMember = id;
                break;
            }
        }
        if (demoteMember == null) return errorMessage(languageManager.get(Message.ERROR_NOT_IN_YOUR_FACTION)
                .replace(Placeholder.INPUT, args[0]).toString());
        if (!faction.isOfficer(demoteMember) || faction.isLeader(demoteMember))
            return errorMessage(languageManager.get(Message.ERROR_DEMOTE_NOT_OFFICER).toString());

        faction.demoteToMember(demoteMember);
        return successMessage(languageManager.get(Message.SUCCESS_DEMOTED_PLAYER)
                .replace(Placeholder.PLAYER, memberName).toString());
    }

}
