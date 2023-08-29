package top.shadowpixel.shadowlevels.command;

import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.SubCommand;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.util.CommandUtils;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsCommand extends SubCommand {

    public StatsCommand() {
        super("Stats");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (CommandUtils.checkArgument(sender, arguments, 1)) {
            var level = CommandUtils.findLevel(sender, arguments[0]);
            if (level == null) {
                return;
            }

            LevelData data = null;
            if (arguments.length > 1 && sender.hasPermission("ShadowLevels.Commands.Stats.Others")) {
                var player = Bukkit.getPlayer(arguments[1]);
                if (player == null) {
                    send(LocaleUtils.getCmdMessage(sender, "Errors.Player-Not-Found"));
                } else {
                    data = level.getLevelData(player);
                }
            } else if (sender instanceof Player) {
                data = level.getLevelData((Player) sender);
            }

            if (data == null) {
                send(LocaleUtils.getCmdMessage(sender, "Errors.Only-For-Player"));
                return;
            }

            SenderUtils.sendMessage(sender,
                    LocaleUtils.getMessage(sender).getStringList("Messages.Command-Messages.Success.Stats-Message." + level.getName()),
                    "{levels}", String.valueOf(data.getLevels()),
                    "{exps}", String.valueOf(data.getExps()),
                    "{next-levels}", String.valueOf(data.getLevels() + 1),
                    "{required-exps}", String.valueOf(data.getRequiredExps()),
                    "{percentage}", String.valueOf(data.getPercentage()),
                    "{progressbar}", data.getProgressBar());
        }
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (arguments.length == 1) {
            return new ArrayList<>(ShadowLevels.getInstance().getLevelManager().getLoadedLevels().keySet());
        }

        return Collections.emptyList();
    }
}
