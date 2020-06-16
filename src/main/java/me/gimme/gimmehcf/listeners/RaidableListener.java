package me.gimme.gimmehcf.listeners;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.events.FactionRaidableEvent;
import me.gimme.gimmehcf.faction.DtrRegenManager;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.Land;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class RaidableListener implements Listener {

    private DtrRegenManager dtrRegenManager;
    private Server server;
    private FileConfiguration config;

    public RaidableListener(DtrRegenManager dtrRegenManager, Server server, FileConfiguration config) {
        this.dtrRegenManager = dtrRegenManager;
        this.server = server;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onFactionRaidable(FactionRaidableEvent event) {
        if (event.isCancelled()) return;
        Faction faction = event.getFaction();
        Player player = event.getPlayer();

        dtrRegenManager.freezeDtr(faction);

        if (player != null && config.getBoolean(Config.DEATH_RAIDABLE_LIGHTNING_PLAYER.getPath()))
            player.getWorld().strikeLightningEffect(player.getLocation());

        if (config.getBoolean(Config.DEATH_RAIDABLE_LIGHTNING_FACTION.getPath())) { // Strike lightning at faction home
            if (faction.getHome() != null) {
                faction.getHome().getWorld().strikeLightningEffect(faction.getHome());
            } else if (faction.getClaimedLand() > 0) { // Strike lightning in the middle of any land that the faction has claimed
                for (UUID worldId : faction.getLandByWorld().keySet()) {
                    Set<Land> landSet = faction.getLandByWorld().get(worldId);
                    if (landSet.size() == 0) continue;

                    Iterator<Land> iter = landSet.iterator();
                    Land land = iter.next();
                    World world = server.getWorld(worldId);
                    world.strikeLightningEffect(world.getChunkAt(land.x, land.z).getBlock(8, 30, 8).getLocation());
                    break;
                }
            }
        }
    }

}
