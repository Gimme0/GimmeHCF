package me.gimme.gimmehcf.faction;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SysFaction extends Faction {

    private String color = "";
    private String description = "";
    private String mapMarker = "+";
    private String mapMarkerColor = "";
    private Map<String, Boolean> flags = new HashMap<>();

    public SysFaction() { // Required no-arg constructor
    }

    public SysFaction(String name) {
        this(name, ChatColor.RESET);
    }

    public SysFaction(String name, @Nullable ChatColor color) {
        super(name);
        setColor(color);
        setFlag(Flag.SYSTEM, true);
    }

    public void setColor(@Nullable ChatColor chatColor) {
        this.color = chatColor != null ? chatColor.getChar() + "" : "";
    }

    public void setMapMarkerColor(@Nullable ChatColor chatColor) {
        this.mapMarkerColor = chatColor != null ? chatColor.getChar() + "" : "";
    }

    public void clearFlags() {
        flags.clear();
    }

    public void setFlag(Flag flag, boolean value) {
        flags.put(flag.toString(), value);
    }

    public void clearFlag(Flag flag) {
        flags.remove(flag.toString());
    }

    @Override
    @Nullable
    public Boolean getFlag(Flag flag) {
        return flags.get(flag.toString());
    }

    @Override
    public String getMapMarker() {
        return (Strings.isNullOrEmpty(color) ? "" : ChatColor.getByChar(color)) + "" +
                (Strings.isNullOrEmpty(mapMarkerColor) ? "" : ChatColor.getByChar(mapMarkerColor)) +
                mapMarker;
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return ChatColor.getByChar(color) + getName();
    }

    /**
     * Returns false because system factions are never raidable.
     *
     * @return false
     */
    @Override
    public boolean isRaidable() {
        return false;
    }

}
