package me.gimme.gimmehcf.listeners.territorial;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import me.gimme.gimmehcf.player.PermissionKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class InteractListener implements Listener {

    private FactionManager factionManager;
    private FileConfiguration config;

    public InteractListener(FactionManager factionManager, FileConfiguration config) {
        this.factionManager = factionManager;
        this.config = config;
    }

    /**
     * Prevents players from interacting with blocks in protected areas.
     * Certain blocks might be allowed depending on what is in the whitelist/blacklist in the config.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;
        final Player player = event.getPlayer();
        final Location location = clickedBlock.getLocation();

        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.INTERACT, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        List<String> interactWhitelist = config.getStringList(Config.FLAGS_INTERACT_WHITELIST.getPath());
        List<String> interactBlacklist = config.getStringList(Config.FLAGS_INTERACT_BLACKLIST.getPath());
        if (interactWhitelist.size() > 0) {
            for (String material : interactWhitelist) {
                if (clickedBlock.getType().equals(Material.matchMaterial(material))) return;
            }
        } else if (interactBlacklist.size() > 0) {
            for (String material : interactBlacklist) {
                if (clickedBlock.getType().equals(Material.matchMaterial(material))) {
                    event.setCancelled(true);
                    return;
                }
            }
            return;
        }

        event.setCancelled(true);
    }

}
