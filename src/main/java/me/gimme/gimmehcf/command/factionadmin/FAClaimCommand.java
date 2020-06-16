package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Land;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class FAClaimCommand extends FACommand {

    private FactionManager factionManager;

    public FAClaimCommand(FactionManager factionManager, LanguageManager languageManager) {
        super("claim", languageManager);
        this.argsUsage = "[one|rectangle=one] <faction> (x,z) (x,z)";
        this.argsAlternatives.add("one " + ArgPlaceholder.FACTIONS.getPlaceholder());
        this.argsAlternatives.add("rectangle " + ArgPlaceholder.FACTIONS.getPlaceholder() + " 0,0");
        this.argsAlternatives.add("rectangle " + ArgPlaceholder.FACTIONS.getPlaceholder() + " " +
                ArgPlaceholder.WILDCARD.getPlaceholder() + " 0,0");
        this.minArgs = 1;
        this.maxArgs = 4;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_FA_CLAIM).toString();

        this.factionManager = factionManager;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Location playerLocation = ((Player) sender).getLocation();

        String arg1 = "one";
        String factionName;
        String coords1 = null;
        String coords2 = null;

        if (args.length == 1) {
            factionName = args[0];
        } else if (args.length >= 2) {
            arg1 = args[0];
            factionName = args[1];
            if (args.length >= 3) {
                coords1 = args[2];
                if (args.length == 4) {
                    coords2 = args[3];
                }
            }
        } else {
            return errorMessageWithUsage(CommandError.TOO_FEW_ARGUMENTS, null);
        }

        Land land1 = getLand(coords1);
        Land land2 = getLand(coords2);
        Faction faction = factionManager.getFaction(factionName);

        if (arg1.equals("one")) {

            if (args.length > 3) return errorMessageWithUsage(CommandError.TOO_MANY_ARGUMENTS, null);
            if (coords1 == null) {
                land1 = new Land(playerLocation);
            } else if (land1 == null) {
                return errorMessageWithUsage(CommandError.INVALID_ARGUMENT, coords1);
            }
            if (faction == null) return errorMessage(languageManager.get(Message.ERROR_FACTION_NOT_FOUND)
                    .replace(Placeholder.INPUT, factionName).toString());

            if (factionManager.claimLand(faction, playerLocation))
                return successMessage(languageManager.get(Message.SUCCESS_ADMIN_CLAIMED_ONE)
                        .replace(Placeholder.FACTION, faction.getDisplayName()).toString());
            return null;


        } else if (arg1.equals("rectangle")) {

            if (args.length > 4) return errorMessageWithUsage(CommandError.TOO_MANY_ARGUMENTS, null);
            if (coords2 == null || coords1 == null)
                return errorMessageWithUsage(CommandError.TOO_FEW_ARGUMENTS, null);
            if (land1 == null) return errorMessageWithUsage(CommandError.INVALID_ARGUMENT, coords1);
            if (land2 == null) return errorMessageWithUsage(CommandError.INVALID_ARGUMENT, coords2);
            if (faction == null) return errorMessage(languageManager.get(Message.ERROR_FACTION_NOT_FOUND)
                    .replace(Placeholder.INPUT, factionName).toString());

            int amountClaimed = 0;
            for (int i = Math.min(land1.x, land2.x); i <= Math.max(land1.x, land2.x); i++) {
                for (int j = Math.min(land1.z, land2.z); j <= Math.max(land1.z, land2.z); j++) {
                    if (factionManager.claimLand(faction, playerLocation.getWorld().getUID(), new Land(i, j)))
                        amountClaimed++;
                }
            }
            return successMessage(languageManager.get(Message.SUCCESS_ADMIN_CLAIMED_MULTIPLE)
                    .replace(Placeholder.N, String.valueOf(amountClaimed))
                    .replace(Placeholder.FACTION, faction.getDisplayName()).toString());

        }

        return errorMessageWithUsage(CommandError.INVALID_ARGUMENT, arg1);
    }

    @Nullable
    private Land getLand(String coords) {
        if (coords == null) return null;
        coords = coords.replaceAll("[<>()]", "").trim();
        String[] array = coords.split(",");
        if (array.length != 2) return null;
        try {
            return new Land(Integer.parseInt(array[0]), Integer.parseInt(array[1]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
