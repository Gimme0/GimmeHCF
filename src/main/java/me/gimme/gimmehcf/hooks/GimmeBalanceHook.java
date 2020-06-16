package me.gimme.gimmehcf.hooks;

import me.gimme.gimmebalance.GimmeBalance;
import me.gimme.gimmebalance.player.CombatLogManager;
import me.gimme.gimmecore.hook.PluginHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GimmeBalanceHook extends PluginHook<GimmeBalance> {

    public interface AllowedCombatLogCondition extends CombatLogManager.AllowedCondition {
        @Override
        boolean isCombatLogAllowed(@NotNull Player player);
    }

    public GimmeBalanceHook(@NotNull PluginManager pluginManager) {
        super(GimmeBalance.PLUGIN_NAME, pluginManager);
    }

    /**
     * Returns if the specified player is in combat.
     * Default: false (if the dependency is missing).
     *
     * @param player the player to check if in combat
     * @return if the specified player is in combat
     */
    public boolean isInCombat(@NotNull Player player) {
        return isInCombat(player.getUniqueId());
    }

    /**
     * Returns if the player with the specified id is in combat.
     * Default: false (if the dependency is missing).
     *
     * @param player the player id to check if in combat
     * @return if the player with the specified id is in combat
     */
    public boolean isInCombat(@NotNull UUID player) {
        return hookedPlugin != null && hookedPlugin.getCombatTimerManager().isInCombat(player);
    }

    /**
     * Adds a condition for when combat logging is allowed.
     *
     * @param condition the condition to add
     */
    public void addAllowedCombatLogCondition(AllowedCombatLogCondition condition) {
        hookedPlugin.addAllowedCombatLogCondition(condition);
    }

    /**
     * @return if the hooked plugin is active on the server and compatible with this hook.
     */
    public boolean isActive() {
        return hookedPlugin != null;
    }

}
