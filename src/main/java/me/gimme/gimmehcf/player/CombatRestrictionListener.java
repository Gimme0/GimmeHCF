package me.gimme.gimmehcf.player;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.hooks.GimmeBalanceHook;
import me.gimme.gimmehcf.events.PlayerTerritoryEnterEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class CombatRestrictionListener implements Listener {

    private FileConfiguration config;
    private GimmeBalanceHook gimmeBalanceHook;

    public CombatRestrictionListener(@NotNull FileConfiguration config, @NotNull GimmeBalanceHook gimmeBalanceHook) {
        this.config = config;
        this.gimmeBalanceHook = gimmeBalanceHook;
    }

    /**
     * Stops players from blacklisted factions while in combat.
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerTerritoryEnter(PlayerTerritoryEnterEvent event) {
        if (event.isCancelled()) return;
        if (!gimmeBalanceHook.isInCombat(event.getPlayer())) return;

        for (String faction : config.getStringList(Config.COMBAT_FACTION_BLACKLIST.getPath())) {
            if (event.getTo().getName().equals(faction)) {
                event.setCancelled(true);
                return;
            }
        }
    }

}
