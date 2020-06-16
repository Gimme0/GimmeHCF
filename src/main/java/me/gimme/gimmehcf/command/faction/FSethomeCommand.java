package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Land;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Sets the player's faction's home.
 */
public class FSethomeCommand extends FCommand {

    public FSethomeCommand(FactionManager factionManager, LanguageManager languageManager) {
        super("sethome", factionManager, languageManager);
        this.maxArgs = 0;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_SETHOME).toString();
        this.factionPermission = FactionPermission.OFFICER;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;

        Set<Land> ownedLand = faction.getLandByWorld().get(player.getWorld().getUID());
        if (ownedLand == null || !ownedLand.contains(new Land(player.getLocation())))
            return errorMessage(languageManager.get(Message.ERROR_NOT_YOUR_LAND).toString());

        faction.setHome(player.getLocation());
        return successMessage(languageManager.get(Message.SUCCESS_FACTION_HOME_SET).toString());
    }

}
