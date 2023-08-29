package top.shadowpixel.shadowlevels.command.reward;

import lombok.var;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.ConsoleCommand;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.reward.RewardManager;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateRewardCommand extends ConsoleCommand {

    private final ShadowLevels plugin;

    public CreateRewardCommand() {
        super("CreateReward", "cr");
        plugin = ShadowLevels.getInstance();
    }

    @Override
    public void execute(@NotNull ConsoleCommandSender sender, @NotNull String label, String[] arguments) {
        if (arguments.length < 2) {
            send(LocaleUtils.getCmdMessage(sender, "Errors.Params-Error"));
            return;
        }

        var level = plugin.getLevel(arguments[1]);
        if (level == null) {
            send(LocaleUtils.getCmdMessage(sender, "Errors.Level-Not-Found"));
            return;
        }

        RewardManager.getInstance().create(arguments[0], level);
    }

    @Override
    public @Nullable List<String> complete(@NotNull ConsoleCommandSender sender, @NotNull String label, String[] arguments) {
        if (arguments.length == 2) {
            return new ArrayList<>(LevelManager.getInstance().getLoadedLevels().keySet());
        }

        return Collections.emptyList();
    }
}
