package me.gimme.gimmehcf.listeners.chat;

import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

public class ChatListener implements Listener {

    private FactionManager factionManager;

    public ChatListener(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        Player sender = event.getPlayer();
        Faction faction = factionManager.getFaction(sender);
        if (faction == null) return;

        for (Player recipient : event.getRecipients()) {
            Team senderTeam = recipient.getScoreboard().getEntryTeam(sender.getName());
            ChatColor senderColor = senderTeam == null ? ChatColor.RESET : senderTeam.getColor();

            String format = "[" + factionManager.getDisplayName(faction, recipient) + ChatColor.RESET + "] " + event.getFormat();
            recipient.sendMessage(String.format(format,
                    senderColor + sender.getDisplayName() + ChatColor.RESET,
                    event.getMessage()));
        }
        event.setCancelled(true);
    }

}
