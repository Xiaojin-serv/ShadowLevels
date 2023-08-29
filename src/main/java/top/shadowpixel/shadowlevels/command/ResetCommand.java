package top.shadowpixel.shadowlevels.command;

import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.SubCommand;
import top.shadowpixel.shadowcore.api.util.Optional;
import top.shadowpixel.shadowcore.util.object.ObjectUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.data.DataHandler;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.util.CommandUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.shadowpixel.shadowlevels.util.CommandUtils.findLevel;
import static top.shadowpixel.shadowlevels.util.CommandUtils.sendCommandMessage;

public class ResetCommand extends SubCommand {

    private final ShadowLevels plugin;

    public ResetCommand() {
        super("Reset", "ShadowLevels.Commands.Reset");
        plugin = ShadowLevels.getInstance();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (!CommandUtils.checkArgument(sender, arguments, 2)) {
            return;
        }

        var player = Bukkit.getPlayer(arguments[0]);
        if (arguments[1].equals("*")) {
            if (player == null) {
                DataHandler.reset(sender, arguments[0], "*");
                return;
            }

            LevelManager.getInstance().resetAll(player);
            return;
        }

        Optional.of(findLevel(sender, arguments[1])).run(level -> {
            if (player == null) {
                DataHandler.reset(sender, arguments[0], arguments[1]);
                return;
            }

            level.getLevelData(player).reset();

            if (Boolean.parseBoolean(ObjectUtils.getOrElse(arguments, 2, "true"))) {
                sendCommandMessage(sender, "Success.Reset",
                        "%player%", arguments[0],
                        "%level-system%", arguments[1]);
            }
        });
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        switch (arguments.length) {
            case 1:
                return null;
            case 2:
                var list = new ArrayList<String>();
                list.add("*");
                list.addAll(LevelManager.getInstance().getLoadedLevels().keySet());
                return list;
        }

        return Collections.emptyList();
    }
}
