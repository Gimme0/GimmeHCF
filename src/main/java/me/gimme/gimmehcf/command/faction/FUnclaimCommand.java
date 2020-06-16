package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Land;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

/**
 * Unclaims land for the player's faction.
 */
public class FUnclaimCommand extends FCommand {

    private FileConfiguration config;

    public FUnclaimCommand(FactionManager factionManager, LanguageManager languageManager, FileConfiguration config) {
        super("unclaim", factionManager, languageManager);
        this.argsUsage = "[one/all]";
        this.argsAlternatives.add("one");
        this.argsAlternatives.add("all");
        this.minArgs = 0;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_UNCLAIM).toString();
        this.factionPermission = FactionPermission.LEADER;

        this.config = config;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();
        Faction faction = factionManager.getFaction(player);
        assert faction != null;

        if (!config.getBoolean(Config.FACTION_PLAYER_UNCLAIM_RAIDABLE.getPath()) && faction.isRaidable())
            return errorMessage(languageManager.get(Message.ERROR_FACTION_RAIDABLE).toString());

        if (args.length == 0 || args[0].equals("one")) {

            Set<Land> factionClaims = faction.getLandByWorld().get(player.getWorld().getUID());
            if (factionClaims == null || !factionClaims.contains(new Land(location))) return null;

            if (config.getBoolean(Config.FACTION_LAND_ADJACENT.getPath()) && !checkAdjacent(new Land(location),
                    faction.getLandByWorld().get(player.getWorld().getUID())))
                return errorMessage(languageManager.get(Message.ERROR_WOULD_BE_NON_ADJACENT).toString());

            if (factionManager.unclaimLand(faction, location))
                return successMessage(languageManager.get(Message.SUCCESS_UNCLAIMED_ONE).toString());
            return null;

        } else if (args[0].equals("all")) {

            int amountUnclaimed = 0;
            for (UUID world : faction.getLandByWorld().keySet()) {
                for (Land land : faction.getLandByWorld().get(world)) {
                    factionManager.unclaimLand(faction, world, land);
                    amountUnclaimed++;
                }
            }
            return successMessage(languageManager.get(Message.SUCCESS_UNCLAIMED_ALL)
                    .replace(Placeholder.N, String.valueOf(amountUnclaimed)).toString());

        } else if (args[0].equals("auto")) {

            return errorMessage("Not implemented yet (WIP)"); //"Auto-claiming: ON (use the same command again to switch OFF)"; //TODO

        }

        return errorMessageWithUsage(CommandError.INVALID_ARGUMENT, args[0]);
    }

    /**
     * Checks if the claims will remain adjacent after unclaiming the specified land.
     *
     * @param land    the land about to be unclaimed
     * @param landSet the land to check if adjacent
     * @return if the claims will remain adjacent after unclaiming the specified land
     */
    private boolean checkAdjacent(Land land, Set<Land> landSet) {
        if (landSet == null || landSet.size() <= 1) return true;

        for (Land adjacent : Land.getAdjacent(land)) {
            if (landSet.contains(adjacent)) {
                boolean hasAdjacent = false;
                for (Land adj : Land.getAdjacent(adjacent)) {
                   if (adj != adjacent && landSet.contains(adj)) {
                       hasAdjacent = true;
                   }
                }
                if (!hasAdjacent) return false;
            }
        }
        return true;
    }
}
