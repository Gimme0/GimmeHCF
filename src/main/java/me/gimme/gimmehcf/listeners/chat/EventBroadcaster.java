package me.gimme.gimmehcf.listeners.chat;

import com.google.common.base.Strings;
import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.events.*;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Relation;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EventBroadcaster implements Listener {

    private FactionManager factionManager;
    private LanguageManager languageManager;
    private Server server;
    FileConfiguration config;

    public EventBroadcaster(FactionManager factionManager, LanguageManager languageManager, Server server, FileConfiguration config) {
        this.factionManager = factionManager;
        this.languageManager = languageManager;
        this.server = server;
        this.config = config;
    }

    private void broadcastMessageWithFactionName(@NotNull Message message, @NotNull Faction faction) {
        for (Player player : server.getOnlinePlayers()) {
            String factionDisplayName = factionManager.getDisplayName(faction, player);
            player.sendMessage(languageManager.get(message).replace(Placeholder.FACTION, factionDisplayName).toString());
        }
    }

    /**
     * Announce when a faction becomes raidable.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onFactionRaidable(FactionRaidableEvent event) {
        if (event.isCancelled()) return;
        broadcastMessageWithFactionName(Message.ANNOUNCEMENT_RAIDABLE, event.getFaction());
    }

    /**
     * Announce when a player dies or gets a kill.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        boolean deathMessages = config.getBoolean(Config.DEATH_MESSAGES.getPath());

        if (deathMessages) {
            event.setDeathMessage(null); // Disable normal death message
            if (killer != null) {
                server.broadcastMessage(languageManager.get(Message.ANNOUNCEMENT_DEATH_PLAYER_KILL)
                        .replace(Placeholder.PLAYER, victim.getDisplayName())
                        .replace(Placeholder.KILLER, killer.getDisplayName()).toString());
            } else {
                server.broadcastMessage(languageManager.get(Message.ANNOUNCEMENT_DEATH_DEFAULT)
                        .replace(Placeholder.PLAYER, victim.getDisplayName()).toString());
            }
        }
    }


    /**
     * Notify all faction members that someone joined their faction.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoinFaction(PlayerJoinFactionEvent event) {
        String message = languageManager.get(Message.INFO_JOINED_YOUR_FACTION)
                .replace(Placeholder.PLAYER, event.getPlayer().getDisplayName()).toString();

        for (UUID member : event.getFaction().getPlayers()) {
            Player p = server.getPlayer(member);
            if (p == null) continue;
            if (p.getUniqueId().equals(member)) continue;
            p.sendMessage(message);
        }
    }

    /**
     * Notify all faction members that someone left their faction.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerLeaveFaction(PlayerLeaveFactionEvent event) {
        if (!(event.getReason().equals(PlayerLeaveFactionEvent.Reason.LEAVE) ||
                event.getReason().equals(PlayerLeaveFactionEvent.Reason.KICK))) return;
        OfflinePlayer player = event.getPlayer();
        String playerName = player.getPlayer() != null ? player.getPlayer().getDisplayName() : player.getName();
        String message = languageManager.get(event.getReason().equals(PlayerLeaveFactionEvent.Reason.LEAVE) ?
                Message.INFO_LEFT_YOUR_FACTION : Message.INFO_KICKED_FROM_YOUR_FACTION)
                .replace(Placeholder.PLAYER, playerName).toString();

        for (UUID member : event.getFaction().getPlayers()) {
            Player p = server.getPlayer(member);
            if (p == null) continue;
            if (p.getUniqueId().equals(member)) continue;
            p.sendMessage(message);
        }
    }


    /**
     * Notify players when they get kicked from their faction.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerKickedFromFaction(PlayerLeaveFactionEvent event) {
        if (!(event.getReason().equals(PlayerLeaveFactionEvent.Reason.KICK) ||
                event.getReason().equals(PlayerLeaveFactionEvent.Reason.DISBAND))) return;
        Player player = event.getPlayer().getPlayer();
        if (player == null) return;

        String message = languageManager.get(event.getReason().equals(PlayerLeaveFactionEvent.Reason.KICK) ?
                Message.INFO_YOU_HAVE_BEEN_KICKED : Message.INFO_YOUR_FACTION_DISBANDED).toString();
        player.sendMessage(message);
    }

    /**
     * Sends update messages to all online members of both factions when they gain or lose ally status.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onFactionRelationChange(FactionRelationChangeEvent event) {
        boolean allied;
        if (Relation.ALLY.equals(event.getToRelation())) allied = true;
        else if (Relation.ALLY.equals(event.getFromRelation())) allied = false;
        else return;
        sendAllyUpdateMessage(event.getFaction1(), event.getFaction2(), allied);
        sendAllyUpdateMessage(event.getFaction2(), event.getFaction1(), allied);
    }

    private void sendAllyUpdateMessage(@NotNull Faction faction, @NotNull Faction ally, boolean allied) {
        Message languageMessage = allied ? Message.INFO_NOW_ALLIED_WITH : Message.INFO_NO_LONGER_ALLIED_WITH;
        if (Strings.isNullOrEmpty(languageManager.get(languageMessage).toString())) return;

        for (UUID memberId : faction.getPlayers()) {
            Player member = server.getPlayer(memberId);
            if (member == null || !member.isOnline()) continue;

            member.sendMessage(languageManager.get(languageMessage)
                    .replace(Placeholder.FACTION, factionManager.getDisplayName(ally, member)).toString());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onFactionFocusSet(FactionFocusSetEvent event) {
        Player player = event.getPlayer();
        Faction faction  = event.getFaction();
        Faction focus = event.getFocus();
        Faction previousFocus = event.getPreviousFocus();

        if (focus != null) {
            for (UUID memberId : faction.getPlayers()) {
                Player member = server.getPlayer(memberId);
                if (member == null || !member.isOnline()) continue;

                member.sendMessage(languageManager.get(Message.INFO_FACTION_FOCUS_SET)
                        .replace(Placeholder.PLAYER, factionManager.getRelationColor(Relation.YOU) + player.getDisplayName())
                        .replace(Placeholder.FACTION, factionManager.getDisplayName(focus, member)).toString());
            }
        } else if (previousFocus != null) {
            for (UUID memberId : faction.getPlayers()) {
                Player member = server.getPlayer(memberId);
                if (member == null || !member.isOnline()) continue;

                member.sendMessage(languageManager.get(Message.INFO_FACTION_FOCUS_REMOVED)
                        .replace(Placeholder.PLAYER, factionManager.getRelationColor(Relation.YOU) + player.getDisplayName())
                        .replace(Placeholder.FACTION, factionManager.getDisplayName(previousFocus, member)).toString());
            }
        }
    }

}
