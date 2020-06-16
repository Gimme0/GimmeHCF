package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.events.PlayerLeaveFactionEvent;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Makes the player leave their faction.
 */
public class FLeaveCommand extends FCommand {

    private FileConfiguration config;

    public FLeaveCommand(FactionManager factionManager, LanguageManager languageManager, FileConfiguration config) {
        super("leave", factionManager, languageManager);
        this.argsUsage = "<faction>";
        this.argsAlternatives.add(ArgPlaceholder.FACTIONS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_LEAVE).toString();
        this.factionPermission = FactionPermission.MEMBER;

        this.config = config;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;

        if (faction.isLeader(player.getUniqueId()))
            return errorMessage(languageManager.get(Message.ERROR_LEADER).toString());
        if (!config.getBoolean(Config.FACTION_PLAYER_LEAVE_RAIDABLE.getPath()) && faction.isRaidable())
            return errorMessage(languageManager.get(Message.ERROR_FACTION_RAIDABLE).toString());
        if (!config.getBoolean(Config.FACTION_PLAYER_LEAVE_WHILE_HOME.getPath()) &&
                faction == factionManager.getFaction(player.getLocation()))
            return errorMessage(languageManager.get(Message.ERROR_LEAVE_WHILE_HOME).toString());
        if (factionManager.getFaction(args[0]) != faction)
            return errorMessageWithUsage(languageManager.get(Message.ERROR_CURRENT_FACTION_MISMATCH)
                    .replace(Placeholder.INPUT, args[0]).toString());

        factionManager.removePlayerFromFaction(faction, player.getUniqueId(), PlayerLeaveFactionEvent.Reason.LEAVE);
        return successMessage(languageManager.get(Message.SUCCESS_LEFT_FACTION)
                .replace(Placeholder.FACTION, faction.getDisplayName()).toString());
    }

}
