package me.gimme.gimmehcf.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatsManager {

    private static final String PLAYERS_FILE_PATH = "data/players.json";
    private File playersFile;

    private Map<UUID, PlayerStats> statsByPlayer = new HashMap<>();

    public PlayerStatsManager(@NotNull File pluginDataFolder) {
        this.playersFile = new File(pluginDataFolder, PLAYERS_FILE_PATH);
    }

    /**
     * Updates the kill count for the killer.
     *
     * @param killer the player that killed the victim
     */
    public void incrementKillCount(@NotNull Player killer) {
        statsByPlayer.computeIfAbsent(killer.getUniqueId(), k -> new PlayerStats());
        statsByPlayer.get(killer.getUniqueId()).incrementKills();
    }

    /**
     * Updates the death count for the victim.
     *
     * @param victim the player that was killed
     */
    public void incrementDeathCount(@NotNull Player victim) {
        statsByPlayer.computeIfAbsent(victim.getUniqueId(), k -> new PlayerStats());
        statsByPlayer.get(victim.getUniqueId()).incrementDeaths();
    }

    /**
     * Gets the stats of a player, or null if no stats registered for that player.
     *
     * @param player the player to get the stats of
     * @return the player stats, or null if no stats registered for that player
     */
    @Nullable
    public PlayerStats getStats(@NotNull UUID player) {
        return statsByPlayer.get(player);
    }

    @NotNull
    public Map<UUID, PlayerStats> getStats() {
        return statsByPlayer;
    }

    /**
     * Loads all saved player stats into the plugin. This overwrites all player stats currently loaded
     * and should only be used when the plugin first gets enabled {@link Plugin#onEnable()}.
     */
    public void loadPlayers() {
        try {

            Gson gson = new GsonBuilder().create();

            if (playersFile.isFile()) {
                Reader reader = new FileReader(playersFile);
                statsByPlayer.putAll(gson.fromJson(reader, new TypeToken<Map<UUID, PlayerStats>>() {
                }.getType()));
                reader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current state of the player stats to the json file.
     * Should be called in {@link Plugin#onDisable()} and is recommended to be called in a constant interval
     * to avoid losing progress in the event of a crash.
     */
    public void savePlayers() {
        if (statsByPlayer.isEmpty()) return;

        Map<UUID, PlayerStats> statsByPlayerSnapshot = new HashMap<>(statsByPlayer);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        String statsByPlayerJson = gson.toJson(statsByPlayerSnapshot);

        try {
            Files.createDirectories(playersFile.toPath().getParent());
            Files.write(playersFile.toPath(), statsByPlayerJson.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
