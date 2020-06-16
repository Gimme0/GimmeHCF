package me.gimme.gimmehcf.gameevent.gameevents;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.events.PlayerTerritoryEnterEvent;
import me.gimme.gimmehcf.gameevent.GameEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SOTWGameEvent extends GameEvent {

    private FileConfiguration config;

    public SOTWGameEvent(FileConfiguration config) {
        this.config = config;
    }

    @Override
    protected void onCountdownStart() {
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected void onFinish() {
    }

    /**
     * Stops players from leaving the chosen prison faction during countdown.
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerTerritoryEnter(PlayerTerritoryEnterEvent event) {
        if (isActive()) return;
        if (event.isCancelled()) return;

        for (String faction : config.getStringList(Config.SOTW_LOCKOUT_FACTION_WHITELIST.getPath())) {
            if (event.getTo().getName().equalsIgnoreCase(faction)) return;
        }

        event.setCancelled(true);
    }

    /**
     * Stops players from using portals that are disabled in the config.
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL) &&
                config.getBoolean(Config.SOTW_DISABLE_END_PORTAL.getPath())) {
            event.setCancelled(true);
        } else if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) &&
                config.getBoolean(Config.SOTW_DISABLE_NETHER_PORTAL.getPath())) {
            event.setCancelled(true);
        }
    }

    /**
     * Prevents PvP.
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER)) return;
        if (!event.getDamager().getType().equals(EntityType.PLAYER)) return;
        if (config.getBoolean(Config.SOTW_PVP.getPath())) return;
        event.setCancelled(true);
    }


}
