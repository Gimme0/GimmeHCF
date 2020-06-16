package me.gimme.gimmehcf.command.faction;

import com.google.common.base.Strings;
import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.events.FactionRelationChangeEvent;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Relation;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class FAllyCommand extends FCommand {

    private Server server;

    public FAllyCommand(FactionManager factionManager, LanguageManager languageManager, Server server) {
        super("ally", factionManager, languageManager);
        this.argsUsage = "[add/remove] <faction>";
        this.argsAlternatives.add("add " + ArgPlaceholder.FACTIONS.getPlaceholder());
        this.argsAlternatives.add("remove " + ArgPlaceholder.FACTIONS.getPlaceholder());
        this.minArgs = 2;
        this.maxArgs = 2;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_ALLY).toString();
        this.factionPermission = FactionPermission.OFFICER;

        this.server = server;
    }

    @Override
    protected @Nullable String execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;
        Faction ally = factionManager.getFaction(args[1]);
        if (ally == null) return errorMessage(languageManager.get(Message.ERROR_FACTION_NOT_FOUND)
                .replace(Placeholder.INPUT, args[1]).toString());

        if (args[0].equals("add")) {

            if (!faction.addAlly(ally.getId())) return null;
            if (ally.getAllies().contains(faction.getId())) {
                faction.removeEnemy(ally.getId());
                ally.removeEnemy(faction.getId());
                Bukkit.getPluginManager().callEvent(new FactionRelationChangeEvent(faction, ally, null, Relation.ALLY));
                return null;
            }

            sendAllyRequest(faction, ally);
            return successMessage(languageManager.get(Message.SUCCESS_SENT_ALLY_REQUEST)
                    .replace(Placeholder.FACTION, factionManager.getDisplayName(ally, player)).toString());

        } else if (args[0].equals("remove")) {

            if (!faction.removeAlly(ally.getId())) return null;
            if (ally.removeAlly(faction.getId())) {
                Bukkit.getPluginManager().callEvent(new FactionRelationChangeEvent(faction, ally, Relation.ALLY, Relation.NEUTRAL));
                return null;
            }

            return successMessage(languageManager.get(Message.SUCCESS_REMOVED_ALLY_REQUEST)
                    .replace(Placeholder.FACTION, factionManager.getDisplayName(ally, player)).toString());

        }

        return errorMessageWithUsage(CommandError.INVALID_ARGUMENT, args[0]);
    }

    private void sendAllyRequest(@NotNull Faction faction, @NotNull Faction ally) {
        Set<UUID> officers = ally.getOfficers();
        if (ally.getLeader() != null) officers.add(ally.getLeader());
        for (UUID officerId : officers) {
            Player officer = server.getPlayer(officerId);
            if (officer == null || !officer.isOnline()) continue;
            String message = languageManager.get(Message.INFO_ALLY_REQUEST_FROM)
                    .replace(Placeholder.FACTION, factionManager.getDisplayName(faction, officer)).toString();
            if (!Strings.isNullOrEmpty(message)) officer.sendMessage(message);
        }
    }
}
