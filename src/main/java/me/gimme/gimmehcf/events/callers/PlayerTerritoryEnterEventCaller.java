package me.gimme.gimmehcf.events.callers;

import me.gimme.gimmehcf.events.PlayerTerritoryEnterEvent;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Land;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTerritoryEnterEventCaller implements Listener {

    private FactionManager factionManager;

    private Map<UUID, Location> lastKnownLocationByPlayer = new HashMap<>();

    public PlayerTerritoryEnterEventCaller(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled() || event.getTo() == null) return;
        Player player = event.getPlayer();

        Location lastKnownLocation = lastKnownLocationByPlayer.get(player.getUniqueId());
        lastKnownLocationByPlayer.put(player.getUniqueId(), event.getTo());
        if (lastKnownLocation == null) return;

        if (Land.getX(lastKnownLocation) == Land.getX(event.getTo()) &&
                Land.getZ(lastKnownLocation) == Land.getZ(event.getTo())) return;

        Faction fromFaction = factionManager.getFaction(lastKnownLocation);
        Faction toFaction = factionManager.getFaction(event.getTo());
        if (fromFaction == toFaction) return;

        PlayerTerritoryEnterEvent playerTerritoryEnterEvent =
                new PlayerTerritoryEnterEvent(player, fromFaction, toFaction, PlayerTerritoryEnterEvent.EnterReason.MOVE);
        Bukkit.getPluginManager().callEvent(playerTerritoryEnterEvent);
        if (playerTerritoryEnterEvent.isCancelled()) {
            Vector vector = event.getTo().toVector().subtract(event.getFrom().toVector());
            vector.setX(vector.getX() * (Land.getX(event.getFrom()) == Land.getX(event.getTo()) ? 1 : -1));
            vector.setZ(vector.getZ() * (Land.getZ(event.getFrom()) == Land.getZ(event.getTo()) ? 1 : -1));
            if (Math.abs(vector.getY()) < 0.01) { // If running on the ground
                if (Land.getX(event.getFrom()) == Land.getX(event.getTo())) vector.rotateAroundZ(0.5);
                if (Land.getZ(event.getFrom()) == Land.getZ(event.getTo())) vector.rotateAroundX(0.5);
            }
            player.setVelocity(vector);

            lastKnownLocation.setYaw(event.getTo().getYaw());
            lastKnownLocation.setPitch(event.getTo().getPitch());
            event.setTo(lastKnownLocation);

            lastKnownLocationByPlayer.put(player.getUniqueId(), event.getTo());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled() || event.getCause().equals(PlayerTeleportEvent.TeleportCause.PLUGIN)
                || event.getTo() == null) return;

        Faction fromFaction = factionManager.getFaction(event.getFrom());
        Faction toFaction = factionManager.getFaction(event.getTo());
        if (fromFaction == toFaction) return;
        Player player = event.getPlayer();

        PlayerTerritoryEnterEvent playerTerritoryEnterEvent =
                new PlayerTerritoryEnterEvent(player, fromFaction, toFaction, PlayerTerritoryEnterEvent.EnterReason.TELEPORT);
        Bukkit.getPluginManager().callEvent(playerTerritoryEnterEvent);
        if (playerTerritoryEnterEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoin(PlayerJoinEvent event) {
        lastKnownLocationByPlayer.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        lastKnownLocationByPlayer.remove(event.getPlayer().getUniqueId());
    }

}
