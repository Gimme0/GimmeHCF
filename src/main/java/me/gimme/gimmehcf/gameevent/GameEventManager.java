package me.gimme.gimmehcf.gameevent;

import me.gimme.gimmecore.scoreboard.TimerScoreboardManager;
import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.gameevent.gameevents.SOTWGameEvent;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Message;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GameEventManager {

    private Plugin plugin;
    private FileConfiguration config;
    private LanguageManager languageManager;
    private TimerScoreboardManager timerScoreboardManager;

    private Map<String, GameEvent> eventByTitle = new HashMap<>();

    public GameEventManager(Plugin plugin, LanguageManager languageManager, TimerScoreboardManager timerScoreboardManager) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.languageManager = languageManager;
        this.timerScoreboardManager = timerScoreboardManager;
    }

    /**
     * Starts the SOTW (Start of the World) event. During the countdown delay, players will be unable to leave the
     * faction that is specified in the config.
     *
     * @param delay    the delay before SOTW starts in minutes
     * @param duration the duration of SOTW in minutes
     */
    public void startSOTW(double delay, double duration) {
        startEvent(new SOTWGameEvent(config),
                languageManager.get(Message.SCOREBOARD_TIMER_TITLE_SOTW_COUNTDOWN).toString(),
                languageManager.get(Message.SCOREBOARD_TIMER_TITLE_SOTW).toString(),
                delay,
                duration);
    }

    /**
     * Starts an event for the specified duration after the specified delay.
     * Game events are registered to receive Bukkit Events during and only during the delay and full duration.
     * Both the delay and the duration countdowns will be displayed on players' timer scoreboards.
     *
     * @param gameEvent     the game event to start
     * @param delayTitle    the title of the countdown for the event, or null if the event has no countdown (e.g. blood moon)
     * @param durationTitle the title of the event when it is ongoing, or null if it has no duration (e.g. loot drop)
     * @param delay         the delay in minutes until the event starts
     * @param duration      the event duration in minutes
     */
    private void startEvent(@NotNull GameEvent gameEvent, @Nullable String delayTitle, @Nullable String durationTitle, double delay, double duration) {
        int score = config.getInt(Config.EVENT_SCORE.getPath());

        if (durationTitle != null) { // Potentially ongoing event
            timerScoreboardManager.cancelEvent(durationTitle); // Cancel ongoing event timer
            cancelEvent(durationTitle); // Cancel ongoing event
        }

        eventByTitle.put(durationTitle, gameEvent);
        plugin.getServer().getPluginManager().registerEvents(gameEvent, plugin);

        gameEvent.countdownPeriod();
        timerScoreboardManager.startEventTimer(delayTitle, Math.round(delay * 60), score,
                () -> {
                    gameEvent.start();
                    timerScoreboardManager.startEventTimer(durationTitle, Math.round(duration * 60), score,
                            () -> {
                                gameEvent.finish();
                                cancelEvent(durationTitle);
                            });
                });
    }

    private void cancelEvent(String durationTitle) {
        if (durationTitle == null) return;
        GameEvent gameEvent = eventByTitle.remove(durationTitle);
        if (gameEvent != null) HandlerList.unregisterAll(gameEvent);
    }

}
