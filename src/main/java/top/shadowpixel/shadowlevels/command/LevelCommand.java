package top.shadowpixel.shadowlevels.command;

import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.SubCommand;
import top.shadowpixel.shadowcore.api.util.Optional;
import top.shadowpixel.shadowcore.util.object.ObjectUtils;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.util.CommandUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static top.shadowpixel.shadowlevels.util.CommandUtils.*;

@SuppressWarnings("unused")
public abstract class LevelCommand extends SubCommand {
    protected String path;

    public LevelCommand(String alias, String... permissions) {
        super(alias, permissions);
    }

    public LevelCommand(Collection<String> aliases, String... permission) {
        super(aliases, permission);
    }

    public LevelCommand(Collection<String> aliases, Collection<String> permissions) {
        super(aliases, permissions);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (!CommandUtils.checkArgument(sender, arguments, 3)) {
            return;
        }

        var player = Bukkit.getPlayer(arguments[0]);
        Optional.of(findLevel(sender, arguments[1])).run(level -> {
            if (player == null) {
                executeOffline(sender, label, arguments);
                return;
            }

            var levels = 0;
            if (arguments[2].contains("%")) {
                //Percentage
                levels = calcPercentage(sender, arguments, level, player);
            } else {
                var i = findInt(sender, arguments[2], 2);
                if (i == null) {
                    return;
                }

                levels = i;
            }

            if (levels < 0) {
                return;
            }

            run(player, level, levels);
            if (Boolean.parseBoolean(ObjectUtils.getOrElse(arguments, 3, "true"))) {
                sendCommandMessage(sender, "Success." + path,
                        "%player%", arguments[0],
                        "%level-system%", arguments[1],
                        "%multiple%", arguments[1],
                        "%amount%", String.valueOf(levels));
            }
        });
    }

    private int calcPercentage(@NotNull CommandSender sender, String[] arguments, @NotNull Level level, Player player) {
        var perc = findDouble(sender, arguments[2].substring(0, arguments[2].indexOf("%")), 2);
        var type = aliases.iterator().next();
        var amount = -1;

        if (perc == null) {
            return -1;
        }

        var data = level.getLevelData(player);
        if (data == null) {
            data = level.getOfflineData(arguments[0]);
        }

        if (type.contains("Level")) {
            amount = data.getLevels();
        }

        if (type.contains("Exp")) {
            amount = data.getExps();
        }

        return (int) (amount * perc * 0.01);
    }

    public abstract void run(@NotNull Player player, @NotNull Level level, int value);

    public abstract void executeOffline(CommandSender sender, String label, String[] arguments);

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
