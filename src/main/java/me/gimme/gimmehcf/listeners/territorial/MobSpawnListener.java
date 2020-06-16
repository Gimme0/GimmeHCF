package me.gimme.gimmehcf.listeners.territorial;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import me.gimme.gimmehcf.faction.Land;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Set;

public class MobSpawnListener implements Listener {

    private static final Set<CreatureSpawnEvent.SpawnReason> ALLOWED_SPAWN_REASONS = Set.of(
            CreatureSpawnEvent.SpawnReason.SPAWNER,
            CreatureSpawnEvent.SpawnReason.CUSTOM);

    private FactionManager factionManager;
    private FileConfiguration config;

    public MobSpawnListener(FactionManager factionManager, FileConfiguration config) {
        this.factionManager = factionManager;
        this.config = config;
    }

    /**
     * Prevents mobs from naturally spawning in areas where it is disabled.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        Location location = event.getLocation();
        if (ALLOWED_SPAWN_REASONS.contains(event.getSpawnReason())) return;
        if (location.getWorld() != null &&
                config.getStringList(Config.FLAGS_MOBS_EXCLUDED_WORLDS.getPath()).contains(location.getWorld().getName()))
            return;
        if (factionManager.getLandFlag(Flag.MOBS, location)) return;
        if (event.getEntity().getType().equals(EntityType.PLAYER)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents hostile mobs from targeting players in territory where mobs don't spawn.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() == null) return;
        Location location = event.getTarget().getLocation();
        if (location.getWorld() != null &&
                config.getStringList(Config.FLAGS_MOBS_EXCLUDED_WORLDS.getPath()).contains(location.getWorld().getName()))
            return;
        if (factionManager.getLandFlag(Flag.MOBS, location)) return;
        if (!(event.getEntity() instanceof Monster)) return;

        event.setCancelled(true);
    }

}
