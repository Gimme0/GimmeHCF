package me.gimme.gimmehcf.command.faction;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.events.FactionFocusSetEvent;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FFocusCommand extends FCommand {

    public FFocusCommand(@NotNull FactionManager factionManager, @NotNull LanguageManager languageManager) {
        super("focus", factionManager, languageManager);
        this.argsUsage = "[faction]";
        this.argsAlternatives.add(ArgPlaceholder.FACTIONS.getPlaceholder());
        this.minArgs = 0;
        this.maxArgs = 1;
        this.playerOnly = true;
        this.description = languageManager.get(Message.DESCRIPTION_F_FOCUS).toString();
        this.factionPermission = FactionPermission.MEMBER;
    }

    @Override
    protected @Nullable String execute(@NotNull CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Faction faction = factionManager.getFaction(player);
        assert faction != null;
        Faction focus = null;

        if (args.length > 0) {
            focus = factionManager.getFaction(args[0]);
            if (focus == null) return errorMessage(languageManager.get(Message.ERROR_FACTION_NOT_FOUND)
                    .replace(Placeholder.INPUT, args[0]).toString());
        }

        Faction previousFocus = factionManager.getFocus(faction);
        if (previousFocus == null && focus == null) return errorMessageWithUsage(CommandError.TOO_FEW_ARGUMENTS, null);
        if (previousFocus != null && previousFocus.equals(focus)) return null;

        Bukkit.getPluginManager().callEvent(new FactionFocusSetEvent(player, faction, focus, previousFocus));
        factionManager.setFocus(faction, focus);
        return null;
    }

}
