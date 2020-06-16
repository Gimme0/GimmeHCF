package me.gimme.gimmehcf.faction;

import me.gimme.gimmecore.scoreboard.PerPlayerScoreboardProvider;
import me.gimme.gimmehcf.events.PlayerJoinFactionEvent;
import me.gimme.gimmehcf.events.PlayerLeaveFactionEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

/**
 * Contains all active player scoreboards for this plugin.
 */
public class TeamColorManager implements Listener {

    private Server server;
    private FactionManager factionManager;

    public TeamColorManager(@NotNull Server server, @NotNull FactionManager factionManager) {
        this.server = server;
        this.factionManager = factionManager;
    }

    /**
     * Registers all the teams on the player's scoreboard.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Scoreboard scoreboard = PerPlayerScoreboardProvider.setupScoreboard(player);

        Team you = scoreboard.registerNewTeam(Relation.YOU.name());
        you.setCanSeeFriendlyInvisibles(true);
        you.setColor(factionManager.getRelationColor(Relation.YOU));
        you.setPrefix(factionManager.getRelationColor(Relation.YOU).toString()); //TODO check if setColor or setPrefix should be used
        //scoreboard.registerNewTeam(Relation.ALLY.name()).setPrefix(factionManager.getRelationColor(Relation.ALLY).toString());
        //scoreboard.registerNewTeam(Relation.FOCUS.name()).setPrefix(factionManager.getRelationColor(Relation.FOCUS).toString());
        //scoreboard.registerNewTeam(Relation.ENEMY.name()).setPrefix(factionManager.getRelationColor(Relation.ENEMY).toString());
        //scoreboard.registerNewTeam(Relation.NEUTRAL.name()).setPrefix(factionManager.getRelationColor(Relation.NEUTRAL).toString());
        scoreboard.registerNewTeam(Relation.ALLY.name()).setColor(factionManager.getRelationColor(Relation.ALLY));
        scoreboard.registerNewTeam(Relation.FOCUS.name()).setColor(factionManager.getRelationColor(Relation.FOCUS));
        scoreboard.registerNewTeam(Relation.ENEMY.name()).setColor(factionManager.getRelationColor(Relation.ENEMY));
        scoreboard.registerNewTeam(Relation.NEUTRAL.name()).setColor(factionManager.getRelationColor(Relation.NEUTRAL));

        for (Player p : server.getOnlinePlayers()) {
            add(player, p);
            add(p, player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        for (Player p : server.getOnlinePlayers()) {
            remove(p, event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerJoinFaction(PlayerJoinFactionEvent event) {
        update(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerLeaveFaction(PlayerLeaveFactionEvent event) {
        OfflinePlayer offlinePlayer = event.getPlayer();
        if (!offlinePlayer.isOnline()) return;
        assert offlinePlayer.getPlayer() != null;
        update(offlinePlayer.getPlayer());
    }

    private void add(@NotNull Player player, @NotNull Player addPlayer) {
        Scoreboard scoreboard = player.getScoreboard();

        Relation relation = factionManager.getRelation(addPlayer, player);
        Team relationTeam = scoreboard.getTeam(relation.name());
        assert relationTeam != null;
        relationTeam.addEntry(addPlayer.getName());
    }

    private void remove(@NotNull Player player, @NotNull Player removePlayer) {
        Scoreboard scoreboard = player.getScoreboard();

        Relation relation = factionManager.getRelation(removePlayer, player);
        Team relationTeam = scoreboard.getTeam(relation.name());
        assert relationTeam != null;
        relationTeam.removeEntry(removePlayer.getName());
    }

    private void update(@NotNull Player player) {
        for (Player p : server.getOnlinePlayers()) {
            remove(player, p);
            add(player, p);
            remove(p, player);
            add(p, player);
        }
    }

}
