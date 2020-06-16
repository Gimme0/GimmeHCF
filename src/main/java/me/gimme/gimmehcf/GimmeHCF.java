package me.gimme.gimmehcf;

import me.gimme.gimmecore.command.BaseCommand;
import me.gimme.gimmecore.scoreboard.TimerScoreboardManager;
import me.gimme.gimmehcf.command.CommandManager;
import me.gimme.gimmehcf.command.event.EventHelpCommand;
import me.gimme.gimmehcf.command.event.EventSOTWCommand;
import me.gimme.gimmehcf.command.factionadmin.*;
import me.gimme.gimmehcf.command.faction.*;
import me.gimme.gimmehcf.config.Config;
import me.gimme.gimmehcf.faction.*;
import me.gimme.gimmehcf.hooks.GimmeBalanceHook;
import me.gimme.gimmehcf.events.callers.PlayerTerritoryEnterEventCaller;
import me.gimme.gimmehcf.gameevent.GameEventManager;
import me.gimme.gimmehcf.hooks.GimmeCoreHook;
import me.gimme.gimmehcf.listeners.*;
import me.gimme.gimmehcf.listeners.chat.ChatListener;
import me.gimme.gimmehcf.listeners.chat.EventBroadcaster;
import me.gimme.gimmehcf.listeners.territorial.*;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.player.*;
import me.gimme.gimmehcf.teleport.TeleportManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static me.gimme.gimmehcf.command.ArgPlaceholder.*;

public final class GimmeHCF extends JavaPlugin {

    public static final String PERMISSION_PATH = "gimmehcf";
    public static final String COMMAND_FACTION = "f";
    public static final String COMMAND_FACTION_ADMIN = "fa";
    public static final String COMMAND_EVENT = "event";

    public static final long PLUGIN_START_TIME_MILLIS = System.currentTimeMillis();

    private GimmeCoreHook gimmeCoreHook;
    private GimmeBalanceHook gimmeBalanceHook;
    private LanguageManager languageManager;
    private CommandManager commandManager;
    private TeleportManager teleportManager;
    private FactionManager factionManager;
    private PlayerStatsManager playerStatsManager;
    private OnlineFactionsListener onlineFactionsListener;
    private GhostManager ghostManager;
    private DtrRegenManager dtrRegenManager;
    private TeamColorManager teamColorManager;
    private TimerScoreboardManager timerScoreboardManager;
    private GameEventManager gameEventManager;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        gimmeCoreHook = new GimmeCoreHook(getServer().getPluginManager());
        gimmeBalanceHook = new GimmeBalanceHook(getServer().getPluginManager());
        languageManager = new LanguageManager(this);
        commandManager = new CommandManager(this, languageManager);
        teleportManager = new TeleportManager(this, languageManager, gimmeCoreHook);
        factionManager = new FactionManager(this);
        playerStatsManager = new PlayerStatsManager(getDataFolder());
        onlineFactionsListener = new OnlineFactionsListener(factionManager);
        ghostManager = new GhostManager(this, languageManager);
        dtrRegenManager = new DtrRegenManager(this, factionManager, onlineFactionsListener, ghostManager);
        teamColorManager = new TeamColorManager(getServer(), factionManager);
        timerScoreboardManager = new TimerScoreboardManager(this, languageManager.get(Message.HEADER_TIMERS_SCOREBOARD).toString());
        gameEventManager = new GameEventManager(this, languageManager, timerScoreboardManager);

        registerCommands();
        registerListeners();

        // Allow combat logging in zones where you cannot take damage
        gimmeBalanceHook.addAllowedCombatLogCondition(player -> !factionManager.getLandFlag(Flag.DAMAGE, player.getLocation()));

        getLogger().info("Loading factions...");
        factionManager.loadFactions();
        getLogger().info("Loading player stats...");
        playerStatsManager.loadPlayers();

        startSaveTask(getConfig().getDouble(Config.AUTO_SAVE_PERIOD.getPath()),
                getConfig().getBoolean(Config.AUTO_SAVE_LOGGING.getPath()));

