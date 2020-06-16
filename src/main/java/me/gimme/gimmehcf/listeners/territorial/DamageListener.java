package me.gimme.gimmehcf.listeners.territorial;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import me.gimme.gimmehcf.faction.Relation;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    private FactionManager factionManager;
    private FileConfiguration config;

    public DamageListener(FactionManager factionManager, FileConfiguration config) {
        this.factionManager = factionManager;
        this.config = config;
    }

    /**
     * Prevents fellow faction members and allies from damaging each other,
     * and players in safe zones from dealing damage to other players.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER) ||
                !event.getDamager().getType().equals(EntityType.PLAYER)) return;
        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        Relation relation = factionManager.getRelation(victim, damager);
        if ((relation == Relation.YOU && !config.getBoolean(Config.FACTION_PLAYER_FRIENDLY_FIRE.getPath())) ||
                (relation == Relation.ALLY && !config.getBoolean(Config.FACTION_PLAYER_FRIENDLY_FIRE_ALLY.getPath()))) {
            event.setCancelled(true);
            return;
        }

        if (factionManager.getLandFlag(Flag.DAMAGE, damager.getLocation())) return;
        event.setCancelled(true);
    }

    /**
     * Prevents players from taking damage while in safe zones.
     * Exception: falling into the void.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerDamage(EntityDamageEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PLAYER)) return;
        if (factionManager.getLandFlag(Flag.DAMAGE, event.getEntity().getLocation())) return;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) return;

        event.setCancelled(true);
    }

}
