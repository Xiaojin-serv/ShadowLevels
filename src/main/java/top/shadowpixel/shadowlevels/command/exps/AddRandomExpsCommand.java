package top.shadowpixel.shadowlevels.command.exps;

import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.SubCommand;
import top.shadowpixel.shadowcore.api.util.Optional;
import top.shadowpixel.shadowcore.util.MathUtils;
import top.shadowpixel.shadowcore.util.collection.ListUtils;
import top.shadowpixel.shadowcore.util.object.ObjectUtils;
import top.shadowpixel.shadowlevels.data.DataHandler;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.object.enums.ModificationType;
import top.shadowpixel.shadowlevels.util.CommandUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.shadowpixel.shadowlevels.util.CommandUtils.*;

public class AddRandomExpsCommand extends SubCommand {

    public AddRandomExpsCommand() {
        super(ListUtils.immutableList("AddRandomExps", "AddRandomExp"),
                "ShadowLevels.Commands.AddRandomExps");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (!CommandUtils.checkArgument(sender, arguments, 4)) {
            return;
        }

        var player = Bukkit.getPlayer(arguments[0]);
        Optional.of(findLevel(sender, arguments[1])).run(level -> {
            var min = findInt(sender, arguments[2], 2);
            var max = findInt(sender, arguments[3], 3);

            if (min == null || max == null) {
                return;
            }

            if (min > max) {
                var a = max;
                max = min;
                min = a;
            }

            var exps = MathUtils.randomMath(min, max).intValue();
            if (player == null) {
                DataHandler.modifyExps(sender, arguments[0], arguments[1], ModificationType.ADD, exps);
                return;
            }

            level.getLevelData(player).addExps(exps);
            if (Boolean.parseBoolean(ObjectUtils.getOrElse(arguments, 4, "true"))) {
                sendCommandMessage(sender, "Success." + "Add-Exps",
                        "%player%", arguments[0],
                        "%level-system%", arguments[1],
                        "%amount%", arguments[1]);
            }
        });
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
