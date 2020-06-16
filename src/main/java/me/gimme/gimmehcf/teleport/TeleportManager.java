package me.gimme.gimmehcf.teleport;

import me.gimme.gimmecore.manager.WarmupActionManager;
import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.hooks.GimmeCoreHook;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class TeleportManager implements Listener {

    private Plugin plugin;
    private LanguageManager languageManager;
    private WarmupActionManager warmupActionManager;

    public TeleportManager(Plugin plugin, LanguageManager languageManager, GimmeCoreHook gimmeCoreHook) {
        this.plugin = plugin;
        this.languageManager = languageManager;
        this.warmupActionManager = gimmeCoreHook.getWarmupActionManager();
    }

    /**
     * Schedules the player to teleport after a certain delay (defined in config), unless they move outside the block
     * that they started on or take damage.
     *
     * @param player         the player to be teleported
     * @param targetLocation the location to teleport to
     */
    public void teleport(@NotNull Player player, @NotNull Location targetLocation) {
        int delay = plugin.getConfig().getInt(Config.TELEPORT_DELAY.getPath());

        if (delay <= 0) {
            player.teleport(targetLocation);
            return;
        }

        warmupActionManager.startWarmupAction(player, delay, true, true, true,
                (time) -> languageManager.get(Message.TELEPORT_TELEPORTING_TITLE)
                        .replace(Placeholder.TIME, String.valueOf(time)).toString(),
                () -> player.teleport(targetLocation));
    }

}
