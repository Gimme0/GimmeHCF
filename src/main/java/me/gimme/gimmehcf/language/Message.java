package me.gimme.gimmehcf.language;

import me.gimme.gimmecore.language.LanguagePath;
import org.jetbrains.annotations.Nullable;

public enum Message implements LanguagePath {

    COLOR_CODE("color-code"),
    PLACEHOLDER_CODE("placeholder-code"),
    ARRAY_SPLIT_CODE("array-split-code"),

    ANNOUNCEMENT("announcement"),

    ANNOUNCEMENT_GLOBAL("global", ANNOUNCEMENT),
    ANNOUNCEMENT_RAIDABLE("raidable", ANNOUNCEMENT_GLOBAL),
    ANNOUNCEMENT_DEATH("death", ANNOUNCEMENT_GLOBAL),
    ANNOUNCEMENT_DEATH_DEFAULT("default", ANNOUNCEMENT_DEATH),
    ANNOUNCEMENT_DEATH_PLAYER_KILL("player-kill", ANNOUNCEMENT_DEATH),

    ANNOUNCEMENT_FACTION("faction", ANNOUNCEMENT),
    INFO_JOINED_YOUR_FACTION("joined-your-faction", ANNOUNCEMENT_FACTION),
    INFO_LEFT_YOUR_FACTION("left-your-faction", ANNOUNCEMENT_FACTION),
    INFO_KICKED_FROM_YOUR_FACTION("kicked-from-your-faction", ANNOUNCEMENT_FACTION),
    INFO_ALLY_REQUEST_FROM("ally-request-from", ANNOUNCEMENT_FACTION),
    INFO_NOW_ALLIED_WITH("now-allied-with", ANNOUNCEMENT_FACTION),
    INFO_NO_LONGER_ALLIED_WITH("no-longer-allied-with", ANNOUNCEMENT_FACTION),
    INFO_FACTION_FOCUS_SET("faction-focus-set", ANNOUNCEMENT_FACTION),
    INFO_FACTION_FOCUS_REMOVED("faction-focus-removed", ANNOUNCEMENT_FACTION),

    ANNOUNCEMENT_PERSONAL("personal", ANNOUNCEMENT),
    INFO_ENTERING_FACTION_GREETING("greeting", ANNOUNCEMENT_PERSONAL),
    INFO_YOU_HAVE_BEEN_INVITED("invited-to", ANNOUNCEMENT_PERSONAL),
    INFO_YOU_HAVE_BEEN_KICKED("kicked-from", ANNOUNCEMENT_PERSONAL),
    INFO_YOUR_FACTION_DISBANDED("your-faction-disbanded", ANNOUNCEMENT_PERSONAL),
    INFO_ACTION_BLOCKED("action-blocked", ANNOUNCEMENT_PERSONAL),

    DEATH_TIMER("death-timer"),
    DEATH_TIMER_ITEM_NAME("item-name", DEATH_TIMER),
    DEATH_TIMER_ITEM_LORE("item-lore", DEATH_TIMER),
    DEATH_TIMER_SCREEN_TEXT("screen-text", DEATH_TIMER),
    DEATH_TIMER_PLAYER_LIST("player-list", DEATH_TIMER),
    DEATH_TIMER_RESPAWNED("respawned", DEATH_TIMER),
    DEATH_TIMER_BLOCKED_COMMAND("blocked-command", DEATH_TIMER),

    SCOREBOARD_TIMER_TITLE("scoreboard-timer-title"),
    SCOREBOARD_TIMER_TITLE_SOTW("sotw", SCOREBOARD_TIMER_TITLE),
    SCOREBOARD_TIMER_TITLE_SOTW_COUNTDOWN("sotw-countdown", SCOREBOARD_TIMER_TITLE),

    TELEPORT("teleport"),
    TELEPORT_TELEPORTING_TITLE("teleporting-title", TELEPORT),
    TELEPORT_TELEPORTING_CHAT("teleporting-chat", TELEPORT),
    TELEPORT_CANCELED_BY_MOVEMENT("canceled-by-movement", TELEPORT),
    TELEPORT_CANCELED_BY_DAMAGE("canceled-by-damage", TELEPORT),

    HEADERS("headers"),
    HEADER_TIMERS_SCOREBOARD("timers-scoreboard", HEADERS),
    HEADER_F_HELP("f-help", HEADERS),
    HEADER_F_MAP("f-map", HEADERS),
    HEADER_F_SHOW("f-show", HEADERS),
    HEADER_F_LIST_ONLINE("f-list-online", HEADERS),
    HEADER_F_LIST_DTR("f-list-dtr", HEADERS),
    HEADER_F_TOP_KILLS("f-top-kills", HEADERS),
    HEADER_F_TOP_KD("f-top-kd", HEADERS),
    HEADER_F_TOP_PLAYERS("f-top-players", HEADERS),
    HEADER_FA_HELP("fa-help", HEADERS),
    HEADER_FA_FLAGS("fa-flags", HEADERS),
    HEADER_EVENT_HELP("event-help", HEADERS),

