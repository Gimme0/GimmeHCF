package me.gimme.gimmehcf.command.event;

import me.gimme.gimmehcf.command.ArgPlaceholder;
import me.gimme.gimmehcf.gameevent.GameEventManager;
import me.gimme.gimmehcf.language.LanguageManager;
import me.gimme.gimmehcf.language.Message;
import me.gimme.gimmehcf.language.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EventSOTWCommand extends EventCommand {

    public EventSOTWCommand(@NotNull LanguageManager languageManager, @NotNull GameEventManager gameEventManager) {
        super("sotw", languageManager, gameEventManager);
        this.argsUsage = "<delay> <duration>";
        this.argsAlternatives.add("0");
        this.argsAlternatives.add(ArgPlaceholder.WILDCARD.getPlaceholder() + " 0");
        this.minArgs = 2;
        this.maxArgs = 2;
        this.playerOnly = false;
        this.description = languageManager.get(Message.DESCRIPTION_EVENT_SOTW).toString();
    }

    @Override
    protected @Nullable String execute(@NotNull CommandSender sender, String[] args) {
        String delayArg = args[0];
        String durationArg = args[1];

        double delay;
        double duration;
        try {
            delay = Double.parseDouble(delayArg);
        } catch (NumberFormatException e) {
            return errorMessageWithUsage(CommandError.NOT_A_NUMBER, delayArg);
        } try {
            duration = Double.parseDouble(durationArg);
        } catch (NumberFormatException e) {
            return errorMessageWithUsage(CommandError.NOT_A_NUMBER, durationArg);
        }

        gameEventManager.startSOTW(delay, duration);
        return successMessage(languageManager.get(Message.SUCCESS_EVENT_SOTW_SET)
                .replace(Placeholder.N, delayArg).toString());
    }
}
