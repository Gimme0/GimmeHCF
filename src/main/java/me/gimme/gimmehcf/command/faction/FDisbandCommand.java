package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static me.gimme.gimmehcf.command.ArgPlaceholder.FACTIONS;

/**
 * Disbands the faction that the player is the leader of.
 */
public class FDisbandCommand extends FCommand {

    private FileConfiguration config;

    public FDisbandCommand(FactionManager factionManager, LanguageManager languageManager, FileConfiguration config) {
        super("disband", factionManager, languageManager);
        this.argsUsage = "<faction>";
        this.argsAlternatives.add(FACTIONS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_DISBAND).toString();
        this.factionPermission = FactionPermission.LEADER;

        this.config = config;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;

        if (!config.getBoolean(Config.FACTION_PLAYER_DISBAND_RAIDABLE.getPath()) && faction.isRaidable())
            return errorMessage(languageManager.get(Message.ERROR_FACTION_RAIDABLE).toString());
        if (factionManager.getFaction(args[0]) != faction)
            return errorMessageWithUsage(languageManager.get(Message.ERROR_CURRENT_FACTION_MISMATCH)
                    .replace(Placeholder.INPUT, args[0]).toString());

        factionManager.removeFaction(faction, player);
        return successMessage(languageManager.get(Message.SUCCESS_DISBANDED_FACTION)
                .replace(Placeholder.FACTION, faction.getName()).toString());
    }
}
