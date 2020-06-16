package me.gimme.gimmehcf.listeners;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.player.PlayerStatsManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class KillDeathListener implements Listener {

    private Plugin plugin;
    private FactionManager factionManager;
    private PlayerStatsManager playerStatsManager;
    private FileConfiguration config;

    private Map<Player, LastDamagerTask> lastDamagerByPlayer = new HashMap<>();

    public KillDeathListener(Plugin plugin, FactionManager factionManager, PlayerStatsManager playerStatsManager) {
        this.plugin = plugin;
        this.factionManager = factionManager;
        this.playerStatsManager = playerStatsManager;
        this.config = plugin.getConfig();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!event.getEntity().getType().equals(EntityType.PLAYER) || !event.getDamager().getType().equals(EntityType.PLAYER))
            return;
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        LastDamagerTask task = lastDamagerByPlayer.get(player);
        if (task != null && task.lastDamager == damager) return;
        if (damager == player) return;
        lastDamagerByPlayer.put(player, new LastDamagerTask(player, damager).start());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null || killer == victim) {
            LastDamagerTask task = lastDamagerByPlayer.get(victim);
            if (task != null) killer = task.lastDamager;
        }

        handleKillDeath(killer, victim);
    }

    private void handleKillDeath(@Nullable Player killer, @NotNull Player victim) {
        if (killer != null) playerStatsManager.incrementKillCount(killer);
        playerStatsManager.incrementDeathCount(victim);
        decrementDtr(victim);
    }

    /**
     * Decrements the DTR of the specified player's faction (if member of one), by the defined amount for that world,
     * which also calls an event if it results in raidable. Does not decrease thh DTR if the faction is already raidable.
     *
     * @param player the player that died
     */
    private void decrementDtr(@NotNull Player player) {
        Faction faction = factionManager.getFaction(player);
        if (faction == null) return;
        if (faction.isRaidable()) return;

        double decreaseAmount = 1;

        String world = player.getWorld().getName();
        ConfigurationSection lossByWorld = config.getConfigurationSection(Config.FACTION_DTR_WORLD_DEATH_LOSS.getPath());
        if (lossByWorld != null && lossByWorld.contains(world)) decreaseAmount = lossByWorld.getDouble(world);

        factionManager.setDtr(faction, faction.getDtr() - decreaseAmount);
    }

    private class LastDamagerTask extends BukkitRunnable {

        private Player player;
        private Player lastDamager;

        LastDamagerTask(Player player, Player lastDamager) {
            this.lastDamager = lastDamager;
        }

        @Override
        public void run() {
            lastDamagerByPlayer.remove(player);
        }

        private LastDamagerTask start() {
            this.runTaskLater(plugin, config.getInt(Config.KILL_CREDIT_TIMER.getPath()));
            return this;
        }

    }

}
