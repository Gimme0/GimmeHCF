package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmecore.util.ChatTableBuilder;
import me.gimme.gimmecore.util.Pageifier;
import me.gimme.gimmecore.util.TableBuilder;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.listeners.OnlineFactionsListener;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import me.gimme.gimmehcf.player.PlayerStats;
import me.gimme.gimmehcf.player.PlayerStatsManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.stream.Collectors;

public class FTopCommand extends FCommand {

    private static final int DEFAULT_LISTINGS_PER_PAGE = 8;
    private static final String ARG_KILLS = "kills";
    private static final String ARG_KD = "kd";
    private static final String ARG_PLAYERS = "players";

    private OnlineFactionsListener onlineFactionsListener;
    private PlayerStatsManager playerStatsManager;
    private Server server;

    public FTopCommand(FactionManager factionManager, LanguageManager languageManager,
                       OnlineFactionsListener onlineFactionsListener, PlayerStatsManager playerStatsManager, Server server) {
        super("top", factionManager, languageManager);
        this.argsUsage = "[kills/kd/players] [page=1]";
        this.argsAlternatives.add(ARG_KILLS);
        this.argsAlternatives.add(ARG_KD);
        this.argsAlternatives.add(ARG_PLAYERS);
        this.minArgs = 0;
        this.maxArgs = 2;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_F_TOP).toString();

        this.onlineFactionsListener = onlineFactionsListener;
        this.playerStatsManager = playerStatsManager;
        this.server = server;
    }

    @Override
    protected @Nullable String execute(CommandSender sender, String[] args) {
        String arg = args.length > 0 ? args[0] : ARG_KILLS;
        String pageInput = args.length > 1 ? args[1] : "1";
        int pageNumber;

        try {
            pageNumber = Integer.parseInt(pageInput);
        } catch (NumberFormatException e) {
            return errorMessageWithUsage(CommandError.NOT_A_NUMBER, pageInput);
        }
        int perPage = (sender instanceof ConsoleCommandSender) ? -1 : DEFAULT_LISTINGS_PER_PAGE;

        DecimalFormat kdFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
        kdFormat.setMaximumFractionDigits(2);
        kdFormat.setMinimumFractionDigits(2);
        kdFormat.setRoundingMode(RoundingMode.HALF_UP);

        if (arg.equals(ARG_KILLS) || arg.equals(ARG_KD)) {

            Message message = Message.HEADER_F_TOP_KILLS;
            if (arg.equals(ARG_KD)) message = Message.HEADER_F_TOP_KD;

            Comparator<? super Map.Entry<Faction, PlayerStats>> comparator = Comparator.comparingInt(e -> e.getValue().getKills());
            if (arg.equals(ARG_KD)) comparator = Comparator.comparingDouble(e -> e.getValue().getKD());

            TableBuilder.Alignment alignment2 = TableBuilder.Alignment.CENTER;
            if (arg.equals(ARG_KD)) alignment2 = TableBuilder.Alignment.LEFT;

            Map<Faction, PlayerStats> statsByFaction = new HashMap<>();

            for (Faction faction : factionManager.getFactions()) {
                PlayerStats factionStats = new PlayerStats();
                for (UUID player : faction.getPlayers()) {
                    PlayerStats playerStats = playerStatsManager.getStats(player);
                    if (playerStats == null) continue;
                    factionStats.setKills(factionStats.getKills() + playerStats.getKills());
                    factionStats.setDeaths(factionStats.getDeaths() + playerStats.getDeaths());
                }
                statsByFaction.put(faction, factionStats);
            }
            List<Faction> sortedFactions = statsByFaction.entrySet()
                    .stream()
                    .sorted(comparator)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            sortedFactions.removeAll(factionManager.getSystemFactions());

            Pageifier.PageResult<Faction> pageResult = Pageifier.getPage(sortedFactions, perPage, pageNumber);

            if (!(1 <= pageNumber && pageNumber <= pageResult.totalPages)) {
                return errorMessage(languageManager.get(Message.ERROR_PAGE_OOB)
                        .replace(Placeholder.N, pageResult.totalPages + "").toString());
            }

            TableBuilder tableBuilder = new ChatTableBuilder()
                    .setEllipsize(true)
                    .addCol(ChatTableBuilder.Alignment.LEFT, 0.25)
                    .addCol(alignment2, -0.45)
                    .addCol(ChatTableBuilder.Alignment.LEFT, 0.30);
            String[] header = languageManager.get(message)
                    .replace(Placeholder.PAGE, pageResult.page + "/" + pageResult.totalPages).toStringArray();
            if (!(header.length == 1 && header[0].length() == 0)) { // If header is not empty
                tableBuilder.addRow(header).addRow();
            }

            Player playerOrNull = (sender instanceof Player) ? (Player) sender : null;
            for (Faction faction : pageResult.content) {
                int onlinePlayers = onlineFactionsListener.getOnlinePlayers(faction);
                PlayerStats factionStats = statsByFaction.get(faction);
                String word1 = factionManager.getDisplayName(faction, playerOrNull);
                String word2 = (onlinePlayers == 0 ? ChatColor.GRAY : ChatColor.YELLOW) + "";
                if (arg.equals(ARG_KILLS)) word2 += factionStats.getKills();
                else
                    word2 += kdFormat.format(factionStats.getKD()) + " (" + factionStats.getKills() + "/" + factionStats.getDeaths() + ")";
                tableBuilder.addRow(word1, word2);
            }

            return tableBuilder.build();

        } else if (arg.equals(ARG_PLAYERS)) {

            List<UUID> sortedPlayers = playerStatsManager.getStats().entrySet().stream()
                    .sorted(Comparator.comparingDouble(e -> e.getValue().getKills()))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            Pageifier.PageResult<UUID> pageResult = Pageifier.getPage(sortedPlayers, perPage, pageNumber);

            if (!(1 <= pageNumber && pageNumber <= pageResult.totalPages)) {
                return errorMessage(languageManager.get(Message.ERROR_PAGE_OOB)
                        .replace(Placeholder.N, pageResult.totalPages + "").toString());
            }

            TableBuilder tableBuilder = new ChatTableBuilder()
                    .setEllipsize(true)
                    .addCol(ChatTableBuilder.Alignment.LEFT, 0.25)
                    .addCol(ChatTableBuilder.Alignment.LEFT, -0.45)
                    .addCol(ChatTableBuilder.Alignment.LEFT, 0.30);
            String[] header = languageManager.get(Message.HEADER_F_TOP_PLAYERS)
                    .replace(Placeholder.PAGE, pageResult.page + "/" + pageResult.totalPages).toStringArray();
            if (!(header.length == 1 && header[0].length() == 0)) { // If header is not empty
                tableBuilder.addRow(header).addRow();
            }

            for (UUID uuid : pageResult.content) {
                Player onlinePlayer = server.getPlayer(uuid);
                String name = onlinePlayer != null ? onlinePlayer.getDisplayName() : server.getOfflinePlayer(uuid).getName();
                boolean online = onlinePlayer != null;
                PlayerStats playerStats = playerStatsManager.getStats(uuid);
                if (playerStats == null) playerStats = new PlayerStats();

                tableBuilder.addRow(name, (!online ? ChatColor.GRAY : ChatColor.YELLOW) + "" +
                        playerStats.getKills() + "/" + playerStats.getDeaths() + " (" + kdFormat.format(playerStats.getKD()) + ")");
            }

            return tableBuilder.build();

        }

        return errorMessageWithUsage(CommandError.INVALID_ARGUMENT, args[0]);
    }

}
