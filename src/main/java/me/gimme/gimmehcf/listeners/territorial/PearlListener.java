package me.gimme.gimmehcf.listeners.territorial;

import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.util.ActionBarSendingUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;

/**
 * Enforces the rules of the PEARL flag.
 *
 * If the PEARL flag is set to false, you cannot launch ender pearls in that area and you cannot teleport into that area
 * with ender pearls.
 */
public class PearlListener implements Listener {

    private FactionManager factionManager;
    private LanguageManager languageManager;

    public PearlListener(FactionManager factionManager, LanguageManager languageManager) {
        this.factionManager = factionManager;
        this.languageManager = languageManager;
    }

    /**
     * Prevents players from teleporting with ender pearls to areas where it is disabled.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        if (!event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) return;

        if (event.getTo() == null || factionManager.getLandFlag(Flag.PEARL, event.getTo())) return;

        event.setCancelled(true);
        sendPearlBlockedMessage(event.getPlayer());
    }

    /**
     * Prevents players from launching ender pearls in areas where it is disabled.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled()) return;
        ProjectileSource shooter = event.getEntity().getShooter();
        if (!(shooter instanceof Player)) return;
        Player player = (Player) shooter;

        if (factionManager.getLandFlag(Flag.PEARL, player.getLocation())) return;

        event.setCancelled(true);
        sendPearlBlockedMessage(player);
    }

    private void sendPearlBlockedMessage(@NotNull Player player) {
        ActionBarSendingUtil.sendActionBar(player, languageManager.get(Message.INFO_ACTION_BLOCKED).toString());
    }

}
