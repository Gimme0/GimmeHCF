package me.gimme.gimmehcf.language;

import org.bukkit.plugin.Plugin;

public class LanguageManager extends me.gimme.gimmecore.language.LanguageManager {

    public LanguageManager(Plugin plugin) {
        super(plugin);
        setColorCode(get(Message.COLOR_CODE).toString());
        setPlaceholderCode(get(Message.PLACEHOLDER_CODE).toString());
        setArraySplitCode(get(Message.ARRAY_SPLIT_CODE).toString());
    }

    public Text get(Message message) {
        return super.get(message);
    }

}
