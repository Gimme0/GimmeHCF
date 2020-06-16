package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.teleport.TeleportManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Teleports the player to the faction home.
 */
public class FHomeCommand extends FCommand {

    private TeleportManager teleportManager;

    public FHomeCommand(FactionManager factionManager, LanguageManager languageManager, TeleportManager teleportManager) {
        super("home", factionManager, languageManager);
        this.maxArgs = 0;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_HOME).toString();
        this.factionPermission = FactionPermission.MEMBER;

        this.teleportManager = teleportManager;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;

        if (faction.getHome() == null) return errorMessage(languageManager.get(Message.ERROR_NO_HOME_SET).toString());

        teleportManager.teleport(player, faction.getHome());
        return null;
    }

}
