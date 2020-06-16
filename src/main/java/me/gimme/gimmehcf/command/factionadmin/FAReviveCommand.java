package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.player.GhostManager;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FAReviveCommand extends FACommand {

    private GhostManager ghostManager;
    private Server server;

    public FAReviveCommand(LanguageManager languageManager, GhostManager ghostManager, Server server) {
        super("revive", languageManager);
        this.aliases.add("respawn");
        this.argsUsage = "<player>";
        this.argsAlternatives.add(ArgPlaceholder.PLAYERS.getPlaceholder());
        this.minArgs = 1;
        this.maxArgs = 1;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_FA_REVIVE).toString();

        this.ghostManager = ghostManager;
        this.server = server;
    }

    @Override
    protected String execute(CommandSender sender, String[] args) {
        Player player = server.getPlayer(args[0]);

        if (player == null) return errorMessage(languageManager.get(Message.ERROR_PLAYER_NOT_FOUND).toString());
        if (!ghostManager.revive(player)) return errorMessage(languageManager.get(Message.ERROR_PLAYER_NOT_DEAD).toString());
        return successMessage(languageManager.get(Message.SUCCESS_ADMIN_REVIVED).toString());
    }
}
