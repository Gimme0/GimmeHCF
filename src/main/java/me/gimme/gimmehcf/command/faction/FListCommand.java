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
import me.gimme.gimmehcf.util.DtrFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class FListCommand extends FCommand {

    private static final int DEFAULT_LISTINGS_PER_PAGE = 8;

    private OnlineFactionsListener onlineFactionsListener;

    public FListCommand(FactionManager factionManager, LanguageManager languageManager, OnlineFactionsListener onlineFactionsListener) {
        super("list", factionManager, languageManager);
        this.argsUsage = "[online/dtr] [page=1]";
        this.argsAlternatives.add("online");
        this.argsAlternatives.add("dtr");
        this.minArgs = 0;
        this.maxArgs = 2;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_F_LIST).toString();

        this.onlineFactionsListener = onlineFactionsListener;
    }

    @Override
    protected String execute(@NotNull CommandSender sender, String[] args) {
        String arg = args.length > 0 ? args[0] : "online";
        String pageInput = args.length > 1 ? args[1] : "1";
        int pageNumber;

        try {
            pageNumber = Integer.parseInt(pageInput);
        } catch (NumberFormatException e) {
            return errorMessageWithUsage(CommandError.NOT_A_NUMBER, pageInput);
        }
        int perPage = (sender instanceof ConsoleCommandSender) ? -1 : DEFAULT_LISTINGS_PER_PAGE;

        if (arg.equals("online")) {

            int amountOfOnlineFactions = onlineFactionsListener.getOnlinePlayersByFaction().size();

            List<Faction> factions = onlineFactionsListener.getOnlinePlayersByFaction().entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(Set::size)))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            List<Faction> offlineFactions = onlineFactionsListener.getOfflineFactions();
            offlineFactions.removeAll(factionManager.getSystemFactions());
            offlineFactions.sort(Comparator.comparing(Faction::getNumberOfPlayers));
            factions.addAll(offlineFactions);

            Pageifier.PageResult<Faction> pageResult = Pageifier.getPage(factions, perPage, pageNumber);

            if (!(1 <= pageNumber && pageNumber <= pageResult.totalPages)) {
                return errorMessage(languageManager.get(Message.ERROR_PAGE_OOB)
                        .replace(Placeholder.N, pageResult.totalPages + "").toString());
            }

            TableBuilder tableBuilder = new ChatTableBuilder()
                    .setEllipsize(true)
                    .addCol(ChatTableBuilder.Alignment.LEFT, 0.25)
                    .addCol(ChatTableBuilder.Alignment.LEFT, -0.45)
                    .addCol(ChatTableBuilder.Alignment.LEFT, 0.30);

            String[] header = languageManager.get(Message.HEADER_F_LIST_ONLINE)
                    .replace(Placeholder.N, amountOfOnlineFactions + "")
                    .replace(Placeholder.PAGE, pageResult.page + "/" + pageResult.totalPages).toStringArray();
            if (!(header.length == 1 && header[0].length() == 0)) { // If header is not empty
                tableBuilder.addRow(header).addRow();
            }

            Player playerOrNull = (sender instanceof Player) ? (Player) sender : null;
            for (Faction faction : pageResult.content) {
                int onlinePlayers = onlineFactionsListener.getOnlinePlayers(faction);
                tableBuilder.addRow(factionManager.getDisplayName(faction, playerOrNull),
                        (onlinePlayers == 0 ? ChatColor.GRAY : ChatColor.YELLOW) + "" +
                                onlinePlayers + "/" + faction.getNumberOfPlayers());
            }

            return tableBuilder.build();

        } else if (arg.equals("dtr")) {

            List<Faction> factions = new ArrayList<>(onlineFactionsListener.getOnlinePlayersByFaction().keySet());
            factions.sort(Comparator.comparing(Faction::getDtr).reversed());

            List<Faction> offlineFactions = onlineFactionsListener.getOfflineFactions();
            offlineFactions.removeAll(factionManager.getSystemFactions());
            offlineFactions.sort(Comparator.comparing(Faction::getDtr).reversed());
            factions.addAll(offlineFactions);

            Pageifier.PageResult<Faction> pageResult = Pageifier.getPage(factions, perPage, pageNumber);

            if (!(1 <= pageNumber && pageNumber <= pageResult.totalPages)) {
                return errorMessage(languageManager.get(Message.ERROR_PAGE_OOB)
                        .replace(Placeholder.N, pageResult.totalPages + "").toString());
            }

            TableBuilder tableBuilder = new ChatTableBuilder()
                    .setEllipsize(true)
                    .addCol(ChatTableBuilder.Alignment.LEFT, 0.25)
                    .addCol(ChatTableBuilder.Alignment.LEFT, -0.45)
                    .addCol(ChatTableBuilder.Alignment.LEFT, 0.30);

            String[] header = languageManager.get(Message.HEADER_F_LIST_DTR)
                    .replace(Placeholder.PAGE, pageResult.page + "/" + pageResult.totalPages).toStringArray();
            if (!(header.length == 1 && header[0].length() == 0)) { // If header is not empty
                tableBuilder.addRow(header).addRow();
            }

            Player playerOrNull = (sender instanceof Player) ? (Player) sender : null;
            for (Faction faction : pageResult.content) {
                int onlinePlayers = onlineFactionsListener.getOnlinePlayers(faction);
                String dtr = DtrFormat.oneDecimal(faction.getDtr());
                String maxDtr = DtrFormat.oneDecimal(factionManager.getMaxDtr(faction));
                tableBuilder.addRow(factionManager.getDisplayName(faction, playerOrNull),
                        (onlinePlayers == 0 ? ChatColor.GRAY : (faction.isRaidable() ? ChatColor.RED : ChatColor.YELLOW)) +
                                dtr + "/" + maxDtr);
            }
            return tableBuilder.build();

        }

        return errorMessageWithUsage(CommandError.INVALID_ARGUMENT, args[0]);
    }

}
