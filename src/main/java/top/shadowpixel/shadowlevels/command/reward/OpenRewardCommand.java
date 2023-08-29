package top.shadowpixel.shadowlevels.command.reward;

import lombok.var;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.SubCommand;
import top.shadowpixel.shadowcore.util.object.ObjectUtils;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.reward.RewardManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.shadowpixel.shadowlevels.util.CommandUtils.*;

public class OpenRewardCommand extends SubCommand {

    public OpenRewardCommand() {
        super("OpenReward", "ShadowLevels.Commands.OpenReward");
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (!checkArgument(sender, arguments, 2)) {
            return;
        }

        var player = findPlayer(sender, arguments[0]);
        if (player == null) {
            return;
        }

        var rewardList = plugin.getRewardList(arguments[1]);
        if (rewardList == null) {
            sendCommandMessage(sender, "Errors.Reward-Not-Found");
            return;
        }

        RewardManager.getInstance().getRewardMenu(player, rewardList).openMenu();

        if (Boolean.parseBoolean(ObjectUtils.getOrElse(arguments, 2, "true"))) {
            sendCommandMessage(sender, "Success.Open-Reward");
        }
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (arguments.length == 1) {
            return null;
        }

        if (arguments.length == 2) {
            return new ArrayList<>(ShadowLevels.getInstance().getRewardManager().getLoadedRewards().keySet());
        }

        return Collections.emptyList();
    }
}
