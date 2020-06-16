package me.gimme.gimmehcf.command.factionadmin;

import com.google.common.base.Strings;
import me.gimme.gimmecore.util.ChatTableBuilder;
import me.gimme.gimmecore.util.TableBuilder;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import me.gimme.gimmehcf.faction.SysFaction;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.Map;

import static me.gimme.gimmehcf.command.ArgPlaceholder.SYSTEM_FACTIONS;

public class FAFlagsCommand extends FACommand {

    private FactionManager factionManager;

    public FAFlagsCommand(FactionManager factionManager, LanguageManager languageManager) {
        super("flags", languageManager);
        this.aliases.add("showflags");
        this.argsUsage = "<faction>";
        this.argsAlternatives.add(SYSTEM_FACTIONS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_FA_FLAGS).toString();

        this.factionManager = factionManager;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        String factionName = args[0];
        SysFaction faction = factionManager.getSystemFaction(factionName);

        if (faction == null) return errorMessage(languageManager.get(Message.ERROR_SYSFACTION_NOT_FOUND)
                .replace(Placeholder.INPUT, factionName).toString());
        return getFormattedMessage(sender, faction);
    }

    private String getFormattedMessage(CommandSender messageReceiver, SysFaction faction) {
        final String header = languageManager.get(Message.HEADER_FA_FLAGS)
                .replace(Placeholder.FACTION, faction.getDisplayName()).toString();
        StringBuilder sb = new StringBuilder(ChatColor.RESET + "");

        if (messageReceiver instanceof ConsoleCommandSender) {
            sb.append(newLine);
        }
        sb.append(header);
        if (!Strings.isNullOrEmpty(header)) sb.append(newLine);

        TableBuilder tableBuilder = new ChatTableBuilder()
                .setEllipsize(true)
                .addCol(ChatTableBuilder.Alignment.LEFT, -0.5)
                .addCol(ChatTableBuilder.Alignment.LEFT, -0.5);

        Map<String, Boolean> factionFlags = faction.getFlags();
        Flag[] allFlags = Flag.values();
        for (Flag f : allFlags) {
            String flagValue;
            if (factionFlags.containsKey(f.toString())) {
                boolean value = factionFlags.get(f.toString());
                flagValue = "" + (value ? ChatColor.GREEN : ChatColor.RED) + value;
            } else {
                flagValue = ChatColor.GRAY + "default";
            }
            tableBuilder.addRow(ChatColor.GOLD + f.toString() + ":", "     " + flagValue);
        }
        sb.append(tableBuilder.build());

        return sb.toString();
    }

}
