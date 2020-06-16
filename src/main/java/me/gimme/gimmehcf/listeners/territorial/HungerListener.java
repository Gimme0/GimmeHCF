package me.gimme.gimmehcf.listeners.territorial;

import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class HungerListener implements Listener {

    private FactionManager factionManager;

    public HungerListener(FactionManager factionManager) {
        this.factionManager = factionManager;
    }

    /**
     * Prevents players from losing food level while in hunger-free zones.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerLoseHunger(FoodLevelChangeEvent event) {
        if (factionManager.getLandFlag(Flag.HUNGER, event.getEntity().getLocation())) return;

        event.setCancelled(true);
    }

}
