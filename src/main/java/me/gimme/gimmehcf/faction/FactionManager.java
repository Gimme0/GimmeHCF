package me.gimme.gimmehcf.faction;

import com.google.common.base.Strings;
import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.events.FactionRaidableEvent;
import me.gimme.gimmehcf.events.PlayerJoinFactionEvent;
import me.gimme.gimmehcf.events.PlayerLeaveFactionEvent;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

/**
 * Manages all factions.
 */
public class FactionManager {

    private static final char COLOR_CHAR = '&';

    private Plugin plugin;
    private FileConfiguration config;
    private FactionLoader factionLoader;

    private Map<String, Faction> factionByName = new HashMap<>();
    private Map<UUID, Faction> factionByPlayer = new HashMap<>();
    private Map<UUID, Map<Land, Faction>> factionByLandByWorld = new HashMap<>();
    private Map<String, SysFaction> systemFactionByName = new HashMap<>();

    private Map<String, Faction> focusByFaction = new HashMap<>();

    public FactionManager(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.factionLoader = new FactionLoader(plugin.getDataFolder());
    }

    /**
     * Loads all saved factions and system factions from their respective file into the plugin.
     * This overwrites all factions currently loaded and should only be used when the plugin first gets
     * enabled {@link Plugin#onEnable()}.
     */
    public void loadFactions() {
        factionLoader.loadFactions(factionByName, systemFactionByName);

        for (Faction faction : factionByName.values()) {
            for (UUID player : faction.getPlayers()) {
                factionByPlayer.put(player, faction);
            }
            for (UUID world : faction.getLandByWorld().keySet()) {
                factionByLandByWorld.computeIfAbsent(world, k -> new HashMap<>());
                for (Land land : faction.getLandByWorld().get(world)) {
                    factionByLandByWorld.get(world).put(land, faction);
                }
            }
        }

        String defaultFaction = config.getString(Config.DEFAULT_FACTION.getPath());
        if (defaultFaction == null || getFaction(defaultFaction) == null) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't find the default faction!" + "\n" +
                    "You need to define a default system faction, that exists, in your config.yml file.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    /**
     * Saves the current state of all factions and system factions to their respective file.
     * Should be called in {@link Plugin#onDisable()} and is recommended to be called in a constant interval
     * to avoid losing progress in the event of a crash.
     * If there are no factions, nothing gets saved because it is likely due to a crash before loading in the factions,
     * and there is probably not any reason to want to save 0 factions anyway.
     */
    public void saveFactions() {
        if (factionByName.isEmpty()) return;
        factionLoader.saveFactions(factionByName, systemFactionByName);
    }

    /**
     * Attempts to create a new faction with the creator as leader.
     * If a faction with that name already exists, no faction is created and false is returned.
     *
     * @param name    the name of the new faction
     * @param creator the player to be the leader of the faction
     * @return true if a new faction was successfully created, false if the name is taken
     * @throws IllegalStateException if the player is in a faction already
     */
    public boolean createFaction(@NotNull String name, @NotNull Player creator) throws IllegalStateException {
        if (getFaction(name) != null) return false;

        Faction faction = new Faction(name);
        faction.setDtr(config.getDouble(Config.FACTION_DTR_STARTING.getPath()));
        addPlayerToFaction(faction, creator);
        faction.promoteToLeader(creator.getUniqueId());

        factionByName.put(faction.getId(), faction);
        return true;
    }

    /**
     * Attempts to create a new system faction. If a faction with that name already exists,
     * no faction is created and false is returned.
     *
     * @param name  the name of the new system faction
     * @param color the color of the new system faction, or null if default color
     * @return true if a new system faction was successfully created, false if the name is taken
     */
    public boolean createSystemFaction(@NotNull String name, @Nullable ChatColor color) {
        if (getFaction(name) != null) return false;

        SysFaction faction;
        if (color == null) faction = new SysFaction(name);
        else faction = new SysFaction(name, color);
        systemFactionByName.put(faction.getId(), faction);
        factionByName.put(faction.getId(), faction);
        return true;
    }

    /**
     * Removes the specified faction, kicking every member and unclaiming all land.
     *
     * @param faction the faction to remove
     * @param caller  the player that disbanded the faction, or null if not a player
     */
    public void removeFaction(@NotNull Faction faction, @Nullable OfflinePlayer caller) {
        for (UUID player : faction.getPlayers()) {
            if (caller != null && player.equals(caller.getUniqueId())) continue;
            removePlayerFromFaction(faction, player, PlayerLeaveFactionEvent.Reason.DISBAND);
        }
        if (caller != null) removePlayerFromFaction(faction, caller.getUniqueId(), PlayerLeaveFactionEvent.Reason.LEAVE);

        for (UUID world : faction.getLandByWorld().keySet()) {
            for (Land land : faction.getLandByWorld().get(world)) {
                unclaimLand(faction, world, land);
            }
        }
        factionByName.remove(faction.getId());
    }

    /**
     * Gets all loaded factions (including system factions).
     *
     * @return all loaded factions
     */
    public Collection<Faction> getFactions() {
        return factionByName.values();
    }

    /**
     * Gets all loaded system factions.
     *
     * @return all loaded system factions
     */
    public Collection<SysFaction> getSystemFactions() {
        return systemFactionByName.values();
    }

    /**
     * Gets the faction with the specified name.
     *
     * @param name the faction name
     * @return the faction with the specified name, or null if it doesn't exist
     */
    @Nullable
    public Faction getFaction(@NotNull String name) {
        name = name.toLowerCase();
        return factionByName.get(name);
    }

    /**
     * Gets the system faction with the specified name.
     *
     * @param name the faction name
     * @return the system faction with the specified name, or null if it doesn't exist
     */
    @Nullable
    public SysFaction getSystemFaction(@NotNull String name) {
        name = name.toLowerCase();
        return systemFactionByName.get(name);
    }

    /**
     * Returns the faction to which the specified player is a member of,
     * or null if this player is not a member of any faction.
     *
     * @param player the player whose faction to get
     * @return the faction that the player is a member of, or null if the player is not in a faction
     */
    @Nullable
    public Faction getFaction(OfflinePlayer player) {
        if (player == null) return null;
        return factionByPlayer.get(player.getUniqueId());
    }

    /**
     * Returns the faction that owns the specified location.
     * If no faction has claimed the land the default faction is returned.
     *
     * @param location the location to check the owner of
     * @return the owner of the location
     */
    @NotNull
    public Faction getFaction(@NotNull Location location) {
        return getFaction(Objects.requireNonNull(location.getWorld()), new Land(location));
    }

    /**
     * Returns the faction that owns the specified land in the specified world.
     * If no faction has claimed the land the default faction is returned.
     *
     * @param world the world to check the land in
     * @param land  the land to check the owner of
     * @return the owner of the land
     */
    @NotNull
    public Faction getFaction(@NotNull World world, Land land) {
        Faction faction = null;
        Map<Land, Faction> factionByLand = factionByLandByWorld.get(world.getUID());
        if (factionByLand != null)
            faction = factionByLand.get(land);
        if (faction != null) return faction;

        ConfigurationSection worldFactionsSection =
                config.getConfigurationSection(Config.DEFAULT_FACTION_BY_WORLD.getPath());
        if (worldFactionsSection != null) {
            String worldFaction = worldFactionsSection.getString(world.getName());
            if (worldFaction != null) faction = getFaction(worldFaction);
        }
        if (faction == null) {
            String defaultFaction = config.getString(Config.DEFAULT_FACTION.getPath());
            if (defaultFaction != null) faction = getFaction(defaultFaction);
        }
        if (faction == null)
            throw new IllegalStateException("Could not find the default faction defined in the config");

        return faction;
    }

    /**
     * Adds the specified player to the specified faction.
     *
     * @param faction the faction to add the specified player to
     * @param player  the player to add to the specified faction
     * @throws IllegalStateException if the player is in a faction already
     */
    public void addPlayerToFaction(@NotNull Faction faction, @NotNull Player player) throws IllegalStateException {
        if (factionByPlayer.containsKey(player.getUniqueId()))
            throw new IllegalStateException("The player cannot be in a faction already");

        faction.addPlayer(player.getUniqueId());
        factionByPlayer.put(player.getUniqueId(), faction);

        Bukkit.getPluginManager().callEvent(new PlayerJoinFactionEvent(player, faction));
    }

    /**
     * Removes the specified player from the specified faction. Returns true if the player was a member of the faction
     * and was successfully removed.
     *
     * @param faction the faction to add the specified player to
     * @param player  the player to add to the specified faction
     * @return true if the player was a member of the faction and was successfully removed
     */
    public boolean removePlayerFromFaction(@NotNull Faction faction, @NotNull UUID player, @NotNull PlayerLeaveFactionEvent.Reason reason) {
        if (!faction.removePlayer(player)) return false;
        factionByPlayer.remove(player);
        faction.setDtr(Math.min(faction.getDtr(), getMaxDtr(faction)));

        Bukkit.getPluginManager().callEvent(
                new PlayerLeaveFactionEvent(plugin.getServer().getOfflinePlayer(player), faction, reason));

        return true;
    }

    /**
     * Sets the DTR of the specified faction and calls an event if it results in raidable.
     *
     * @param faction the faction to set the DTR for
     * @param dtr     the new DTR
     */
    public void setDtr(@NotNull Faction faction, double dtr) {
        setDtr(faction, dtr, null);
    }

    /**
     * Sets the DTR of the specified faction and calls an event if it results in raidable.
     * The specified player is the death that caused the dtr change or null if another cause.
     *
     * @param faction the faction to set the DTR for
     * @param dtr     the new DTR
     */
    public void setDtr(@NotNull Faction faction, double dtr, @Nullable Player player) {
        boolean wasRaidable = faction.isRaidable();
        faction.setDtr(Math.min(dtr, getMaxDtr(faction)));

        if (!wasRaidable && faction.isRaidable()) {
            FactionRaidableEvent factionRaidableEvent = new FactionRaidableEvent(faction, player);
            Bukkit.getPluginManager().callEvent(factionRaidableEvent);
            if (factionRaidableEvent.isCancelled()) {
                faction.setDtr(config.getDouble(Config.FACTION_DTR_REGEN.getPath(), 0.1));
            }
        }
    }

    /**
     * Gets the max deaths until raidable for the specified faction.
     *
     * @param faction the faction to get the max DTR from
     * @return the max deaths until raidable for the specified faction
     */
    public double getMaxDtr(@NotNull Faction faction) {
        return getMaxDtr(faction.getNumberOfPlayers());
    }

    /**
     * Gets the max deaths until raidable for a faction with the specified amount of members.
     *
     * @param members amount of members to calculate the max DTR for
     * @return the max deaths until raidable for the specified amount of members
     */
    public double getMaxDtr(int members) {
        double max = config.getDouble(Config.FACTION_DTR_MAX.getPath());
        double base = config.getDouble(Config.FACTION_DTR_BASE.getPath());
        double bonus = config.getDouble(Config.FACTION_DTR_BONUS_PER_PLAYER.getPath());
        return Math.min(max, base + bonus * members);
    }

    /**
     * Returns the max amount of land that the specified faction can have, or -1 if infinite.
     *
     * @param faction the faction to check its max
     * @return max amount of land allowed, or -1 if infinite
     */
    public int getMaxLand(@NotNull Faction faction) {
        if (getLandFlag(Flag.SYSTEM, faction)) return -1;

        int base = config.getInt(Config.FACTION_LAND_BASE.getPath());
        int bonusPerPlayer = config.getInt(Config.FACTION_LAND_BONUS_PER_PLAYER.getPath());
        int maxLimit = config.getInt(Config.FACTION_LAND_MAX.getPath(), -1);

        int calculatedMax = base + bonusPerPlayer * faction.getNumberOfPlayers();
        if (maxLimit >= 0) return Math.min(calculatedMax, maxLimit);
        return calculatedMax;
    }

    /**
     * Returns if the faction is overclaimed. A faction is overclaimed if it has more claimed land than its max land.
     *
     * @param faction the faction to check if overclaimed
     * @return true if the faction is overclaimed
     */
    public boolean isOverclaimed(@NotNull Faction faction) {
        int claimedLand = faction.getClaimedLand();
        int maxLand = getMaxLand(faction);
        return claimedLand > maxLand && maxLand != -1;
    }

    /**
     * Claims the land at the specified location for the specified faction.
     *
     * @param faction  the faction to claim the land for
     * @param location a location in the land to claim
     * @return if the land was claimed successfully
     */
    public boolean claimLand(@NotNull Faction faction, @NotNull Location location) {
        return claimLand(faction, Objects.requireNonNull(location.getWorld()).getUID(), new Land(location));
    }

    /**
     * Claims the specified land for the specified faction.
     *
     * @param faction the faction to claim the land for
     * @param world   the world that the land is in
     * @param land    the land to claim
     * @return if the land was claimed successfully
     */
    public boolean claimLand(@NotNull Faction faction, @NotNull UUID world, @NotNull Land land) {
        if (faction.addLand(world, land)) {
            factionByLandByWorld.computeIfAbsent(world, k -> new HashMap<>());
            Map<Land, Faction> factionByLand = factionByLandByWorld.get(world);
            if (factionByLand.containsKey(land)) {
                factionByLand.get(land).removeLand(world, land);
            }
            factionByLand.put(land, faction);
            return true;
        }
        return false;
    }

    /**
     * Tries to unclaim the land at the specified location for the specified faction.
     * Returns true if the land was owned by the specified faction and successfully unclaimed it.
     *
     * @param faction  the faction to unclaim the land for
     * @param location a location in the land to unclaim
     * @return true if the land was owned by the specified faction and successfully unclaimed it
     */
    public boolean unclaimLand(@NotNull Faction faction, @NotNull Location location) {
        return unclaimLand(faction, Objects.requireNonNull(location.getWorld()).getUID(), new Land(location));
    }

    /**
     * Tries to unclaim the specified land for the specified faction.
     * Returns true if the land was owned by the specified faction and successfully unclaimed it.
     *
     * @param faction the faction to unclaim the land for
     * @param world   the world that the land is in
     * @param land    the land to unclaim
     * @return true if the land was owned by the specified faction and successfully unclaimed it
     */
    public boolean unclaimLand(@NotNull Faction faction, @NotNull UUID world, Land land) {
        faction.removeLand(world, land);
        if (factionByLandByWorld.get(world) == null) return false;
        return factionByLandByWorld.get(world).remove(land) == faction;
    }

    public int unclaimAllLand(@NotNull Faction faction) {
        int amountUnclaimed = 0;
        for (Iterator<UUID> worldIterator = faction.getLandByWorld().keySet().iterator(); worldIterator.hasNext(); ) {
            UUID world = worldIterator.next();
            for (Iterator<Land> landIterator = faction.getLandByWorld().get(world).iterator(); landIterator.hasNext(); ) {
                Land land = landIterator.next();
                landIterator.remove();
                factionByLandByWorld.get(world).remove(land);
                amountUnclaimed++;
            }
            worldIterator.remove();
        }
        faction.setHome(null);
        return amountUnclaimed;
    }

    /**
     * Gets the value of the specified flag of the land at the specified location. If no specific flag is set,
     * the value is taken from the list of default flags in the config.
     *
     * @param flag     the flag to check the value for
     * @param location a location in the land to check
     * @return the value of the flag set for this land
     */
    public boolean getLandFlag(@NotNull Flag flag, @NotNull Location location) {
        return getLandFlag(flag, getFaction(location));
    }

    /**
     * Gets the value of the specified flag of the land at the specified location. If no specific flag is set,
     * the value is taken from the list of default flags in the config.
     *
     * @param flag  the flag to check the value for
     * @param world the world that the land is in
     * @param land  the land to check
     * @return the value of the flag set for this land
     */
    public boolean getLandFlag(@NotNull Flag flag, @NotNull World world, Land land) {
        return getLandFlag(flag, getFaction(world, land));
    }

    /**
     * Gets the value of the specified flag of all land of the specified faction. If no specific flag is set,
     * the value is taken from the list of default flags in the config.
     *
     * @param flag    the flag to check the value for
     * @param faction the faction to check
     * @return the value of the flag set for this faction
     */
    public boolean getLandFlag(@NotNull Flag flag, @NotNull Faction faction) {
        Boolean value = null;

        if (faction.isRaidable() && config.contains(Config.FLAGS_FACTION_RAIDABLE.getPath() + "." + flag.toString())) {
            value = config.getBoolean(Config.FLAGS_FACTION_RAIDABLE.getPath() + "." + flag.toString());
        }

        if (value == null) value = faction.getFlag(flag);
        if (value == null) value = config.getBoolean(Config.FLAGS_FACTION_DEFAULT.getPath() + "." + flag.toString(),
                flag.getDefault());
        return value;
    }

    /**
     * Sets the faction to be focused for the specified faction.
     *
     * @param faction the faction doing the focusing
     * @param focus   the faction to be focused
     */
    public void setFocus(@NotNull Faction faction, @Nullable Faction focus) {
        focusByFaction.put(faction.getId(), focus);
    }

    /**
     * Gets the faction that is currently being focused by the specified faction, or null if nothing is being focused.
     *
     * @param faction the faction doing the focusing
     * @return the faction currently being focused, or null if nothing is being focused
     */
    @Nullable
    public Faction getFocus(@NotNull Faction faction) {
        return focusByFaction.get(faction.getId());
    }

    /**
     * Gets the display name of the specified faction from the specified player's perspective. For example, if the
     * faction is an ally to the player's faction, a specific ally color is added to the name.
     *
     * @param faction  the faction to get the display name of
     * @param toPlayer the player to whom the name is displayed, or null if not a player
     * @return the display name of the specified faction with regards to the specified player
     */
    @NotNull
    public String getDisplayName(@NotNull Faction faction, @Nullable OfflinePlayer toPlayer) {
        if (toPlayer == null) return faction.getDisplayName();
        return getDisplayColor(faction, toPlayer) + faction.getDisplayName();
    }

    /**
     * Gets the display color of the specified faction from the specified player's perspective.
     * For example, if the faction is an ally to the player's faction, a specific ally color is returned.
     *
     * @param faction  the faction to get the display color of
     * @param toPlayer the player to whom the color is displayed
     * @return the display color of the specified faction with regards to the specified player
     */
    @NotNull
    public ChatColor getDisplayColor(@NotNull Faction faction, @NotNull OfflinePlayer toPlayer) {
        return getRelationColor(getRelation(faction, toPlayer));
    }

    /**
     * Gets the color associated with the specified relation.
     *
     * @param relation the relation to get the color from
     * @return the color associated with the specified relation
     */
    @NotNull
    public ChatColor getRelationColor(@NotNull Relation relation) {
        ChatColor defaultColor = ChatColor.WHITE;
        String color = config.getString(relation.getColorConfigPath());
        if (color == null || color.length() < 1) return defaultColor;
        ChatColor chatColor = ChatColor.getByChar(color.charAt(color.length() - 1));
        if (chatColor == null) return defaultColor;
        return chatColor;
    }

    /**
     * Gets the relation between the specified players.
     *
     * @param player   the player to get the relation to
     * @param toPlayer the player to whom the relation is
     * @return the relation between the specified players
     */
    @NotNull
    public Relation getRelation(@NotNull OfflinePlayer player, @NotNull OfflinePlayer toPlayer) {
        Faction faction = getFaction(player);
        if (faction == null) return Relation.NEUTRAL;
        return getRelation(faction, toPlayer);
    }

    /**
     * Gets the relation between the specified faction and the specified player.
     *
     * @param faction  the faction to get the relation to
     * @param toPlayer the player to whom the relation is
     * @return the relation between the specified faction and the specified player
     */
    @NotNull
    public Relation getRelation(@NotNull Faction faction, @NotNull OfflinePlayer toPlayer) {
        Faction toFaction = getFaction(toPlayer);
        if (toFaction == null) return Relation.NEUTRAL;
        return getRelation(faction, toFaction);
    }

    /**
     * Gets the relation between the specified factions.
     *
     * @param faction  the faction to get the relation to
     * @param toFaction the faction to which the relation is
     * @return the relation between the specified factions
     */
    @NotNull
    public Relation getRelation(@NotNull Faction faction, @NotNull Faction toFaction) {
        if (faction == toFaction) {
            return Relation.YOU;
        } else if (toFaction.getAllies().contains(faction.getId()) && faction.getAllies().contains(toFaction.getId())) {
            return Relation.ALLY;
        } else if (getFocus(toFaction) == faction) {
            return Relation.FOCUS;
        } else if (toFaction.getEnemies().contains(faction.getId()) && faction.getEnemies().contains(toFaction.getId())) {
            return Relation.ENEMY;
        }
        return Relation.NEUTRAL;
    }

}