    COMMAND("command"),
    ERROR_ILLEGAL_CHARACTERS("illegal-characters", COMMAND),
    ERROR_INVALID_ARGUMENT("invalid-argument", COMMAND),
    ERROR_NO_PERMISSION("no-permission", COMMAND),
    ERROR_NOT_A_COLOR("not-a-color", COMMAND),
    ERROR_NOT_A_NUMBER("not-a-number", COMMAND),
    ERROR_PLAYER_ONLY("player-only", COMMAND),
    ERROR_TOO_FEW_ARGUMENTS("too-few-arguments", COMMAND),
    ERROR_TOO_MANY_ARGUMENTS("too-many-arguments", COMMAND),
    ERROR_UNKNOWN("unknown", COMMAND),
    ERROR_PAGE_OOB("page-oob", COMMAND),
    ERROR_YOU_IN_COMBAT("you-in-combat", COMMAND),
    ERROR_THEY_IN_COMBAT("they-in-combat", COMMAND),

    FACTION("faction"),

    F_COMMAND("command", FACTION),
    ERROR_NOT_IN_YOUR_FACTION("not-in-your-faction", F_COMMAND),
    ERROR_ALREADY_IN_FACTION("already-in-faction", F_COMMAND),
    ERROR_FACTION_NOT_FOUND("faction-not-found", F_COMMAND),
    ERROR_SYSFACTION_NOT_FOUND("system-faction-not-found", F_COMMAND),
    ERROR_CURRENT_FACTION_MISMATCH("current-faction-mismatch", F_COMMAND),
    ERROR_FACTION_RAIDABLE("faction-raidable", F_COMMAND),
    ERROR_MATERIAL_NOT_FOUND("material-not-found", F_COMMAND),

    F_COMMAND_ALLY("ally-command", FACTION),
    DESCRIPTION_F_ALLY("description", F_COMMAND_ALLY),
    SUCCESS_SENT_ALLY_REQUEST("sent-ally-request", F_COMMAND_ALLY),
    SUCCESS_REMOVED_ALLY_REQUEST("removed-ally-request", F_COMMAND_ALLY),

    F_COMMAND_FOCUS("focus-command", FACTION),
    DESCRIPTION_F_FOCUS("description", F_COMMAND_FOCUS),

    F_COMMAND_CLAIM("claim-command", FACTION),
    DESCRIPTION_F_CLAIM("description", F_COMMAND_CLAIM),
    ERROR_CLAIM_PERMISSION("no-permission", F_COMMAND_CLAIM),
    ERROR_CLAIM_NOT_ADJACENT("not-adjacent", F_COMMAND_CLAIM),
    ERROR_CLAIM_MAX_REACHED("max-reached", F_COMMAND_CLAIM),
    SUCCESS_CLAIMED_ONE("claimed-one", F_COMMAND_CLAIM),
    SUCCESS_OVERCLAIMED_ONE("overclaimed-one", F_COMMAND_CLAIM),

    F_COMMAND_UNCLAIM("unclaim-command", FACTION),
    DESCRIPTION_F_UNCLAIM("description", F_COMMAND_UNCLAIM),
    ERROR_WOULD_BE_NON_ADJACENT("not-adjacent", F_COMMAND_UNCLAIM),
    SUCCESS_UNCLAIMED_ONE("unclaimed-one", F_COMMAND_UNCLAIM),
    SUCCESS_UNCLAIMED_ALL("unclaimed-all", F_COMMAND_UNCLAIM),

    F_COMMAND_CREATE("create-command", FACTION),
    DESCRIPTION_F_CREATE("description", F_COMMAND_CREATE),
    ERROR_NAME_TAKEN("name-taken", F_COMMAND_CREATE),
    ERROR_NAME_TOO_SHORT("name-too-short", F_COMMAND_CREATE),
    ERROR_NAME_TOO_LONG("name-too-long", F_COMMAND_CREATE),
    ERROR_NAME_ILLEGAL_PHRASE("illegal-phrase", F_COMMAND_CREATE),
    SUCCESS_CREATED_FACTION("created-faction", F_COMMAND_CREATE),

