package top.shadowpixel.shadowlevels.command.multiple;

import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.SubCommand;
import top.shadowpixel.shadowcore.api.util.Optional;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowcore.util.object.ObjectUtils;
import top.shadowpixel.shadowlevels.data.DataHandler;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.util.CommandUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.shadowpixel.shadowlevels.util.CommandUtils.*;

public class SetMultipleCommand extends SubCommand {

    public SetMultipleCommand() {
        super(ListUtils.immutableList("SetMultiple", "SetMultiples"),
                "ShadowLevels.Commands.SetMultiple");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (!CommandUtils.checkArgument(sender, arguments, 3)) {
            return;
        }

        var player = Bukkit.getPlayer(arguments[0]);
        Optional.of(findLevel(sender, arguments[1])).run(level ->
                Optional.of(findDouble(sender, arguments[2], 3)).run(value -> {
                    if (player == null) {
                        DataHandler.modifyMultiple(sender, arguments[0], arguments[1], value.floatValue());
                        return;
                    }

                    level.getLevelData(player).setMultiple(value.floatValue());
                    if (Boolean.parseBoolean(ObjectUtils.getOrElse(arguments, 3, "true"))) {
                        sendCommandMessage(sender, "Success." + "Set-Multiple",
                                "%player%", arguments[0],
                                "%level-system%", arguments[1],
                                "%multiple%", arguments[2],
                                "%amount%", arguments[2]);
                    }
                })
        );
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        switch (arguments.length) {
            case 1:
                return null;
            case 2:
                return new ArrayList<>(LevelManager.getInstance().getLoadedLevels().keySet());
        }

        return Collections.emptyList();
    }
}