        getLogger().info("Done");
    }

    @Override
    public void onDisable() {
        getLogger().info("Saving factions...");
        factionManager.saveFactions();
        getLogger().info("Saving player stats...");
        playerStatsManager.savePlayers();
        getLogger().info("Done");
    }

    private void registerCommands() {
        commandManager.registerPlaceholder(PLAYERS.getPlaceholder(), getServer().getOnlinePlayers(), Player::getName);
        commandManager.registerPlaceholder(FACTIONS.getPlaceholder(), factionManager.getFactions(), Faction::getName);
        commandManager.registerPlaceholder(SYSTEM_FACTIONS.getPlaceholder(), factionManager.getSystemFactions(), Faction::getName);
        commandManager.registerPlaceholder(MATERIAL.getPlaceholder(), List.of(Material.values()), material -> material.getKey().getKey());

        registerCommand(new FHelpCommand(commandManager, languageManager));
        registerCommand(new FCreateCommand(factionManager, languageManager, getConfig()));
        registerCommand(new FDisbandCommand(factionManager, languageManager, getConfig()));
        registerCommand(new FClaimCommand(factionManager, languageManager, getConfig()));
        registerCommand(new FUnclaimCommand(factionManager, languageManager, getConfig()));
        registerCommand(new FInviteCommand(factionManager, languageManager, getServer()));
        registerCommand(new FUninviteCommand(factionManager, languageManager));
        registerCommand(new FJoinCommand(factionManager, languageManager, getConfig()));
        registerCommand(new FLeaveCommand(factionManager, languageManager, getConfig()));
        registerCommand(new FKickCommand(factionManager, languageManager, gimmeBalanceHook, getServer()));
        registerCommand(new FPromoteCommand(factionManager, languageManager, getServer()));
        registerCommand(new FDemoteCommand(factionManager, languageManager, getServer()));
        registerCommand(new FOwnerCommand(factionManager, languageManager, getServer()));
        registerCommand(new FSethomeCommand(factionManager, languageManager));
        registerCommand(new FHomeCommand(factionManager, languageManager, teleportManager));
        registerCommand(new FShowCommand(factionManager, languageManager, dtrRegenManager, getConfig(), getServer()));
        registerCommand(new FListCommand(factionManager, languageManager, onlineFactionsListener));
        registerCommand(new FTopCommand(factionManager, languageManager, onlineFactionsListener, playerStatsManager, getServer()));
        registerCommand(new FMapCommand(factionManager, languageManager));
        registerCommand(new FAllyCommand(factionManager, languageManager, getServer()));
        registerCommand(new FFocusCommand(factionManager, languageManager));

        registerCommand(new FAHelpCommand(commandManager, languageManager));
        registerCommand(new FACreatesysCommand(factionManager, languageManager));
        registerCommand(new FADisbandCommand(factionManager, languageManager));
        registerCommand(new FAClaimCommand(factionManager, languageManager));
        registerCommand(new FAUnclaimCommand(factionManager, languageManager));
        registerCommand(new FASetdtrCommand(factionManager, languageManager));
        registerCommand(new FAFreezeCommand(factionManager, languageManager, dtrRegenManager));
        registerCommand(new FAFlagsCommand(factionManager, languageManager));
        registerCommand(new FASetflagCommand(factionManager, languageManager));
        registerCommand(new FAClearflagsCommand(factionManager, languageManager));
        registerCommand(new FAReviveCommand(languageManager, ghostManager, getServer()));
        registerCommand(new FABlockRegCommand(factionManager, languageManager));

        registerCommand(new EventHelpCommand(commandManager, languageManager));
        registerCommand(new EventSOTWCommand(languageManager, gameEventManager));

        registerCommand(new FATestCommand(factionManager, languageManager, this)); //TODO remove test command
    }

    private void registerCommand(BaseCommand command) {
        commandManager.register(command);
    }

    private void registerListeners() {
        registerListener(teleportManager);
        registerListener(onlineFactionsListener);
        registerListener(ghostManager);
        registerListener(dtrRegenManager);
        registerListener(teamColorManager);
        registerListener(timerScoreboardManager);

        registerListener(new PlayerTerritoryEnterEventCaller(factionManager));

        registerListener(new GriefListener(this, factionManager, getConfig()));
        registerListener(new DamageListener(factionManager, getConfig()));
        registerListener(new HungerListener(factionManager));
        registerListener(new InteractListener(factionManager, getConfig()));
        registerListener(new MobSpawnListener(factionManager, getConfig()));
        registerListener(new PearlListener(factionManager, languageManager));
        registerListener(new GreetingListener(factionManager, languageManager, getConfig()));

        registerListener(new ChatListener(factionManager));
        registerListener(new EventBroadcaster(factionManager, languageManager, getServer(), getConfig()));
        registerListener(new KillDeathListener(this, factionManager, playerStatsManager));
        registerListener(new RaidableListener(dtrRegenManager, getServer(), getConfig()));
        if (gimmeBalanceHook.isActive()) registerListener(new CombatRestrictionListener(getConfig(), gimmeBalanceHook));
    }

    private void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void startSaveTask(double period, boolean logging) {
        long timer = Math.round(period * 60 * 20);
        if (timer <= 0) return;
        getLogger().info("Auto-saving enabled: every " + period + " minutes");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (logging) getLogger().info("Saving...");
                factionManager.saveFactions();
                playerStatsManager.savePlayers();
            }
        }.runTaskTimerAsynchronously(this, timer, timer);
    }

}