    F_COMMAND_DISBAND("disband-command", FACTION),
    DESCRIPTION_F_DISBAND("description", F_COMMAND_DISBAND),
    SUCCESS_DISBANDED_FACTION("disbanded-faction", F_COMMAND_DISBAND),

    F_COMMAND_HELP("help-command", FACTION),
    DESCRIPTION_F_HELP("description", F_COMMAND_HELP),

    F_COMMAND_HOME("home-command", FACTION),
    DESCRIPTION_F_HOME("description", F_COMMAND_HOME),
    ERROR_NO_HOME_SET("no-home-set", F_COMMAND_HOME),

    F_COMMAND_INVITE("invite-command", FACTION),
    DESCRIPTION_F_INVITE("description", F_COMMAND_INVITE),
    ERROR_ALREADY_INVITED("already-invited", F_COMMAND_INVITE),
    SUCCESS_INVITED_PLAYER("invited-player", F_COMMAND_INVITE),

    F_COMMAND_UNINVITE("uninvite-command", FACTION),
    DESCRIPTION_F_UNINVITE("description", F_COMMAND_UNINVITE),
    ERROR_PLAYER_WAS_NOT_INVITED("not-invited", F_COMMAND_UNINVITE),
    SUCCESS_UNINVITED_ONE("uninvited-one", F_COMMAND_UNINVITE),
    SUCCESS_UNINVITED_ALL("uninvited-all", F_COMMAND_UNINVITE),

    F_COMMAND_JOIN("join-command", FACTION),
    DESCRIPTION_F_JOIN("description", F_COMMAND_JOIN),
    ERROR_YOU_ARE_NOT_INVITED("not-invited", F_COMMAND_JOIN),
    ERROR_FACTION_FULL("faction-full", F_COMMAND_JOIN),
    SUCCESS_JOINED_FACTION("joined-faction", F_COMMAND_JOIN),

    F_COMMAND_LEAVE("leave-command", FACTION),
    DESCRIPTION_F_LEAVE("description", F_COMMAND_LEAVE),
    ERROR_LEAVE_WHILE_HOME("leave-while-home", F_COMMAND_LEAVE),
    ERROR_LEADER("leader", F_COMMAND_LEAVE),
    SUCCESS_LEFT_FACTION("left-faction", F_COMMAND_LEAVE),

    F_COMMAND_KICK("kick-command", FACTION),
    DESCRIPTION_F_KICK("description", F_COMMAND_KICK),
    SUCCESS_KICKED_PLAYER("kicked-player", F_COMMAND_KICK),

    F_COMMAND_PROMOTE("promote-command", FACTION),
    DESCRIPTION_F_PROMOTE("description", F_COMMAND_PROMOTE),
    ERROR_PROMOTE_ALREADY_OFFICER("already-officer", F_COMMAND_PROMOTE),
    SUCCESS_PROMOTED_PLAYER("promoted-player", F_COMMAND_PROMOTE),

    F_COMMAND_DEMOTE("demote-command", FACTION),
    DESCRIPTION_F_DEMOTE("description", F_COMMAND_DEMOTE),
    ERROR_DEMOTE_NOT_OFFICER("not-officer", F_COMMAND_DEMOTE),
    SUCCESS_DEMOTED_PLAYER("demoted-player", F_COMMAND_DEMOTE),

    F_COMMAND_OWNER("owner-command", FACTION),
    DESCRIPTION_F_OWNER("description", F_COMMAND_OWNER),
    ERROR_PROMOTE_ALREADY_OWNER("already-owner", F_COMMAND_OWNER),
    SUCCESS_MADE_OWNER("made-owner", F_COMMAND_OWNER),

    F_COMMAND_MAP("map-command", FACTION),
    DESCRIPTION_F_MAP("description", F_COMMAND_MAP),
    PLAYER_MARKER("player-marker", F_COMMAND_MAP),
    FACTION_MARKERS("faction-markers", F_COMMAND_MAP),

    F_COMMAND_SETHOME("sethome-command", FACTION),
    DESCRIPTION_F_SETHOME("description", F_COMMAND_SETHOME),
    ERROR_NOT_YOUR_LAND("not-your-land", F_COMMAND_SETHOME),
    SUCCESS_FACTION_HOME_SET("home-set", F_COMMAND_SETHOME),

    F_COMMAND_SHOW("show-command", FACTION),
    DESCRIPTION_F_SHOW("description", F_COMMAND_SHOW),

    F_COMMAND_LIST("list-command", FACTION),
    DESCRIPTION_F_LIST("description", F_COMMAND_LIST),

    F_COMMAND_TOP("top-command", FACTION),
    DESCRIPTION_F_TOP("description", F_COMMAND_TOP),

