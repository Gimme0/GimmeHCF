package me.gimme.gimmehcf.command.factionadmin;

import me.gimme.gimmehcf.faction.FactionManager;
import me.gimme.gimmehcf.language.LanguageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Test command meant to be used during development only.
 */
public class FATestCommand extends FACommand {

    private FactionManager factionManager;
    private Plugin plugin;

    public FATestCommand(FactionManager factionManager, LanguageManager languageManager, Plugin plugin) {
        super("test", languageManager);
        this.minArgs = 0;
        this.maxArgs = 0;

        this.factionManager = factionManager;
        this.plugin = plugin;
    }

    @Override
    protected @Nullable String execute(CommandSender sender, String[] args) {

        return "Version: " + plugin.getServer().getVersion() + newLine +
                "Bukkit version: " + plugin.getServer().getBukkitVersion() + newLine +
                "Package name: " + plugin.getServer().getClass().getPackage().getName();

        //sender.sendMessage("Width: " + MinecraftFont.Font.getChar(args[3].charAt(0)).getWidth());
        //sender.sendMessage("Width: " + MinecraftFont.Font.getWidth(args[3]));

        /*return new ChatTableBuilder()
                .setEllipsize(true)
                .addCol(ChatTableBuilder.Alignment.LEFT, 80)
                .addCol(ChatTableBuilder.Alignment.LEFT, 80)
                .addFormattedRow("" + ChatColor.BOLD + ChatColor.UNDERLINE, "Na", "Value")
                .addRow()
                .addRow(args[0], args[1])
                .addRow(args[2], args[3])
                .build();*/

        /*StringBuilder sb = new StringBuilder();
        for (TableBuilder.DefaultFontInfo dfi : TableBuilder.DefaultFontInfo.values()) {
            int length1 = dfi.getLength();
            MapFont.CharacterSprite characterSprite = MinecraftFont.Font.getChar(dfi.getCharacter());
            if (characterSprite == null) {
                sb.append(dfi.getCharacter()).append(" ").append(length1).append(" ").append("null").append("   ");
                continue;
            }
            int length2 = characterSprite.getWidth() + 1;
            if (length1 != length2)
                sb.append(dfi.getCharacter()).append(" ").append(length1).append(" ").append(length2).append("   ");
        }
        return sb.toString();*/
    }

}
