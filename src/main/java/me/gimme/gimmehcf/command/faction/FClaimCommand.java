package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import me.gimme.gimmehcf.faction.Land;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Claims chunks to the faction. "One" (default) is to only claim the current chunk, and "auto" is a toggle that claims
 * every chunk that the player enters until switched off or the faction's capacity is reached.
 */
public class FClaimCommand extends FCommand {

    private FileConfiguration config;

    public FClaimCommand(FactionManager factionManager, LanguageManager languageManager, FileConfiguration config) {
        super("claim", factionManager, languageManager);
        this.argsUsage = "[one/auto]";
        this.argsAlternatives.add("one");
        this.argsAlternatives.add("auto"); //TODO
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_CLAIM).toString();
        this.factionPermission = FactionPermission.OFFICER;

        this.config = config;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();
        Faction faction = factionManager.getFaction(player);
        assert faction != null;

        if (args.length == 0 || args[0].equals("one")) {

            Set<Land> factionClaims = faction.getLandByWorld().get(player.getWorld().getUID());
            if (factionClaims != null && factionClaims.contains(new Land(location))) return null;

            Faction locationFaction = factionManager.getFaction(location);
            if (factionManager.isOverclaimed(locationFaction) && factionManager.unclaimLand(locationFaction, location))
                return successMessage(languageManager.get(Message.SUCCESS_OVERCLAIMED_ONE)
                        .replace(Placeholder.FACTION, factionManager.getDisplayName(locationFaction, player)).toString());

            boolean claimable = factionManager.getLandFlag(Flag.CLAIM, location);
            if (claimable) claimable = isFactionGapSufficient(player);
            if (!claimable) return errorMessage(languageManager.get(Message.ERROR_CLAIM_PERMISSION).toString());

            if (config.getBoolean(Config.FACTION_LAND_ADJACENT.getPath()) && !isAdjacent(new Land(location),
                    faction.getLandByWorld().get(player.getWorld().getUID())))
                return errorMessage(languageManager.get(Message.ERROR_CLAIM_NOT_ADJACENT).toString());

            if (faction.getClaimedLand() >= factionManager.getMaxLand(faction))
                return errorMessage(languageManager.get(Message.ERROR_CLAIM_MAX_REACHED).toString());

            if (factionManager.claimLand(faction, location))
                return successMessage(languageManager.get(Message.SUCCESS_CLAIMED_ONE).toString());
            return null;

        } else if (args[0].equals("auto")) {

            return errorMessage("Not implemented yet (WIP)"); //"Auto-claiming: ON (use the same command again to switch OFF)"; //TODO

        }

        return errorMessageWithUsage(CommandError.INVALID_ARGUMENT, args[0]);
    }

    private boolean isAdjacent(Land land, Set<Land> landSet) {
        if (landSet == null || landSet.isEmpty()) return true;

        for (Land adjacent : Land.getAdjacent(land)) {
            if (landSet.contains(adjacent)) return true;
        }
        return false;
    }

    private boolean isFactionGapSufficient(Player player) {
        int gap = config.getInt(Config.FACTION_LAND_GAP.getPath());
        if (gap <= 0) return true;
        boolean systemFactionsIncluded = config.getBoolean(Config.FACTION_LAND_GAP_SYSTEM_INCLUDED.getPath());
        Faction playerFaction = factionManager.getFaction(player);
        World world = player.getWorld();
        Land playerLand = new Land(player.getLocation());

        Land loopLand = new Land();
        for (int x = -gap; x <= gap; x++) {
            loopLand.x = playerLand.x + x;
            for (int z = -gap; z <= gap; z++) {
                loopLand.z = playerLand.z + z;
                Faction faction = factionManager.getFaction(world, loopLand);

                if (faction == playerFaction) continue;
                if (factionManager.getLandFlag(Flag.CLAIM, faction)) continue;
                if (!systemFactionsIncluded && factionManager.getLandFlag(Flag.SYSTEM, faction)) continue;
                return false;
            }
        }
        return true;
    }

}
