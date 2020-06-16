package me.gimme.gimmehcf.faction;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.geom.Point2D;
import java.util.*;

@Getter
@Setter
public class Faction {

    private String name = "";

    @Nullable
    private UUID leader = null;
    private Set<UUID> officers = new HashSet<>();
    private Set<UUID> members = new HashSet<>();

    private Set<String> invites = new HashSet<>();
    private Set<String> allies = new LinkedHashSet<>();
    private Set<String> enemies = new LinkedHashSet<>();

    private Map<UUID, Set<Land>> landByWorld = new HashMap<>();
    private Map<UUID, Point2D> centerByWorld = new HashMap<>();

    private Map<Material, Integer> blockRegeneration = new HashMap<>();

    @Nullable
    private Location home = null;
    private double dtr = 0;

    public Faction() { // Required no-arg constructor
    }

    public Faction(String name) {
        this.name = name;
    }

    /**
     * Gets the unique ID of this faction. The ID is the faction's name in lower case.
     *
     * @return this faction's ID
     */
    public String getId() {
        return name.toLowerCase();
    }

    /**
     * Checks if the specified player is the leader of this faction.
     *
     * @param player the player to check
     * @return true if the specified player is the leader of this faction
     */
    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }

    /**
     * Checks if the specified player is an officer (higher ranks included) of this faction.
     *
     * @param player the player to check
     * @return true if the specified player is an officer of this faction
     */
    public boolean isOfficer(UUID player) {
        return officers.contains(player) || isLeader(player);
    }

    /**
     * Checks if the specified player is a member (higher ranks included) of this faction.
     *
     * @param player the player to check
     * @return true if the specified player is a member of this faction
     */
    public boolean isMember(UUID player) {
        return members.contains(player) || isOfficer(player);
    }

    /**
     * Gets the UUID of all players in the faction. O(n).
     * This returns a new list that does not get updated when the underlying data changes.
     *
     * @return the UUID of all players in the faction
     */
    public Set<UUID> getPlayers() {
        Set<UUID> result = new HashSet<>();
        if (leader != null) result.add(leader);
        result.addAll(officers);
        result.addAll(members);
        return result;
    }

    /**
     * Promotes a player from member to officer and returns true if the specified player was a member and was
     * successfully promoted to officer.
     *
     * @param player the player to promote
     * @return true if the player was promoted
     */
    public boolean promoteToOfficer(@NotNull UUID player) {
        if (!members.contains(player)) return false;
        members.remove(player);
        officers.add(player);
        return true;
    }

    /**
     * Promotes a player to leader and returns true if the specified player was a member or officer and was
     * successfully promoted to leader.
     *
     * @param player the player to promote
     * @return true if the player was promoted
     */
    public boolean promoteToLeader(@NotNull UUID player) {
        if (!members.contains(player) && !officers.contains(player)) return false;
        if (leader != null) {
            addPlayer(leader);
            promoteToOfficer(leader);
        }
        removePlayer(player);
        leader = player;
        return true;
    }

    /**
     * Demotes a player from officer to member and returns true if the player was an officer and was successfully
     * demoted to member.
     *
     * @param player the player to demote
     * @return true if the player was demoted
     */
    public boolean demoteToMember(UUID player) {
        if (!officers.contains(player)) return false;
        return removePlayer(player) && addPlayer(player);
    }

    public boolean addInvite(@NotNull String player) {
        return invites.add(player.toLowerCase());
    }

    public boolean removeInvite(@NotNull String player) {
        return invites.remove(player.toLowerCase());
    }

    public int removeAllInvites() {
        int cleared = invites.size();
        invites.clear();
        return cleared;
    }

    public int getNumberOfPlayers() {
        return members.size() + officers.size() + (leader != null ? 1 : 0);
    }

    public boolean addAlly(@NotNull String faction) {
        return allies.add(faction);
    }

    public boolean removeAlly(@NotNull String faction) {
        return allies.remove(faction);
    }

    public boolean addEnemy(@NotNull String faction) {
        return enemies.add(faction);
    }

    public boolean removeEnemy(@NotNull String faction) {
        return enemies.remove(faction);
    }

    /**
     * Returns if the faction is raidable.
     * A precision of a single decimal is used where anything below 0.05 DTR would result in raidable.
     *
     * @return if the faction is raidable
     */
    public boolean isRaidable() {
        return Math.round(dtr * 10) <= 0;
    }

    @Nullable
    public Boolean getFlag(Flag flag) {
        return null;
    }

    @NotNull
    public String getDisplayName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        return "";
    }

    @Nullable
    public String getMapMarker() {
        return null;
    }

    public int getClaimedLand() {
        int result = 0;
        for (Set<Land> landSet : landByWorld.values()) {
            result += landSet.size();
        }
        return result;
    }

    /**
     * Returns the average x and z coordinates of all the faction's claims in the specified world,
     * or null if the faction has no claims in that world.
     *
     * @param world the world to get the faction center in
     * @return the average x and z coordinates of all the faction's claims in the specified world,
     * or null if the faction has no claims in that world
     */
    @Nullable
    public Point2D getCenter(@NotNull UUID world) {
        return centerByWorld.get(world);
    }

    /**
     * Sets blocks of the specified material to be breakable and to regenerate after the specified delay in seconds after
     * being broken within this faction's territory.
     * <p>
     * A negative delay removes the item from being breakable and from regenerating.
     * <p>
     * Only makes a difference for factions that are otherwise ungriefable.
     *
     * @param material the material of the blocks to regenerate
     * @param delay    the time in seconds after the block is broken until it regenerates
     */
    public void setBlockRegeneration(Material material, int delay) {
        if (delay < 0) blockRegeneration.remove(material);
        else blockRegeneration.put(material, delay);
    }

    void setDtr(double dtr) {
        this.dtr = dtr;
    }

    boolean addPlayer(UUID player) {
        return members.add(player);
    }

    boolean removePlayer(UUID player) {
        if (leader != null && player == leader) {
            leader = null;
            return true;
        }
        return members.remove(player) || officers.remove(player);
    }

    boolean addLand(UUID world, Land land) {
        if (landByWorld.computeIfAbsent(world, k -> new LinkedHashSet<>()).add(land)) {
            updateCenter(world, land, true);
            return true;
        }
        return false;
    }

    boolean removeLand(UUID world, Land land) {
        Set<Land> landSet = landByWorld.get(world);
        if (landSet != null && landSet.remove(land)) {
            if (landSet.isEmpty()) landByWorld.remove(world);
            if (home != null && Objects.requireNonNull(home.getWorld()).getUID() == world &&
                    new Land(home).equals(land)) {
                setHome(null);
            }
            updateCenter(world, land, false);
            return true;
        }
        return false;
    }

    private void updateCenter(@NotNull UUID world, @NotNull Land land, boolean add) {
        Set<Land> landSet = landByWorld.computeIfAbsent(world, k -> new LinkedHashSet<>());

        if (!add && landByWorld.get(world).isEmpty()) {
            centerByWorld.remove(world);
            return;
        }

        Point2D center = centerByWorld.computeIfAbsent(world, k -> new Point2D.Double(0, 0));
        int size = landSet.size();
        double x = land.x * 16 + 8;
        double z = land.z * 16 + 8;
        double newCenterX = (center.getX() * (size + (add ? -1 : 1)) + x) / size;
        double newCenterZ = (center.getY() * (size + (add ? -1 : 1)) + z) / size;
        center.setLocation(newCenterX, newCenterZ);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}
