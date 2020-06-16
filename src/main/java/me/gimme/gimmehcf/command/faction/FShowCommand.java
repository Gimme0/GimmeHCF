package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.DtrRegenManager;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import me.gimme.gimmehcf.util.DtrFormat;
import me.gimme.gimmehcf.util.TimeFormat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.geom.Point2D;
import java.util.UUID;

import static me.gimme.gimmehcf.command.ArgPlaceholder.FACTIONS;

/**
 * Shows information of a faction.
 */
public class FShowCommand extends FCommand {

    private static final ChatColor COLOR_PRIMARY = ChatColor.GOLD;
    private static final ChatColor COLOR_SECONDARY = ChatColor.YELLOW;

    private DtrRegenManager dtrRegenManager;
    private FileConfiguration config;
    private Server server;

    public FShowCommand(@NotNull FactionManager factionManager, @NotNull LanguageManager languageManager,
                        @NotNull DtrRegenManager dtrRegenManager, @NotNull FileConfiguration config, @NotNull Server server) {
        super("show", factionManager, languageManager);
        this.aliases.add("info");
        this.aliases.add("who");
        this.argsUsage = "[faction=you]";
        this.argsAlternatives.add(FACTIONS.getPlaceholder());
        this.minArgs = 0;
        this.maxArgs = 1;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_F_SHOW).toString();

        this.dtrRegenManager = dtrRegenManager;
        this.config = config;
        this.server = server;
    }

    @Override
    protected String execute(@NotNull CommandSender sender, String[] args) {
        Faction faction;
        if (args.length == 0) {
            if (!(sender instanceof Player))
                return errorMessageWithUsage(CommandError.TOO_FEW_ARGUMENTS, null);
            faction = factionManager.getFaction((Player) sender);
            if (faction == null) return errorMessageWithUsage(CommandError.TOO_FEW_ARGUMENTS, null);
        } else {
            faction = factionManager.getFaction(args[0]);
            if (faction == null) return errorMessage(languageManager.get(Message.ERROR_FACTION_NOT_FOUND)
                    .replace(Placeholder.INPUT, args[0]).toString());
        }

        return getFactionInfo(faction, sender);
    }

    @NotNull
    private String getFactionInfo(@NotNull Faction faction, @NotNull CommandSender receiver) {
        Player player = (receiver instanceof Player) ? (Player) receiver : null;
        StringBuilder sb = new StringBuilder();
        if (receiver instanceof ConsoleCommandSender) sb.append(newLine);

        sb.append(languageManager.get(Message.HEADER_F_SHOW)
                .replace(Placeholder.FACTION, factionManager.getDisplayName(faction, player)).toString());

        String dtr = DtrFormat.oneDecimal(faction.getDtr()); // Formatted with 1 decimal rounded up
        String maxDtr = DtrFormat.oneDecimal(factionManager.getMaxDtr(faction));
        boolean infDtr = factionManager.getLandFlag(Flag.SYSTEM, faction);
        sb.append(newLine).append(title("DTR"))
                .append(infDtr ? "-" : (dtr + "/" + maxDtr))
                .append(faction.isRaidable() ? ChatColor.RED + " (RAIDABLE)" : "");

        long frozenSeconds = dtrRegenManager.getFrozenSeconds(faction);
        if (frozenSeconds > 0) {
            sb.append(newLine).append(title("DTR Freeze"))
                    .append(TimeFormat.wordsTime(frozenSeconds));
        }

        int claimedLand = faction.getClaimedLand();
        int maxLand = factionManager.getMaxLand(faction);
        boolean overclaimed = factionManager.isOverclaimed(faction);
        sb.append(newLine).append(title("Land"))
                .append(claimedLand).append("/").append(maxLand == -1 ? "-" : maxLand)
                .append(overclaimed ? ChatColor.RED + " (OVERCLAIMED)" : "");

        sb.append(newLine).append(title("Faction Home"));
        World world = player == null ? server.getWorlds().get(0) : player.getWorld();
        Point2D center = faction.getCenter(world.getUID());
        if (faction.getHome() != null) {
            Block block = faction.getHome().getBlock();
            sb.append("x: ").append(block.getX()).append(",  y: ").append(block.getY()).append(",  z: ").append(block.getZ());
        } else if (center != null) {
            sb.append("x: ").append(Math.round(center.getX())).append(",  z: ").append(Math.round(center.getY()));
        } else {
            sb.append("-");
        }

        String allies = getAllies(faction, player);
        if (!allies.isEmpty()) {
            sb.append(newLine).append(title("Allies"))
                    .append(allies);
        }

        sb.append(newLine).append(title("Online(" + getNumOfOnlineMembers(faction) + ")"))
                .append(getMembers(faction, true));
        sb.append(newLine).append(title("Offline(" + (faction.getNumberOfPlayers() - getNumOfOnlineMembers(faction)) + ")"))
                .append(ChatColor.GRAY).append(getMembers(faction, false));

        return sb.toString();
    }

    @NotNull
    private String title(@NotNull String title) {
        return COLOR_PRIMARY + title + ": " + COLOR_SECONDARY;
    }

    private int getNumOfOnlineMembers(@NotNull Faction faction) {
        int result = 0;
        for (UUID playerId : faction.getPlayers()) {
            Player player = server.getPlayer(playerId);
            if (player != null && player.isOnline()) result++;
        }
        return result;
    }

    @NotNull
    private String getMembers(@NotNull Faction faction, boolean online) {
        StringBuilder sb = new StringBuilder();

        for (UUID playerId : faction.getPlayers()) {
            OfflinePlayer player;
            if (online) {
                player = server.getPlayer(playerId);
                if (player == null || !player.isOnline()) continue;
            } else {
                player = server.getOfflinePlayer(playerId);
                if (player.isOnline()) continue;
            }
            if (sb.length() != 0) sb.append(", ");
            if (faction.isLeader(playerId)) sb.append(config.getString(Config.FACTION_PREFIX_LEADER.getPath()));
            else if (faction.isOfficer(playerId)) sb.append(config.getString(Config.FACTION_PREFIX_OFFICER.getPath()));
            sb.append(player.getName());
        }

        return sb.toString();
    }

    @NotNull
    private String getAllies(@NotNull Faction faction, @Nullable Player displayedTo) {
        StringBuilder sb = new StringBuilder();

        for (String allyName : faction.getAllies()) {
            Faction ally = factionManager.getFaction(allyName);
            if (ally == null || !ally.getAllies().contains(faction.getId())) continue;

            if (sb.length() != 0) sb.append(", ");
            sb.append(factionManager.getDisplayName(ally, displayedTo));
        }

        return sb.toString();
    }

}