    FA_COMMAND_CLAIM("admin-claim-command", FACTION),
    DESCRIPTION_FA_CLAIM("description", FA_COMMAND_CLAIM),
    SUCCESS_ADMIN_CLAIMED_ONE("claimed-one", FA_COMMAND_CLAIM),
    SUCCESS_ADMIN_CLAIMED_MULTIPLE("claimed-multiple", FA_COMMAND_CLAIM),

    FA_COMMAND_UNCLAIM("admin-unclaim-command", FACTION),
    DESCRIPTION_FA_UNCLAIM("description", FA_COMMAND_UNCLAIM),
    SUCCESS_ADMIN_UNCLAIMED_ONE("unclaimed-one", FA_COMMAND_UNCLAIM),
    SUCCESS_ADMIN_UNCLAIMED_ALL("unclaimed-all", FA_COMMAND_UNCLAIM),
    SUCCESS_ADMIN_UNCLAIMED_MULTIPLE("unclaimed-multiple", FA_COMMAND_UNCLAIM),

    FA_COMMAND_CREATESYS("admin-createsys-command", FACTION),
    DESCRIPTION_FA_CREATESYS("description", FA_COMMAND_CREATESYS),

    FA_COMMAND_DISBAND("admin-disband-command", FACTION),
    DESCRIPTION_FA_DISBAND("description", FA_COMMAND_DISBAND),

    FA_COMMAND_FLAGS("admin-flags-command", FACTION),
    DESCRIPTION_FA_FLAGS("description", FA_COMMAND_FLAGS),

    FA_COMMAND_CLEARFLAGS("admin-clearflags-command", FACTION),
    DESCRIPTION_FA_CLEARFLAGS("description", FA_COMMAND_CLEARFLAGS),
    SUCCESS_ADMIN_CLEARED_FLAGS("cleared-flags", FA_COMMAND_CLEARFLAGS),

    FA_COMMAND_SETFLAG("admin-setflag-command", FACTION),
    DESCRIPTION_FA_SETFLAG("description", FA_COMMAND_SETFLAG),
    ERROR_INVALID_FLAG("invalid-flag", FA_COMMAND_SETFLAG),

    FA_COMMAND_FREEZE("admin-freeze-command", FACTION),
    DESCRIPTION_FA_FREEZE("description", FA_COMMAND_FREEZE),
    SUCCESS_ADMIN_FREEZE_SET("freeze-set", FA_COMMAND_FREEZE),

    FA_COMMAND_SETDTR("admin-setdtr-command", FACTION),
    DESCRIPTION_FA_SETDTR("description", FA_COMMAND_SETDTR),
    SUCCESS_ADMIN_DTR_SET("dtr-set", FA_COMMAND_SETDTR),

    FA_COMMAND_REVIVE("admin-revive-command", FACTION),
    DESCRIPTION_FA_REVIVE("description", FA_COMMAND_REVIVE),
    ERROR_PLAYER_NOT_FOUND("player-not-found", FA_COMMAND_REVIVE),
    ERROR_PLAYER_NOT_DEAD("player-not-dead", FA_COMMAND_REVIVE),
    SUCCESS_ADMIN_REVIVED("revived", FA_COMMAND_REVIVE),

    FA_COMMAND_BLOCKREG("admin-blockreg-command", FACTION),
    DESCRIPTION_FA_BLOCKREG("description", FA_COMMAND_BLOCKREG),
    SUCCESS_ADMIN_BLOCKREG_SET("blockreg-set", FA_COMMAND_BLOCKREG),
    SUCCESS_ADMIN_BLOCKREG_RESET("blockreg-reset", FA_COMMAND_BLOCKREG),

    FA_COMMAND_HELP("admin-help-command", FACTION),
    DESCRIPTION_FA_HELP("description", FA_COMMAND_HELP),

    EVENT_COMMAND_SOTW("event-sotw-command", FACTION),
    DESCRIPTION_EVENT_SOTW("description", EVENT_COMMAND_SOTW),
    SUCCESS_EVENT_SOTW_SET("sotw-set", EVENT_COMMAND_SOTW),

    EVENT_COMMAND_HELP("event-help-command", FACTION),
    DESCRIPTION_EVENT_HELP("description", EVENT_COMMAND_HELP);

    private final String key;
    private final Message parent;

    Message(String key) {
        this(key, null);
    }

    Message(String key, @Nullable Message parent) {
        this.key = key;
        this.parent = parent;
    }

    public String getPath() {
        return (parent == null ? "" : (parent.getPath() + ".")) + key;
    }

    @Override
    public String toString() {
        return getPath();
    }

}
