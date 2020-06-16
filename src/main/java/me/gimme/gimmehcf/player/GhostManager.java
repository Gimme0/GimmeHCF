package me.gimme.gimmehcf.player;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.events.PlayerTerritoryEnterEvent;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import me.gimme.gimmehcf.util.TimeFormat;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

public class GhostManager implements Listener {

    private Plugin plugin;
    private FileConfiguration config;
    private LanguageManager languageManager;

    private Map<UUID, RespawnTask> deathTimers = new HashMap<>();

    public GhostManager(Plugin plugin, LanguageManager languageManager) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.languageManager = languageManager;
    }

    /**
     * Returns if the specified player is respawning.
     *
     * @param player the player to check
     * @return if the specified player is respawning
     */
    public boolean isDead(UUID player) {
        return deathTimers.containsKey(player);
    }

    /**
     * Finishes a player's death timer if they had one. Returns true if the player was dead is now revived.
     *
     * @param player the player to revive
     * @return if the player was dead and is now revived
     */
    public boolean revive(Player player) {
        RespawnTask task = deathTimers.get(player.getUniqueId());
        if (task == null) return false;

        task.finish();
        return true;
    }

    /**
     * Stops players from leaving the chosen prison faction.
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerTerritoryEnter(PlayerTerritoryEnterEvent event) {
        if (event.isCancelled()) return;
        if (!isDead(event.getPlayer())) return;

        for (String faction : config.getStringList(Config.DEATH_FACTION_WHITELIST.getPath())) {
            if (event.getTo().getName().equalsIgnoreCase(faction)) return;
        }

        event.setCancelled(true);
    }

    /**
     * Starts the death timer when players die.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerDeath(PlayerDeathEvent event) {
        startDeathTimer(event.getEntity());
    }

    /**
     * Allows only whitelisted commands or prevents only blacklisted commands (depending on what is used in the config).
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        if (event.isCancelled()) return;
        if (!isDead(player)) return;

        List<String> whitelist = config.getStringList(Config.DEATH_COMMAND_WHITELIST.getPath());
        List<String> blacklist = config.getStringList(Config.DEATH_COMMAND_BLACKLIST.getPath());
        if (whitelist.size() > 0) {
            for (String c : whitelist) {
                if (command.startsWith(c)) {
                    if (command.length() == c.length() || command.charAt(c.length()) == ' ') {
                        return;
                    }
                }
            }
        } else if (blacklist.size() > 0) {
            for (String c : blacklist) {
                if (command.startsWith(c)) {
                    if (command.length() == c.length() || command.charAt(c.length()) == ' ') {
                        player.sendMessage(languageManager.get(Message.DEATH_TIMER_BLOCKED_COMMAND).toString());
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            return;
        }

        player.sendMessage(languageManager.get(Message.DEATH_TIMER_BLOCKED_COMMAND).toString());
        event.setCancelled(true);
    }

    /**
     * Gives dead players a death timer item that shows the remaining death time.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!isDead(player)) return;

        ItemStack clock = new ItemStack(Material.CLOCK);

        ItemMeta meta = clock.getItemMeta();
        assert meta != null;
        meta.setDisplayName(languageManager.get(Message.DEATH_TIMER_ITEM_NAME).toString());
        meta.setLore(Arrays.asList(languageManager.get(Message.DEATH_TIMER_ITEM_LORE).toString()
                .split(Pattern.quote("\n")))); // "\n" in the string represents a new line in the lore
        clock.setItemMeta(meta);

        player.getInventory().setItem(8, clock);
        player.getInventory().setHeldItemSlot(8);
    }

    /**
     * Makes sure that players holding the death timer item are set to have their remaining duration displayed.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (!isDead(player)) return;

        ItemStack mainHandItem = player.getInventory().getItem(event.getNewSlot());
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if ((mainHandItem == null || !mainHandItem.getType().equals(Material.CLOCK)) &&
                !offHandItem.getType().equals(Material.CLOCK)) {
            deathTimers.get(player.getUniqueId()).setDisplayScreenTimer(false);
            return;
        }

        ItemMeta mainHandMeta = mainHandItem == null ? null : mainHandItem.getItemMeta();
        ItemMeta offHandMeta = offHandItem.getItemMeta();

        if ((mainHandMeta != null && mainHandMeta.getDisplayName().equals(languageManager.get(Message.DEATH_TIMER_ITEM_NAME).toString())) ||
                (offHandMeta != null && offHandMeta.getDisplayName().equals(languageManager.get(Message.DEATH_TIMER_ITEM_NAME).toString()))) {
            deathTimers.get(player.getUniqueId()).setDisplayScreenTimer(true);
            return;
        }

        deathTimers.get(player.getUniqueId()).setDisplayScreenTimer(false);
    }

    /**
     * Stops players from clicking the death timer item in their inventory and keeping it from getting removed at the
     * end of the timer.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (!isDead(event.getWhoClicked())) return;

        if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.CLOCK)) {
            ItemMeta meta = event.getCurrentItem().getItemMeta();
            if (meta != null &&
                    meta.hasDisplayName() &&
                    meta.getDisplayName().equals(languageManager.get(Message.DEATH_TIMER_ITEM_NAME).toString())) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getCursor() != null && event.getCursor().getType().equals(Material.CLOCK)) {
            ItemMeta meta = event.getCursor().getItemMeta();
            if (meta != null &&
                    meta.hasDisplayName() &&
                    meta.getDisplayName().equals(languageManager.get(Message.DEATH_TIMER_ITEM_NAME).toString())) {
                event.setCancelled(true);
                //return;
            }
        }
    }

    /**
     * Stops players from dropping the death timer item.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.isCancelled()) return;
        if (!isDead(event.getPlayer())) return;

        ItemMeta meta = event.getItemDrop().getItemStack().getItemMeta();
        if (meta == null) return;
        if (!meta.hasDisplayName()) return;
        if (!meta.getDisplayName().equals(languageManager.get(Message.DEATH_TIMER_ITEM_NAME).toString())) return;

        event.setCancelled(true);
    }

    /**
     * Removes death timer item from players who were logged off when they respawned.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (isDead(player)) return;
        removeDeathTimerItemFromInventory(player);
    }

    /**
     * Starts a deathTimer for the specified player for a duration depending on their rank.
     * Players with a death timer can have certain limitations (e.g. cannot leave spawn).
     *
     * @param player the player to start the death timer for
     */
    private void startDeathTimer(Player player) {
        int duration = getDuration(player);
        if (duration <= 0) return;
        deathTimers.put(player.getUniqueId(), new RespawnTask(player, duration).start());
    }

    private boolean isDead(HumanEntity player) {
        return deathTimers.containsKey(player.getUniqueId());
    }

    private int getDuration(Player player) {
        int duration = 0;
        boolean durationSet = false;
        ConfigurationSection ranks = config.getConfigurationSection(Config.DEATH_TIMER_RANKS.getPath());
        if (ranks != null) {
            for (String rank : ranks.getKeys(false)) {
                if (player.hasPermission(PermissionKey.DEATH_TIMER.getChildPath(rank))) {
                    if (durationSet) {
                        duration = Math.min(duration, ranks.getInt(rank));
                    } else {
                        duration = ranks.getInt(rank);
                        durationSet = true;
                    }
                }
            }
        }
        if (!durationSet) duration = config.getInt(Config.DEATH_TIMER_DEFAULT.getPath());
        return duration;
    }

    private void removeDeathTimerItemFromInventory(@NotNull Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) { // Remove the death timer item
            if (itemStack == null) continue; // This check is required
            if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName().equals(
                    languageManager.get(Message.DEATH_TIMER_ITEM_NAME).toString())) {
                player.getInventory().remove(itemStack);
                break;
            }
        }
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
        if (itemInOffHand.getItemMeta() != null && itemInOffHand.getItemMeta().getDisplayName().equals(
                languageManager.get(Message.DEATH_TIMER_ITEM_NAME).toString())) {
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    private class RespawnTask extends BukkitRunnable {

        private Player player;
        private int secondsLeft;
        private String formattedTime;
        private boolean displayScreenTimer = false;

        private RespawnTask(Player player, int delay) {
            this.player = player;
            this.secondsLeft = delay;
        }

        @Override
        public void run() {
            formattedTime = TimeFormat.digitalTime(secondsLeft);

            player.setPlayerListName(languageManager.get(Message.DEATH_TIMER_PLAYER_LIST)
                    .replace(Placeholder.PLAYER, player.getDisplayName())
                    .replace(Placeholder.TIME, formattedTime).toString());

            if (displayScreenTimer) {
                sendScreenText();
            }

            if (secondsLeft-- <= 0) finish();
        }

        private void finish() {
            cancel();
            deathTimers.remove(player.getUniqueId());
            player.setPlayerListName(player.getDisplayName());
            removeDeathTimerItemFromInventory(player);

            player.sendTitle(languageManager.get(Message.DEATH_TIMER_RESPAWNED).toString(), "",
                    0, 20, 20);
        }

        private RespawnTask start() {
            runTaskTimer(plugin, 0, 20);
            return this;
        }

        private void sendScreenText() {
            player.sendTitle(languageManager.get(Message.DEATH_TIMER_SCREEN_TEXT)
                            .replace(Placeholder.TIME, formattedTime).toString(),
                    "",
                    0, 25, 10);
        }

        /**
         * Sets whether or not to display the timer on the players screen.
         * If true, the screen text will be updated every seconds. Additionally, the first time this changes the value
         * each frame (second), the screen text is instantly shown/removed on the screen depending on the new value.
         *
         * @param b whether or not to display the timer on the players screen
         */
        private void setDisplayScreenTimer(boolean b) {
            this.displayScreenTimer = b;
            if (b) sendScreenText();
            else player.resetTitle();
        }
    }

}
