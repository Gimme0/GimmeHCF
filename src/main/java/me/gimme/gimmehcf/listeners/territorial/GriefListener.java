package me.gimme.gimmehcf.listeners.territorial;

import me.gimme.gimmehcf.GimmeHCF;
import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.Faction;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import me.gimme.gimmehcf.player.PermissionKey;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GriefListener implements Listener {

    private static final Set<Material> BLOCKED_PLACE_ENTITY_MATERIALS = Set.of(Material.ARMOR_STAND,
            Material.ACACIA_BOAT, Material.BIRCH_BOAT, Material.DARK_OAK_BOAT, Material.JUNGLE_BOAT, Material.OAK_BOAT,
            Material.SPRUCE_BOAT);

    private static final String METADATA_KEY_BLOCK_PLACE_TIMESTAMP = "block_place_timestamp";

    private Plugin plugin;
    private FactionManager factionManager;
    private FileConfiguration config;

    public GriefListener(Plugin plugin, FactionManager factionManager, FileConfiguration config) {
        this.factionManager = factionManager;
        this.plugin = plugin;
        this.config = config;
    }

    /**
     * Prevents placing blocks in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) {
            int blockProtectionDelay = config.getInt(Config.BLOCK_PROTECTION_DELAY.getPath());
            if (blockProtectionDelay > 0) {
                // Mark the time (in deciseconds) when the protection for this block starts
                event.getBlock().setMetadata(METADATA_KEY_BLOCK_PLACE_TIMESTAMP, new FixedMetadataValue(plugin,
                        Math.round((System.currentTimeMillis() - GimmeHCF.PLUGIN_START_TIME_MILLIS) / 100f
                                + blockProtectionDelay * 10)));
            }
            return;
        }

        event.setBuild(false);
        event.setCancelled(true);
    }

    /**
     * Prevents breaking blocks in protected areas.
     * Exception if it's in a faction where the block type is a regenerating block.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;

        Faction faction = factionManager.getFaction(location);
        if (factionManager.getLandFlag(Flag.GRIEF, faction)) return;
        if (factionManager.getFaction(player) == faction) return;
        if (!factionManager.getLandFlag(Flag.SYSTEM, faction) &&
                event.getBlock().hasMetadata(METADATA_KEY_BLOCK_PLACE_TIMESTAMP) &&
                (System.currentTimeMillis() - GimmeHCF.PLUGIN_START_TIME_MILLIS) / 100f <
                    event.getBlock().getMetadata(METADATA_KEY_BLOCK_PLACE_TIMESTAMP).get(0).asInt()) return;

        Integer regenDelay = faction.getBlockRegeneration().get(event.getBlock().getType());
        if (regenDelay == null) {
            event.setCancelled(true);
        } else {
            BlockState blockState = event.getBlock().getState();
            new BukkitRunnable() {
                @Override
                public void run() {
                    blockState.update(true, false);
                }

                @Override
                public void cancel() {
                    super.cancel();
                    run();
                }
            }.runTaskLater(plugin, regenDelay * 20);
        }
    }

    /**
     * Prevents emptying buckets in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents filling buckets in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        final Location location = event.getBlock().getLocation();

        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents exploding blocks in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        for (Iterator<Block> blockIterator = event.blockList().iterator(); blockIterator.hasNext(); ) {
            final Location location = blockIterator.next().getLocation();
            if (factionManager.getLandFlag(Flag.EXPLOSION, location)) continue;
            blockIterator.remove();
        }
    }

    /**
     * Prevents placing multi-blocks such as beds that clip into protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;
        final Faction playerFaction = factionManager.getFaction(player);

        for (BlockState blockState : event.getReplacedBlockStates()) {
            final Location location = blockState.getLocation();

            if (factionManager.getLandFlag(Flag.GRIEF, location)) continue;
            if (playerFaction == factionManager.getFaction(location)) continue;

            event.setCancelled(true);
            return;
        }
    }

    /**
     * Prevents pistons from extending and pushing blocks into protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (event.isCancelled()) return;
        final Block piston = event.getBlock();

        final Faction pistonFaction = factionManager.getFaction(piston.getLocation());

        if (event.getBlocks().size() == 0) { // Prevent piston from extending into protected areas

            final Block invadedBlock = piston.getRelative(event.getDirection());

            if (factionManager.getLandFlag(Flag.GRIEF, invadedBlock.getLocation())) return;
            if (factionManager.getFaction(invadedBlock.getLocation()) == pistonFaction) return;

            event.setCancelled(true);

        } else { // Prevent piston from pushing blocks into protected areas

            for (Block block : event.getBlocks()) {
                final Block invadedBlock = block.getRelative(event.getDirection());

                if (factionManager.getLandFlag(Flag.GRIEF, invadedBlock.getLocation())) continue;
                if (factionManager.getFaction(invadedBlock.getLocation()) == pistonFaction) continue;

                event.setCancelled(true);
                return;
            }

        }
    }

    /**
     * Prevents pistons from retracting blocks from protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.isCancelled()) return;
        final Block piston = event.getBlock();
        final Faction pistonFaction = factionManager.getFaction(piston.getLocation());

        for (Block block : event.getBlocks()) { // Prevent piston from retracting blocks from protected areas
            if (factionManager.getLandFlag(Flag.GRIEF, block.getLocation())) continue;
            if (factionManager.getFaction(block.getLocation()) == pistonFaction) continue;

            event.setCancelled(true);
            return;
        }
    }

    /**
     * Prevents blocks in protected areas from burning from sources outside the area.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockBurn(BlockBurnEvent event) {
        if (event.isCancelled()) return;
        if (event.getIgnitingBlock() == null) return;
        final Location from = event.getIgnitingBlock().getLocation();
        final Location to = event.getBlock().getLocation();

        if (factionManager.getLandFlag(Flag.GRIEF, to)) return;
        if (factionManager.getFaction(from) == factionManager.getFaction(to)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents blocks from being ignited in protected areas.
     * Exception: flint and steel by authorized players.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockIgnite(BlockIgniteEvent event) {
        if (event.isCancelled()) return;
        final Location location = event.getBlock().getLocation();

        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (event.getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents fire from spreading into protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockSpread(BlockSpreadEvent event) {
        if (event.isCancelled()) return;
        final Location from = event.getSource().getLocation();
        final Location to = event.getBlock().getLocation();

        if (event.getSource().getType() != Material.FIRE) return;
        if (factionManager.getLandFlag(Flag.GRIEF, to)) return;
        if (factionManager.getFaction(from) == factionManager.getFaction(to)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents water and lava from flowing into protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockFromTo(BlockFromToEvent event) {
        if (event.isCancelled()) return;
        final Location from = event.getBlock().getLocation();
        final Location to = event.getToBlock().getLocation();

        if (factionManager.getLandFlag(Flag.GRIEF, to)) return;
        if (factionManager.getFaction(from) == factionManager.getFaction(to)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents dispensers from dispensing into protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onBlockDispense(BlockDispenseEvent event) {
        if (event.isCancelled()) return;
        final Location from = event.getBlock().getLocation();
        final Location to = event.getBlock().getRelative(((Directional) event.getBlock().getBlockData()).getFacing())
                .getLocation();

        if (factionManager.getLandFlag(Flag.GRIEF, to)) return;
        if (factionManager.getFaction(from) == factionManager.getFaction(to)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents organic structures (trees, huge mushrooms) from growing into protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onStructureGrow(StructureGrowEvent event) {
        if (event.isCancelled()) return;
        final Faction sourceOwner = factionManager.getFaction(event.getLocation());

        for (Iterator<BlockState> blockIterator = event.getBlocks().iterator(); blockIterator.hasNext(); ) {
            Location growthLocation = blockIterator.next().getLocation();

            if (factionManager.getLandFlag(Flag.GRIEF, growthLocation)) continue;
            if (sourceOwner == factionManager.getFaction(growthLocation)) continue;

            blockIterator.remove();
        }
    }

    /**
     * Prevents players from trampling farmland in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerTrample(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        final Player player = event.getPlayer();
        final Location location = event.getClickedBlock().getLocation();

        if (!event.getAction().equals(Action.PHYSICAL) || !event.getClickedBlock().getType().equals(Material.FARMLAND))
            return;
        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents mobs from griefing in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onMobGrief(EntityChangeBlockEvent event) {
        if (event.isCancelled()) return;
        final Location location = event.getBlock().getLocation();

        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents placing items in item frames in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerInteractWithItemFrame(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!entity.getType().equals(EntityType.ITEM_FRAME)) return;
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Location location = entity.getLocation();

        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents stealing items from item frames in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerTakeFromItemFrame(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getType().equals(EntityType.ITEM_FRAME)) return;
        if (!event.getDamager().getType().equals(EntityType.PLAYER)) return;
        if (event.isCancelled()) return;

        final Player player = (Player) event.getDamager();
        final Location location = entity.getLocation();

        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents placing hanging entities in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onHangingPlace(HangingPlaceEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        final Location location = event.getEntity().getLocation();

        if (player != null && player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents breaking hanging entities in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onHangingBreakByPlayer(HangingBreakByEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getRemover() == null || !event.getRemover().getType().equals(EntityType.PLAYER)) return;
        final Player player = (Player) event.getRemover();
        final Location location = event.getEntity().getLocation();

        if (player != null && player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents breaking hanging entities in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onHangingBreak(HangingBreakEvent event) {
        if (event.isCancelled()) return;
        if (event.getCause().equals(HangingBreakEvent.RemoveCause.ENTITY)) return;
        final Location location = event.getEntity().getLocation();

        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents manipulating armor stands in protected areas-
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        final Location location = event.getRightClicked().getLocation();

        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents breaking armor stands in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onArmorStandDamagedByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!event.getEntityType().equals(EntityType.ARMOR_STAND)) return;
        final Player player = event.getDamager().getType().equals(EntityType.PLAYER) ? (Player) event.getDamager() : null;
        final Location location = event.getEntity().getLocation();

        if (player != null && player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        event.setCancelled(true);
    }

    /**
     * Prevents placing entities such as armor stands or boats in protected areas.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerPlaceEntity(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Location location = event.getClickedBlock() == null ? null : event.getClickedBlock().getLocation();

        if (!BLOCKED_PLACE_ENTITY_MATERIALS.contains(event.getMaterial())) return;
        if (player.hasPermission(PermissionKey.EDIT.getPath())) return;
        if (location != null && factionManager.getLandFlag(Flag.GRIEF, location)) return;
        if (location != null && factionManager.getFaction(player) == factionManager.getFaction(location)) return;

        event.setUseItemInHand(Event.Result.DENY);
    }

}
