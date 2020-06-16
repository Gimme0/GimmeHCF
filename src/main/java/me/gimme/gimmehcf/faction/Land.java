package me.gimme.gimmehcf.faction;

import lombok.EqualsAndHashCode;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
public class Land {

    private static final int LAND_SIDE_LENGTH = 16;

    public int x;
    public int z;

    public Land() { // Required no-arg constructor
    }

    public Land(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public Land(@NotNull Location location) {
        this.x = getX(location);
        this.z = getZ(location);
    }

    public static Set<Land> getAdjacent(@NotNull Land land) {
        Set<Land> landSet = new HashSet<>();
        landSet.add(new Land(land.x + 1, land.z));
        landSet.add(new Land(land.x - 1, land.z));
        landSet.add(new Land(land.x, land.z + 1));
        landSet.add(new Land(land.x, land.z - 1));
        return landSet;
    }

    public static int getX(@NotNull Location location) {
        return Math.floorDiv(location.getBlockX(), LAND_SIDE_LENGTH);
    }

    public static int getZ(@NotNull Location location) {
        return Math.floorDiv(location.getBlockZ(), LAND_SIDE_LENGTH);
    }

}
