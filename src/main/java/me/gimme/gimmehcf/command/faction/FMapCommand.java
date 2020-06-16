package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Land;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Shows a map of surrounding factions.
 */
public class FMapCommand extends FCommand {

    private static final int MAP_WIDTH = 39;
    private static final int MAP_HEIGHT = 8;

    private int currentFactionMarkerIndex = 0;
    private Map<String, String> markerByFaction = new HashMap<>();

    public FMapCommand(@NotNull FactionManager factionManager, @NotNull LanguageManager languageManager) {
        super("map", factionManager, languageManager);
        this.argsUsage = "[on/off=once]]";
        this.argsAlternatives.add("on");
        this.argsAlternatives.add("off");
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_MAP).toString();
    }

    @Override
    protected String execute(@NotNull CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0 || args[0].equals("once")) {
            return getFormattedMessage(player);
        } else if (args[0].equals("on")) {
            return errorMessage("Not implemented yet"); //TODO
        } else if (args[0].equals("off")) {
            return errorMessage("Not implemented yet"); //TODO
        }

        return errorMessageWithUsage(CommandError.INVALID_ARGUMENT, args[0]);
    }

    private String getFormattedMessage(Player player) {
        String header = getHeader(player);
        String content = getContent(player);
        String footer = getFooter();
        currentFactionMarkerIndex = 0;
        markerByFaction.clear();

        return header + content + (footer.isEmpty() ? "" : (newLine + footer));
    }

    private String getHeader(Player player) {
        Land land = new Land(player.getLocation());
        int x = land.x;
        int z = land.z;
        Faction landOwner = factionManager.getFaction(player.getLocation());
        return languageManager.get(Message.HEADER_F_MAP)
                .replace(Placeholder.COORDINATES, x + "," + z)
                .replace(Placeholder.FACTION, factionManager.getDisplayName(landOwner, player)).toString();
    }

    private String getContent(Player player) {
        StringBuilder sb = new StringBuilder();

        int rowStart = -MAP_WIDTH / 2;
        int rowEnd = -rowStart;
        int colStart = -MAP_HEIGHT / 2;
        int colEnd = -colStart;
        for (int row = colStart; row < colEnd; row++) {
            sb.append(newLine);
            for (int col = rowStart; col < rowEnd; col++) {
                if (col < rowStart + 3 && row < colStart + 3) { // Compass
                    sb.append(getCompass(player, col - rowStart, row - colStart));
                } else {
                    sb.append(getMarker(player, col, row));
                }
            }
        }

        return sb.toString();
    }

    private String getCompass(Player player, int col, int row) {
        if (col != 0) return "";

        float r = -player.getLocation().getYaw();
        String NW = ((202.5 < r && r <= 247.5) ? ChatColor.RED : ChatColor.YELLOW) + "\\";
        String N = ((157.5 < r && r <= 202.5) ? ChatColor.RED : ChatColor.YELLOW) + "N";
        String NE = ((112.5 < r && r <= 157.5) ? ChatColor.RED : ChatColor.YELLOW) + "/";
        String W = ((247.5 < r && r <= 292.5) ? ChatColor.RED : ChatColor.YELLOW) + "W";
        String E = ((67.5 < r && r <= 112.5) ? ChatColor.RED : ChatColor.YELLOW) + "E";
        String SW = ((292.5 < r && r <= 337.5) ? ChatColor.RED : ChatColor.YELLOW) + "/";
        String S = ((337.5 < r || r <= 22.5) ? ChatColor.RED : ChatColor.YELLOW) + "S";
        String SE = ((22.5 < r && r <= 67.5) ? ChatColor.RED : ChatColor.YELLOW) + "\\";
        String mid = ChatColor.YELLOW + "+";

        switch (row) {
            case 0:
                return NW + N + NE;
            case 1:
                return W + mid + E;
            case 2:
                return SW + S + SE;
        }

        return "";
    }

    private String getMarker(Player player, int relativeX, int relativeZ) {
        if (relativeX == 0 && relativeZ == 0) return languageManager.get(Message.PLAYER_MARKER).toString();
        World world = player.getWorld();
        Land land = new Land(player.getLocation());
        land.x += relativeX;
        land.z += relativeZ;

        Faction landOwner = factionManager.getFaction(world, land);

        String mapMarker = landOwner.getMapMarker();
        if (mapMarker != null) return mapMarker;

        mapMarker = markerByFaction.get(landOwner.getName());
        if (mapMarker == null) {
            mapMarker = factionManager.getDisplayColor(landOwner, player) + getNextMarker();
            markerByFaction.put(landOwner.getName(), mapMarker);
        }
        return mapMarker;
    }

    private String getNextMarker() {
        String[] markers = languageManager.get(Message.FACTION_MARKERS).toString()
                .replaceAll("\\s","")
                .split(",");
        int index = currentFactionMarkerIndex++;
        index = index % markers.length;
        return markers[index];
    }

    private String getFooter() {
        StringBuilder sb = new StringBuilder();

        for (String factionName : markerByFaction.keySet()) {
            if (!sb.toString().isEmpty()) sb.append(ChatColor.RESET).append(", ");
            sb.append(markerByFaction.get(factionName)).append(": ").append(factionName);
        }

        return sb.toString();
    }
}
