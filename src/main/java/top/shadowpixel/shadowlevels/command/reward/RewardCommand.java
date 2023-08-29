package top.shadowpixel.shadowlevels.command.reward;

import lombok.var;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.shadowpixel.shadowcore.api.command.subcommand.SubCommand;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.reward.RewardManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.shadowpixel.shadowlevels.util.CommandUtils.checkArgument;
import static top.shadowpixel.shadowlevels.util.CommandUtils.sendCommandMessage;

public class RewardCommand extends SubCommand {

    private final ShadowLevels plugin;

    public RewardCommand() {
        super("Reward");
        plugin = ShadowLevels.getInstance();
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (checkArgument(sender, arguments, 1)) {
            var rewardList = plugin.getRewardList(arguments[0]);
            if (rewardList == null) {
                sendCommandMessage(sender, "Errors.Reward-Not-Found");
                return;
            }

            RewardManager.getInstance().getRewardMenu((Player) sender, rewardList).openMenu();
        }
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String label, String[] arguments) {
        if (arguments.length == 1) {
            return new ArrayList<>(ShadowLevels.getInstance().getRewardManager().getLoadedRewards().keySet());
        }

        return Collections.emptyList();
    }
}
