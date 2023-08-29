package top.shadowpixel.shadowlevels.command;

import lombok.var;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.SubCommand;
import top.shadowpixel.shadowcore.util.entity.PlayerUtils;
import top.shadowpixel.shadowcore.util.entity.SenderUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.api.ShadowLevelsAPI;
import top.shadowpixel.shadowlevels.level.LevelData;
import top.shadowpixel.shadowlevels.util.CommandUtils;
import top.shadowpixel.shadowlevels.util.LocaleUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatsOfflineCommand extends SubCommand {
    public StatsOfflineCommand() {
        super("StatsOffline", "ShadowLevels.Commands.StatsOffline");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (CommandUtils.checkArgument(sender, arguments, 2)) {
            var level = CommandUtils.findLevel(sender, arguments[0]);
            if (level == null) {
                return;
            }

            if (PlayerUtils.isOnline(arguments[1])) {
                send("&cThis player is online!");
                return;
            }

            LevelData data = level.getOfflineData(arguments[1]);
            if (data == null) {
                send(LocaleUtils.getCmdMessage(sender, "Errors.Player-Not-Found",
                        "%name%", arguments[1]));
                return;
            }

            SenderUtils.sendMessage(sender,
                    LocaleUtils.getMessage(sender).getStringList("Messages.Command-Messages.Success.Stats-Message." + level.getName()),
                    "{levels}", String.valueOf(data.getLevels()),
                    "{exps}", String.valueOf(data.getExps()),
                    "{next-levels}", String.valueOf(data.getLevels() + 1),
                    "{required-exps}", "&cFAILED",
                    "{percentage}", "&cFAILED",
                    "{progressbar}", "&cFAILED");

            send("&aRewards the player has received:");
            for (var entry : data.getReceivedRewards().entrySet()) {
                var name = entry.getKey();
                var reward = entry.getValue();
                var list = ShadowLevelsAPI.getRewardList(name);
                if (list == null) {
                    continue;
                }

                send(name + "&7[&f" + reward.size() + "&7/&f" + list.getRewards().size() + "&7]&f " + String.join(", ", reward));
            }
        }
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] arguments) {
        if (arguments.length == 1) {
            return new ArrayList<>(ShadowLevels.getInstance().getLevelManager().getLoadedLevels().keySet());
        }

        return Collections.emptyList();
    }
}
