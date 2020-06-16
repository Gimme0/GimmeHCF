package me.gimme.gimmehcf.faction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.gimme.gimmehcf.util.LocationAdapter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles loading and saving faction from and to json files for persistence.
 */
class FactionLoader {

    private static final String FACTIONS_FILE_PATH = "data/factions.json";
    private static final String SYSTEM_FACTIONS_FILE_PATH = "data/system_factions.json";

    private File factionsFile;
    private File systemFactionsFile;

    FactionLoader(File pluginDataFolder) {
        factionsFile = new File(pluginDataFolder, FACTIONS_FILE_PATH);
        systemFactionsFile = new File(pluginDataFolder, SYSTEM_FACTIONS_FILE_PATH);
    }

    /**
     * Loads all saved factions and system factions from their respective file into the plugin.
     * This overwrites all factions currently loaded and should only be used when the plugin first gets
     * enabled {@link Plugin#onEnable()}.
     */
    void loadFactions(@NotNull Map<String, Faction> factionByName, @NotNull Map<String, SysFaction> systemFactionByName) {
        try {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Location.class, new LocationAdapter())
                    .create();

            // Load regular factions
            if (factionsFile.isFile()) {
                Reader reader = new FileReader(factionsFile);
                factionByName.putAll(gson.fromJson(reader, new TypeToken<Map<String, Faction>>() {
                }.getType()));
                reader.close();
            }

            // Load system factions
            saveDefaultSystemFactions();
            if (systemFactionsFile.isFile()) {
                Reader reader = new FileReader(systemFactionsFile);
                systemFactionByName.putAll(gson.fromJson(reader, new TypeToken<Map<String, SysFaction>>() {
                }.getType()));
                reader.close();

                for (String systemFaction : systemFactionByName.keySet()) {
                    factionByName.put(systemFaction, systemFactionByName.get(systemFaction));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current state of all factions and system factions to their respective file.
     * Should be called in {@link Plugin#onDisable()} and is recommended to be called in a constant interval
     * to avoid losing progress in the event of a crash.
     */
    void saveFactions(@NotNull Map<String, Faction> factionByName, @NotNull Map<String, SysFaction> systemFactionByName) {
        Map<String, Faction> factionByNameSnapshot = new HashMap<>(factionByName);
        Map<String, SysFaction> systemFactionByNameSnapshot = new HashMap<>(systemFactionByName);

        // Round dtr numbers to one decimal, for cleaner numbers in storage and potentially avoiding error accumulation
        // in the long run
        for (Faction faction : factionByNameSnapshot.values()) {
            faction.setDtr(Math.round(faction.getDtr() * 10) / 10d);
        }

        // Separate system factions
        for (String systemFaction : systemFactionByNameSnapshot.keySet()) {
            factionByNameSnapshot.remove(systemFaction);
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Location.class, new LocationAdapter())
                .create();

        String factionByNameJson = gson.toJson(factionByNameSnapshot);
        String systemFactionByNameJson = gson.toJson(systemFactionByNameSnapshot);

        try {
            Files.createDirectories(factionsFile.toPath().getParent());
            Files.write(factionsFile.toPath(), factionByNameJson.getBytes(StandardCharsets.UTF_8));

            Files.createDirectories(systemFactionsFile.toPath().getParent());
            Files.write(systemFactionsFile.toPath(), systemFactionByNameJson.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultSystemFactions() {
        if (systemFactionsFile.isFile()) return;

        SysFaction wilderness = new SysFaction("WILDERNESS", ChatColor.DARK_GREEN);
        wilderness.setMapMarker("-");
        wilderness.setMapMarkerColor(ChatColor.GRAY);
        //wilderness.setDescription("It\'s dangerous to go alone.");
        wilderness.setFlag(Flag.GRIEF, true);
        wilderness.setFlag(Flag.EXPLOSION, true);
        wilderness.setFlag(Flag.INTERACT, true);
        wilderness.setFlag(Flag.DAMAGE, true);
        wilderness.setFlag(Flag.HUNGER, true);
        wilderness.setFlag(Flag.CLAIM, true);
        wilderness.setFlag(Flag.MOBS, true);
        wilderness.setFlag(Flag.PEARL, true);
        wilderness.setFlag(Flag.GREETING, true);

        SysFaction grayzone = new SysFaction("GRAYZONE", ChatColor.DARK_GRAY);
        grayzone.setFlag(Flag.GRIEF, true);
        grayzone.setFlag(Flag.EXPLOSION, true);
        grayzone.setFlag(Flag.INTERACT, true);
        grayzone.setFlag(Flag.DAMAGE, true);
        grayzone.setFlag(Flag.HUNGER, true);
        grayzone.setFlag(Flag.CLAIM, false);
        grayzone.setFlag(Flag.MOBS, true);
        grayzone.setFlag(Flag.PEARL, true);
        grayzone.setFlag(Flag.GREETING, true);

        SysFaction safezone = new SysFaction("SAFEZONE", ChatColor.GOLD);
        //safezone.setDescription("No one can harm you here.");
        safezone.setFlag(Flag.GRIEF, false);
        safezone.setFlag(Flag.EXPLOSION, false);
        safezone.setFlag(Flag.INTERACT, true);
        safezone.setFlag(Flag.DAMAGE, false);
        safezone.setFlag(Flag.HUNGER, false);
        safezone.setFlag(Flag.CLAIM, false);
        safezone.setFlag(Flag.MOBS, false);
        safezone.setFlag(Flag.PEARL, false);
        safezone.setFlag(Flag.GREETING, true);

        SysFaction warzone = new SysFaction("WARZONE", ChatColor.DARK_RED);
        //warzone.setDescription("Watch out for other players!");
        warzone.setFlag(Flag.GRIEF, false);
        warzone.setFlag(Flag.EXPLOSION, false);
        warzone.setFlag(Flag.INTERACT, true);
        warzone.setFlag(Flag.DAMAGE, true);
        warzone.setFlag(Flag.HUNGER, true);
        warzone.setFlag(Flag.CLAIM, false);
        warzone.setFlag(Flag.MOBS, false);
        warzone.setFlag(Flag.PEARL, true);
        warzone.setFlag(Flag.GREETING, true);

        Map<String, SysFaction> map = new HashMap<>();
        map.put(wilderness.getId(), wilderness);
        map.put(grayzone.getId(), grayzone);
        map.put(safezone.getId(), safezone);
        map.put(warzone.getId(), warzone);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Location.class, new LocationAdapter())
                .create();

        String json = gson.toJson(map);

        try {
            Files.createDirectories(systemFactionsFile.toPath().getParent());
            Files.write(systemFactionsFile.toPath(), json.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
