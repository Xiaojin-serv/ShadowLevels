package top.shadowpixel.shadowlevels.util;

import lombok.var;
import org.jetbrains.annotations.NotNull;
import top.shadowpixel.shadowcore.api.command_v2.component.CommandArgument;
import top.shadowpixel.shadowcore.api.exception.command.ParameterizedCommandInterruptedException;
import top.shadowpixel.shadowlevels.ShadowLevels;
import top.shadowpixel.shadowlevels.level.Level;
import top.shadowpixel.shadowlevels.level.LevelManager;
import top.shadowpixel.shadowlevels.reward.RewardList;
import top.shadowpixel.shadowlevels.reward.RewardManager;

public class CommandUtils {

    public static final ShadowLevels plugin = ShadowLevels.getInstance();

    private CommandUtils() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Level getLevel(@NotNull CommandArgument argument) {
        var level = LevelManager.getInstance().getLevelSystem(argument.getValue());
        if (level == null) {
            throw new ParameterizedCommandInterruptedException(argument, "level not found");
        }

        return level;
    }

    @NotNull
    public static RewardList getRewardList(@NotNull CommandArgument argument) {
        var reward = RewardManager.getInstance().getRewardList(argument.getValue());
        if (reward == null) {
            throw new ParameterizedCommandInterruptedException(argument, "reward not found");
        }

        return reward;
    }

    public static void checkValidNumber(@NotNull CommandArgument argument) {
        if (!Utils.isValidNumber(argument)) {
            throw new ParameterizedCommandInterruptedException(argument, "not double");
        }
    }
}
