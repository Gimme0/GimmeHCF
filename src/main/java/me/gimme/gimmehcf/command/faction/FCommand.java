package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.GimmeHCF;
import me.gimme.gimmehcf.command.BaseCommand;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for faction commands.
 */
abstract class FCommand extends BaseCommand {

    protected enum FactionPermission {
        NONE,
        MEMBER,
        OFFICER,
        LEADER
    }

    protected final FactionManager factionManager;
    protected final LanguageManager languageManager;
    protected FactionPermission factionPermission = FactionPermission.NONE;

    protected FCommand(@NotNull String name, @NotNull FactionManager factionManager, @NotNull LanguageManager languageManager) {
        super(GimmeHCF.COMMAND_FACTION, name);
        this.factionManager = factionManager;
        this.languageManager = languageManager;
    }

    @Override
    protected boolean isPermitted(@NotNull CommandSender sender) {
        if (factionPermission.equals(FactionPermission.NONE)) return super.isPermitted(sender);
        if (playerOnly && !(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        if (faction == null) return false;
        if (factionPermission.equals(FactionPermission.OFFICER) && !faction.isOfficer(player.getUniqueId()))
            return false;
        if (factionPermission.equals(FactionPermission.LEADER) && !faction.isLeader(player.getUniqueId()))
            return false;

        return super.isPermitted(sender);
    }

}
