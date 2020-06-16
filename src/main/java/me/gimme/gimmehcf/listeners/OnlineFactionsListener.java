package me.gimme.gimmehcf.listeners;

import me.gimme.gimmehcf.events.PlayerJoinFactionEvent;
import me.gimme.gimmehcf.events.PlayerLeaveFactionEvent;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class OnlineFactionsListener implements Listener {

    private FactionManager factionManager;

    private Map<Faction, Set<UUID>> onlinePlayersByFaction = new HashMap<>();

    public OnlineFactionsListener(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    /**
     * Returns the amount of players online in the specified faction.
     *
     * @param faction the faction to count the online players in
     * @return amount of online players in the faction
     */
    public int getOnlinePlayers(Faction faction) {
        Set<UUID> onlinePlayers = onlinePlayersByFaction.get(faction);
        if (onlinePlayers == null) return 0;
        return onlinePlayers.size();
    }

    /**
     * Returns the map of online players by faction.
     * Factions with no online players will be absent from the map.
     *
     * @return the map of online players by faction
     */
    public Map<Faction, Set<UUID>> getOnlinePlayersByFaction() {
        return onlinePlayersByFaction;
    }

    /**
     * Returns all factions that currently don't have any players online. This list does not stay up to date as players
     * join and leave. It simply takes all factions and removes every faction that has at least one player online.
     *
     * @return all currently offline factions
     */
    public List<Faction> getOfflineFactions() {
        List<Faction> offlineFactions = new ArrayList<>(factionManager.getFactions());
        offlineFactions.removeIf(faction -> getOnlinePlayers(faction) > 0);
        return offlineFactions;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Faction faction = factionManager.getFaction(event.getPlayer());
        if (faction == null) return;

        incrementOnline(faction, event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Faction faction = factionManager.getFaction(event.getPlayer());
        if (faction == null) return;

        decrementOnline(faction, event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoinFaction(PlayerJoinFactionEvent event) {
        incrementOnline(event.getFaction(), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerLeaveFaction(PlayerLeaveFactionEvent event) {
        decrementOnline(event.getFaction(), event.getPlayer().getUniqueId());
    }

    private void incrementOnline(Faction faction, UUID player) {
        onlinePlayersByFaction.computeIfAbsent(faction, k -> new HashSet<>()).add(player);
    }

    private void decrementOnline(Faction faction, UUID player) {
        Set<UUID> onlinePlayers = onlinePlayersByFaction.get(faction);
        onlinePlayers.remove(player);
        if (onlinePlayers.size() <= 0) onlinePlayersByFaction.remove(faction);
    }

}
