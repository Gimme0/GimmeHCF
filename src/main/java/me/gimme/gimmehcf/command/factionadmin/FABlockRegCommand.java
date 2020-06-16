package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class FABlockRegCommand extends FACommand {

    private FactionManager factionManager;

    public FABlockRegCommand(FactionManager factionManager, LanguageManager languageManager) {
        super("blockreg", languageManager);
        this.argsUsage = "<faction> <material> <seconds>";
        this.argsAlternatives.add(ArgPlaceholder.FACTIONS.getPlaceholder() + " " + ArgPlaceholder.MATERIAL + " 300");
        this.minArgs = 3;
        this.maxArgs = 3;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_FA_BLOCKREG).toString();

        this.factionManager = factionManager;
    }

    @Override
    protected String execute(@NotNull CommandSender sender, String[] args) {
        String factionArg = args[0];
        String materialArg = args[1];
        String secondsArg = args[2];

        Faction faction = factionManager.getFaction(factionArg);

        if (faction == null) return errorMessage(languageManager.get(Message.ERROR_FACTION_NOT_FOUND)
                .replace(Placeholder.INPUT, factionArg).toString());

        Material material = Material.matchMaterial(materialArg);
        if (material == null) return errorMessage(languageManager.get(Message.ERROR_MATERIAL_NOT_FOUND)
                .replace(Placeholder.INPUT, materialArg).toString());

        try {
            int delay = Integer.parseInt(secondsArg);
            faction.setBlockRegeneration(material, delay);

            if (delay < 0) {
                return successMessage(languageManager.get(Message.SUCCESS_ADMIN_BLOCKREG_RESET)
                        .replace(Placeholder.FACTION, faction.getDisplayName())
                        .replace(Placeholder.MATERIAL, material.getKey().getKey())
                        .toString());
            } else {
                return successMessage(languageManager.get(Message.SUCCESS_ADMIN_BLOCKREG_SET)
                        .replace(Placeholder.FACTION, faction.getDisplayName())
                        .replace(Placeholder.MATERIAL, material.getKey().getKey())
                        .replace(Placeholder.N, String.valueOf(delay))
                        .toString());
            }
        } catch (NumberFormatException e) {
            return errorMessageWithUsage(CommandError.NOT_A_NUMBER, secondsArg);
        }
    }
}
