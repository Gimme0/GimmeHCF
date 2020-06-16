package me.gimme.gimmehcf.listeners.territorial;

import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.events.PlayerTerritoryEnterEvent;
import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.faction.Flag;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GreetingListener implements Listener {

    private FactionManager factionManager;
    private LanguageManager languageManager;
    private FileConfiguration config;

    public GreetingListener(FactionManager factionManager, LanguageManager languageManager, FileConfiguration config) {
        this.factionManager = factionManager;
        this.languageManager = languageManager;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerTerritoryEnter(PlayerTerritoryEnterEvent event) {
        if (event.isCancelled()) return;
        if (!factionManager.getLandFlag(Flag.GREETING, event.getTo())) return;

        String message = languageManager.get(Message.INFO_ENTERING_FACTION_GREETING)
                .replace(Placeholder.FACTION, factionManager.getDisplayName(event.getTo(), event.getPlayer())).toString();

        if (config.getBoolean(Config.FLAGS_GREETING_ON_SCREEN.getPath())) {
            int fadeIn = config.getInt(Config.FLAGS_GREETING_FADE_IN.getPath());
            int stay = config.getInt(Config.FLAGS_GREETING_STAY.getPath());
            int fadeOut = config.getInt(Config.FLAGS_GREETING_FADE_OUT.getPath());
            event.getPlayer().sendTitle("", message, fadeIn, stay, fadeOut);
        } else {
            event.getPlayer().sendMessage(message);
        }
    }

}
