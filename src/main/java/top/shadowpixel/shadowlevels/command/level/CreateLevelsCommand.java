package top.shadowpixel.shadowlevels.command.level;

import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.command.subcommand.ConsoleCommand;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import static top.shadowpixel.shadowlevels.util.CommandUtils.checkArgument;

public class CreateLevelsCommand extends ConsoleCommand {

    private final ShadowLevels plugin;

    public CreateLevelsCommand() {
        super("CreateLevel", "CreateLevelSystem", "cls");
        this.plugin = ShadowLevels.getInstance();
    }

    @Override
    public void execute(@NotNull ConsoleCommandSender sender, @NotNull String label, String[] arguments) {
        if (!checkArgument(sender, arguments, 1)) {
            return;
        }

        if (plugin.getLevel(arguments[0]) != null) {
            send(LocaleUtils.getMessage(sender, "Messages.Levels.Existed"));
            return;
        }

        LevelManager.getInstance().create(arguments[0]);
    }
}
