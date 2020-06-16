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

public class FOwnerCommand extends FCommand {

    private Server server;

    public FOwnerCommand(FactionManager factionManager, LanguageManager languageManager, Server server) {
        super("owner", factionManager, languageManager);
        this.aliases.add("leader");
        this.argsUsage = "<player>";
        this.argsAlternatives.add(ArgPlaceholder.PLAYERS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_OWNER).toString();
        this.factionPermission = FactionPermission.LEADER;

        this.server = server;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;

        UUID promoteMember = null;
        String memberName = null;
        for (UUID id : faction.getPlayers()) {
            memberName = server.getOfflinePlayer(id).getName();
            if (memberName != null && memberName.equalsIgnoreCase(args[0])) {
                promoteMember = id;
                break;
            }
        }
        if (promoteMember == null) return errorMessage(languageManager.get(Message.ERROR_NOT_IN_YOUR_FACTION)
                .replace(Placeholder.INPUT, args[0]).toString());
        if (faction.isLeader(promoteMember))
            return errorMessage(languageManager.get(Message.ERROR_PROMOTE_ALREADY_OWNER).toString());

        faction.promoteToLeader(promoteMember);
        return successMessage(languageManager.get(Message.SUCCESS_MADE_OWNER)
                .replace(Placeholder.PLAYER, memberName).toString());
    }
}
