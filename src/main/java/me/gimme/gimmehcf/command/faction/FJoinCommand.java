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
 * Makes the player join a faction that they were invited to.
 */
public class FJoinCommand extends FCommand {

    private FileConfiguration config;

    public FJoinCommand(FactionManager factionManager, LanguageManager languageManager, FileConfiguration config) {
        super("join", factionManager, languageManager);
        this.argsUsage = "<faction>";
        this.argsAlternatives.add(FACTIONS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_JOIN).toString();

        this.config = config;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        Faction faction = factionManager.getFaction(args[0]);
        if (faction == null) return errorMessage(languageManager.get(Message.ERROR_FACTION_NOT_FOUND)
                .replace(Placeholder.INPUT, args[0]).toString());
        if (!faction.getInvites().contains(player.getName().toLowerCase()))
            return errorMessage(languageManager.get(Message.ERROR_YOU_ARE_NOT_INVITED).toString());
        int factionPlayerLimit = config.getInt(Config.FACTION_PLAYER_FACTION_LIMIT.getPath(), -1);
        if (factionPlayerLimit >= 0 && faction.getNumberOfPlayers() >= factionPlayerLimit)
            return errorMessage(languageManager.get(Message.ERROR_FACTION_FULL).toString());
        if (!config.getBoolean(Config.FACTION_PLAYER_JOIN_RAIDABLE.getPath()) && faction.isRaidable())
            return errorMessage(languageManager.get(Message.ERROR_FACTION_RAIDABLE).toString());
        if (factionManager.getFaction(player) != null)
            return errorMessage(languageManager.get(Message.ERROR_ALREADY_IN_FACTION).toString());

        factionManager.addPlayerToFaction(faction, player);
        return successMessage(languageManager.get(Message.SUCCESS_JOINED_FACTION)
                .replace(Placeholder.FACTION, faction.getDisplayName()).toString());
    }

}
