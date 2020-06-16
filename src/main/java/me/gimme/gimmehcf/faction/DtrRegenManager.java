package me.gimme.gimmehcf.faction;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.events.PlayerLeaveFactionEvent;
import me.gimme.gimmehcf.listeners.OnlineFactionsListener;
import me.gimme.gimmehcf.player.GhostManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DtrRegenManager implements Listener {

    private Plugin plugin;
    private FileConfiguration config;
    private FactionManager factionManager;
    private OnlineFactionsListener onlineFactionsListener;
    private GhostManager ghostManager;

    private Map<Faction, UnfreezeTask> frozen = new HashMap<>();

    public DtrRegenManager(Plugin plugin, FactionManager factionManager, OnlineFactionsListener onlineFactionsListener, GhostManager ghostManager) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.factionManager = factionManager;
        this.onlineFactionsListener = onlineFactionsListener;
        this.ghostManager = ghostManager;

        startRegenerating();
    }

    /**
     * Freezes the specified faction from regenerating DTR for the duration specified in the config.
     *
     * @param faction the faction to freeze
     */
    public void freezeDtr(Faction faction) {
        freezeDtr(faction, 60 * config.getInt(Config.FACTION_DTR_FREEZE_DURATION.getPath()));
    }

    /**
     * Freezes the specified faction from regenerating DTR for the specified duration in seconds.
     *
     * @param faction the faction to freeze
     */
    public void freezeDtr(Faction faction, int duration) {
        if (duration <= 0) return;

        UnfreezeTask currentTask = frozen.get(faction);
        if (currentTask != null) {
            currentTask.secondsLeft = duration;
        } else {
            frozen.put(faction, new UnfreezeTask(faction, duration).start());
        }
    }

    /**
     * Gets the time (in seconds) left of the freeze on the specified faction.
     * Returns 0 if the faction is not frozen.
     *
     * @param faction the faction to get the freeze time of
     * @return the time left of the freeze in seconds, or 0 if not frozen
     */
    public long getFrozenSeconds(Faction faction) {
        UnfreezeTask task = frozen.get(faction);
        if (task == null) return 0;
        return task.secondsLeft;
    }

    /**
     * Truncates the DTR when a player leaves a faction to the max allowed by the new total amount of members.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerLeaveFaction(PlayerLeaveFactionEvent event) {
        Faction faction = event.getFaction();
        faction.setDtr(Math.min(faction.getDtr(), factionManager.getMaxDtr(faction)));
    }

    private void startRegenerating() {
        double regenDelay = config.getDouble(Config.FACTION_DTR_REGEN_PERIOD.getPath());
        long delay = Math.round(regenDelay * 20);
        if (delay <= 0) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                double regen = config.getDouble(Config.FACTION_DTR_REGEN.getPath());
                Map<Faction, Set<UUID>> onlinePlayersByFaction = onlineFactionsListener.getOnlinePlayersByFaction();
                for (Faction faction : onlinePlayersByFaction.keySet()) { // For all online factions
                    if (frozen.containsKey(faction)) continue; // Do not regenerate frozen factions
                    long playersAlive = onlinePlayersByFaction.get(faction)
                            .stream()
                            .filter(player -> !ghostManager.isDead(player))
                            .count();
                    if (playersAlive == 0) continue; // Only regen if at least one player is alive

                    double max = factionManager.getMaxDtr((int) playersAlive);
                    if (faction.getDtr() >= max) continue; // We don't want to truncate the DTR here

                    // The max DTR depends on amount of alive (online) members
                    double newDtr = Math.min(faction.getDtr() + regen, max);
                    factionManager.setDtr(faction, newDtr);
                }
            }
        }.runTaskTimer(plugin, delay, delay);
    }

    private class UnfreezeTask extends BukkitRunnable {

        private Faction faction;
        private int secondsLeft;

        private UnfreezeTask(Faction faction, int delay) {
            this.faction = faction;
            this.secondsLeft = delay;
        }

        @Override
        public void run() {
            if (secondsLeft-- <= 0) {
                frozen.remove(faction);
                cancel();
            }
        }

        private UnfreezeTask start() {
            runTaskTimer(plugin, 0, 20);
            return this;
        }
    }

}
